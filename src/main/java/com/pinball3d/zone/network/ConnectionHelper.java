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
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.SerialNumber;
import com.pinball3d.zone.sphinx.SphinxUtil;
import com.pinball3d.zone.sphinx.log.Log;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEBeaconCore;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

	public static void requestControllerConnect(UUID uuid, WorldPos controller, Type... types) {
		if (types.length == 0) {
			pool.remove(uuid);
			return;
		}
		EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
				.getPlayerByUUID(uuid);
		if (controller.isAreaLoaded(5) && controller.getBlockState().getBlock() == BlockLoader.controller_mainframe) {
			WorldPos center = BlockControllerMainframe.getProcessingCenterPos(controller);
			if (!center.isOrigin()) {
				TEProcessingCenter te = (TEProcessingCenter) center.getTileEntity();
				Connect connect = new Connect(player, te.getUUID(), WorldPos.ORIGIN, ConnectType.CONTROLLER, types);
				if (connect.isValid()) {
					pool.put(uuid, connect);
				}
			}
		}
	}

	public static void requestTerminalConnect(UUID uuid, Type... types) {
		if (types.length == 0) {
			pool.remove(uuid);
			return;
		}
		EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
				.getPlayerByUUID(uuid);
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() != ItemLoader.terminal) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() == ItemLoader.terminal && stack.getItemDamage() == 0) {
			WorldPos pos = SphinxUtil.getNetworkPosFromTerminal(player);
			if (!pos.isOrigin()) {
				TileEntity tileentity = pos.getTileEntity();
				if (tileentity instanceof TEProcessingCenter) {
					TEProcessingCenter te = ((TEProcessingCenter) tileentity);
					Connect connect;
					if (te.isPointInRange(player.dimension, player.posX, player.posY, player.posZ)
							&& te.getWorkingState() == WorkingState.WORKING) {
						connect = new Connect(player, te.getUUID(), WorldPos.ORIGIN, ConnectType.TERMINAL, types);
					} else {
						connect = new Connect(player, null, WorldPos.ORIGIN, ConnectType.TERMINAL, types);
					}
					if (connect.isValid()) {
						pool.put(uuid, connect);
						return;
					}
				}
			}
			Connect connect = new Connect(player, null, WorldPos.ORIGIN, ConnectType.TERMINAL, types);
			if (connect.isValid()) {
				pool.put(uuid, connect);
				return;
			}
		}
	}

	public static void requestNeedNetworkConnect(UUID uuid, WorldPos needNetwork, Type... types) {
		if (types.length == 0) {
			pool.remove(uuid);
			return;
		}
		EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
				.getPlayerByUUID(uuid);
		TileEntity tileentity = needNetwork.getTileEntity();
		if (tileentity instanceof INeedNetwork) {
			INeedNetwork te = (INeedNetwork) tileentity;
			UUID network = te.getNetwork();
			if (network != null) {
				WorldPos pos = GlobalNetworkData.getPos(network);
				if (!pos.isOrigin()) {
					tileentity = pos.getTileEntity();
					if (tileentity instanceof TEProcessingCenter) {
						TEProcessingCenter pc = ((TEProcessingCenter) tileentity);
						Connect connect = new Connect(player, pc.getUUID(), needNetwork, ConnectType.NEEDNETWORK,
								types);
						if (connect.isValid()) {
							pool.put(uuid, connect);
							return;
						}
					}
				}
			}
		}
		Connect connect = new Connect(player, null, needNetwork, ConnectType.NEEDNETWORK, types);
		if (connect.isValid()) {
			pool.put(uuid, connect);
			return;
		}
	}

	public static Connect getConnect(UUID uuid) {
		return pool.get(uuid);
	}

	public static class Connect {
		public final UUID uuid;
		public UUID network;
		public WorldPos needNetwork;
		public Set<Type> reqDataType;
		public ConnectType connectType;
		public int mapRefreshColddown, packRefreshColddown, itemRefreshColddown, classifyRefreshColddown, logUpdateRate;

		private Connect(EntityPlayer player, UUID network, WorldPos needNetwork, ConnectType connectType,
				Type... types) {
			uuid = player.getUniqueID();
			this.network = network;
			this.needNetwork = needNetwork;
			this.connectType = connectType;
			reqDataType = new HashSet<Type>(Arrays.asList(types));
		}

		public boolean isValid() {
			EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
					.getPlayerByUUID(uuid);
			if (player == null) {
				return false;
			}
			switch (connectType) {
			case CONTROLLER:
				if (network != null) {
					WorldPos pos = GlobalNetworkData.getPos(network);
					if (pos.isOrigin()) {
						return false;
					}
					TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
					if (te.isAdmin(player)) {
						break;
					}
				}
				return false;
			case TERMINAL:
				network = SphinxUtil.getNetworkUUIDFromTerminal(player);
				if (network != null) {
					WorldPos pos = GlobalNetworkData.getPos(network);
					if (pos.isOrigin()) {
						network = null;
					} else {
						TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
						if (!te.isPointInRange(player.dimension, player.posX, player.posY, player.posZ)
								|| te.getWorkingState() != WorkingState.WORKING) {
							network = null;
						}
						if (!te.isUser(player)) {
							network = null;
						}
					}
				}
				break;
			case NEEDNETWORK:
				network = SphinxUtil.getNetworkUUIDFromNeedNetwork(needNetwork);
				if (network != null) {
					WorldPos pos = GlobalNetworkData.getPos(network);
					if (pos.isOrigin()) {
						network = null;
					} else {
						TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
						if (!te.isPointInRange(player.dimension, needNetwork.getPos().getX(),
								needNetwork.getPos().getY(), needNetwork.getPos().getZ())
								|| te.getWorkingState() != WorkingState.WORKING) {
							network = null;
						}
						if (!te.isUser(player)) {
							network = null;
						}
					}
				}
				break;
			}
			return true;
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

	public static enum ConnectType {
		CONTROLLER, TERMINAL, NEEDNETWORK;
	}

	public static enum Type {
		NETWORKUUID, ITEMS, NETWORKPOS, PLAYERVALIDNETWORK, MAP, PACK, NEEDNETWORKVALIDNETWORK,
		NETWORKUUIDFROMCONTROLLER, NAME, LOADTICK, ON, WORKINGSTATE, USEDSTORAGE, MAXSTORAGE, CLASSIFY, USERS, LOGS,
		NEEDNETWORKSERIAL, ENERGY;

		public void writeToNBT(NBTTagCompound tag, EntityPlayer player, Connect connect) {
			WorldPos pos = WorldPos.ORIGIN;
			TEProcessingCenter te = null;
			INeedNetwork needNetwork = null;
			if (connect.network != null) {
				pos = GlobalNetworkData.getPos(connect.network);
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
			case CLASSIFY:
				if (te != null) {
					if (connect.classifyRefreshColddown <= 0) {
						NBTTagList list = new NBTTagList();
						te.getClassifyGroups().forEach((k, v) -> {
							NBTTagCompound t = new NBTTagCompound();
							t.setInteger("id", k);
							t.setTag("group", v.writeToNBT(new NBTTagCompound()));
							list.appendTag(t);
						});
						tag.setTag(name(), list);
						connect.classifyRefreshColddown += ConfigLoader.classifyUpdateRate;
					}
					connect.classifyRefreshColddown--;
				}
				break;
			case USERS:
				if (te != null) {
					NBTTagList list = new NBTTagList();
					te.getUsers().values().forEach(e -> {
						e.checkOnline();
						list.appendTag(e.writeToNBT(new NBTTagCompound()));
					});
					tag.setTag(name(), list);
				}
				break;
			case LOGS:
				if (te != null) {
					if (connect.logUpdateRate <= 0) {
						NBTTagList list = new NBTTagList();
						for (Log e : te.getLogCache()) {
							e.check(te);
							list.appendTag(e.writeToNBT(new NBTTagCompound()));
						}
						tag.setTag(name(), list);
						connect.logUpdateRate += ConfigLoader.logUpdateRate;
					}
					connect.logUpdateRate--;
				}
				break;
			case NEEDNETWORKSERIAL:
				if (te != null && needNetwork != null) {
					SerialNumber serial = te.getSerialNumberFromPos(connect.needNetwork);
					if (serial != null) {
						serial.check(te);
						tag.setTag(name(), serial.writeToNBT(new NBTTagCompound()));
					}
				}
				break;
			case ENERGY:
				if (te != null) {
					tag.setInteger(name(), te.getEnergy());
				}
			}
		}
	}
}
