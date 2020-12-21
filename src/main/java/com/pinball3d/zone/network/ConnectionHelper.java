package com.pinball3d.zone.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.block.BlockControllerMainframe;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEBeaconCore;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@Mod.EventBusSubscriber
public class ConnectionHelper {
	private static Map<UUID, Connect> pool = new HashMap<UUID, Connect>();

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if (event.phase == Phase.END) {
			update();
		}
	}

	public static void update() {
		Iterator<Connect> it = pool.values().iterator();
		while (it.hasNext()) {
			Connect c = it.next();
			if (!c.isValid()) {
				it.remove();
				continue;
			}
			c.update();
		}
	}

	public static void refreshRequest(UUID uuid, UUID network, WorldPos needNetwork, Type... types) {
		if (types.length == 0) {
			pool.remove(uuid);
			return;
		}
		pool.put(uuid,
				new Connect(
						FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid),
						network, needNetwork, types));
	}

	public static Connect getConnect(UUID uuid) {
		return pool.get(uuid);
	}

	public static class Connect {
		public final UUID uuid;
		public UUID network;
		public WorldPos needNetwork;
		public Set<Type> reqDataType;
		public int mapRefreshColddown, packRefreshColddown, itemRefreshColddown;

		public Connect(EntityPlayer player, UUID network, WorldPos needNetwork, Type... types) {
			uuid = player.getUniqueID();
			this.network = network;
			this.needNetwork = needNetwork;
			reqDataType = new HashSet<Type>(Arrays.asList(types));
		}

		public boolean isValid() {
			return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
					.getPlayerByUUID(uuid) != null;
		}

		public void update() {
			NBTTagCompound data = new NBTTagCompound();
			EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
					.getPlayerByUUID(uuid);
			reqDataType.forEach(e -> {
				e.writeToNBT(data, player, this);
			});
			NetworkHandler.instance
					.sendTo(new MessageConnectionUpdate(network, data, reqDataType.toArray(new Type[] {})), player);
		}
	}

	public static enum Type {
		NETWORKUUID, ITEMS, NETWORKPOS, PASSWORD, ADMINPASSWORD, PLAYERVALIDNETWORK, MAP, PACK, NEEDNETWORKVALIDNETWORK,
		NETWORKUUIDFROMCONTROLLER, NAME, LOADTICK, ON, WORKINGSTATE, INITED, USEDSTORAGE, MAXSTORAGE;

		public void writeToNBT(NBTTagCompound tag, EntityPlayer player, Connect connect) {
			WorldPos pos = WorldPos.ORIGIN;
			TEProcessingCenter te = null;
			INeedNetwork needNetwork = null;
			if (connect.network != null) {
				pos = GlobalNetworkData.getData(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0))
						.getNetwork(connect.network);
				te = (TEProcessingCenter) pos.getTileEntity();
			}
			if (!connect.needNetwork.isOrigin()) {
				TileEntity tileentity = connect.needNetwork.getTileEntity();
				if (tileentity instanceof INeedNetwork) {
					needNetwork = (INeedNetwork) tileentity;
				}
			}
			switch (this) {
			case NETWORKUUID:
				tag.setUniqueId(name(),
						needNetwork != null
								? needNetwork.getNetwork() != null ? needNetwork.getNetwork() : new UUID(0, 0)
								: new UUID(0, 0));
				break;
			case ITEMS:
				if (te != null) {
					if (connect.itemRefreshColddown <= 0) {
						tag.setTag(name(), te.getNetworkUseableItems().writeToNBT(new NBTTagCompound()));
						connect.itemRefreshColddown += ConfigLoader.itemUpdateRate;
					}
					connect.itemRefreshColddown--;
				}
				break;
			case NETWORKPOS:
				tag.setTag(name(), pos.writeToNBT(new NBTTagCompound()));
				break;
			case PASSWORD:
				if (te != null) {
					tag.setString(name(), te.getPassword());
				}
				break;
			case ADMINPASSWORD:
				if (te != null) {
					tag.setString(name(), te.getAdminPassword());
				}
				break;
			case PLAYERVALIDNETWORK:
				tag.setTag(name(), SphinxUtil.getValidNetworkData(new WorldPos(player), player, true));
				break;
			case MAP:
				if (te != null) {
					if (connect.mapRefreshColddown <= 0) {
						tag.setTag(name(), te.genMapData(player, new NBTTagCompound()));
						connect.mapRefreshColddown += ConfigLoader.mapUpdateRate;
					}
					connect.mapRefreshColddown--;
				}
				break;
			case PACK:
				if (te != null) {
					if (connect.packRefreshColddown <= 0) {
						tag.setTag(name(), te.genPackData(player, new NBTTagCompound()));
						connect.packRefreshColddown += ConfigLoader.packUpdateRate;
					}
					connect.packRefreshColddown--;
				}
				break;
			case NEEDNETWORKVALIDNETWORK:
				if (needNetwork != null) {
					if (needNetwork instanceof TEBeaconCore) {
						tag.setTag(name(),
								SphinxUtil.getValidNetworkDataWithoutRange(connect.needNetwork, player, false));
					} else {
						tag.setTag(name(), SphinxUtil.getValidNetworkData(connect.needNetwork, player, false));
					}
				}
				break;
			case NETWORKUUIDFROMCONTROLLER:
				if (!connect.needNetwork.isOrigin()) {
					WorldPos p = BlockControllerMainframe.getProcessingCenterPos(connect.needNetwork);
					if (!p.isOrigin()) {
						tag.setUniqueId(name(), ((TEProcessingCenter) p.getTileEntity()).getUUID());
					}
				}
			case NAME:
				if (te != null) {
					tag.setString(name(), te.getName());
				}
				break;
			case LOADTICK:
				if (te != null) {
					tag.setInteger(name(), te.getLoadTick());
				}
				break;
			case ON:
				if (te != null) {
					tag.setBoolean(name(), te.isOn());
				}
				break;
			case WORKINGSTATE:
				if (te != null) {
					tag.setInteger(name(), te.getWorkingState().ordinal() + 1);
				}
				break;
			case INITED:
				if (te != null) {
					tag.setBoolean(name(), !te.needInit());
				}
				break;
			case USEDSTORAGE:
				if (te != null) {
					tag.setInteger(name(), te.getUsedStorage());
				}
				break;
			case MAXSTORAGE:
				if (te != null) {
					tag.setInteger(name(), te.getMaxStorage());
				}
				break;
			}
		}
	}
}
