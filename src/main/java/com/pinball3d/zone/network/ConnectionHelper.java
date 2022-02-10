package com.pinball3d.zone.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.tileentity.TETerminal;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
			System.out.println(c);
		}
	}

	public static void requestTerminalConnect(EntityPlayer player, WorldPos terminal, UUID network, Type... types) {
		if (types.length == 0) {
			pool.remove(player.getUniqueID());
			return;
		}
		WorldPos pos = GlobalNetworkData.getPos(network);
		if (!pos.isOrigin()) {
			TileEntity tileentity = pos.getTileEntity();
			if (tileentity instanceof TEProcessingCenter) {
				TEProcessingCenter pc = ((TEProcessingCenter) tileentity);
				Connect connect = new Connect(player, pc.getUUID(), terminal, types);
				if (connect.isValid()) {
					pool.put(player.getUniqueID(), connect);
					return;
				}
			}
		}
	}

	public static Connect getConnect(UUID uuid) {
		return pool.get(uuid);
	}

	public static class Connect {
		public final UUID uuid;
		public UUID network;
		public WorldPos terminal;
		public Set<Type> reqDataType;
		public int mapRefreshColddown, packRefreshColddown, itemRefreshColddown, classifyRefreshColddown,
				logRefreshColddown, oreDictionaryRefreshColddown;

		private Connect(EntityPlayer player, UUID network, WorldPos terminal, Type... types) {
			uuid = player.getUniqueID();
			this.network = network;
			this.terminal = terminal;
			reqDataType = new HashSet<Type>(Arrays.asList(types));
		}

		public boolean isValid() {
			EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
					.getPlayerByUUID(uuid);
			if (player == null || network == null) {
				return false;
			}
			WorldPos pos = GlobalNetworkData.getPos(network);
			if (pos.isOrigin()) {
				network = null;
				return false;
			} else {
				TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
				TileEntity tileentity = terminal.getTileEntity();
				if (!te.isPointInRange(terminal.getDim(), terminal.getPos().getX(), terminal.getPos().getY(),
						terminal.getPos().getZ()) || te.getWorkingState() != WorkingState.WORKING
						|| !te.isUser(player) && tileentity instanceof TETerminal
								&& ((TETerminal) tileentity).getPlayerUuid().equals(uuid)) {
					network = null;
					return false;
				}
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

	public static enum Type {
		NETWORKUUID, ITEMS, NETWORKPOS, MAP, PACK, NAME, ON, WORKINGSTATE, USEDSTORAGE, MAXSTORAGE, CLASSIFY, USERS,
		LOGS, ENERGY, OREDICTIONARY;

		public void writeToNBT(NBTTagCompound tag, EntityPlayer player, Connect connect) {
			WorldPos pos = WorldPos.ORIGIN;
			TEProcessingCenter te = null;
			INeedNetwork needNetwork = null;
			if (connect.network != null) {
				pos = GlobalNetworkData.getPos(connect.network);
				te = (TEProcessingCenter) pos.getTileEntity();
			}
			if (!connect.terminal.isOrigin()) {
				TileEntity tileentity = connect.terminal.getTileEntity();
				if (tileentity instanceof INeedNetwork) {
					needNetwork = (INeedNetwork) tileentity;// FIXME terminal
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
				if (te != null && connect.itemRefreshColddown <= 0) {
					tag.setTag(name(), te.getNetworkUseableItems().writeToNBT(new NBTTagCompound()));
					connect.itemRefreshColddown += ConfigLoader.itemUpdateRate;
				}
				connect.itemRefreshColddown = connect.itemRefreshColddown - 1 < 0 ? 0 : connect.itemRefreshColddown - 1;
				break;
			case NETWORKPOS:
				tag.setTag(name(), pos.writeToNBT(new NBTTagCompound()));
				break;
			case MAP:
				if (te != null && connect.mapRefreshColddown <= 0) {
					tag.setTag(name(), te.genMapData(player, new NBTTagCompound()));
					connect.mapRefreshColddown += ConfigLoader.mapUpdateRate;
				}
				connect.mapRefreshColddown = connect.mapRefreshColddown - 1 < 0 ? 0 : connect.mapRefreshColddown - 1;
				break;
			case PACK:
				if (te != null && connect.packRefreshColddown <= 0) {
					tag.setTag(name(), te.genPackData(player, new NBTTagCompound()));
					connect.packRefreshColddown += ConfigLoader.packUpdateRate;
				}
				connect.packRefreshColddown = connect.packRefreshColddown - 1 < 0 ? 0 : connect.packRefreshColddown - 1;
				break;
			case NAME:
				if (te != null) {
					tag.setString(name(), te.getName());
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
				if (te != null && connect.classifyRefreshColddown <= 0) {
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
				connect.classifyRefreshColddown = connect.classifyRefreshColddown - 1 < 0 ? 0
						: connect.classifyRefreshColddown - 1;
				break;
			case USERS:
				if (te != null) {
					NBTTagList list = new NBTTagList();
					te.getUsers().values().forEach(e -> {
						e.updateData();
						list.appendTag(e.writeToNBT(new NBTTagCompound()));
					});
					tag.setTag(name(), list);
				}
				break;
			case LOGS:
				if (te != null && connect.logRefreshColddown <= 0) {
					NBTTagList list = new NBTTagList();
//					for (Log e : te.getLogCache()) {
//						e.check(te);
//						list.appendTag(e.writeToNBT(new NBTTagCompound()));
//					}//TODO
					tag.setTag(name(), list);
					connect.logRefreshColddown += ConfigLoader.logUpdateRate;
				}
				connect.logRefreshColddown = connect.logRefreshColddown - 1 < 0 ? 0 : connect.logRefreshColddown - 1;
				break;
			case ENERGY:
				if (te != null) {
					tag.setInteger(name(), te.getEnergy());
				}
				break;
			case OREDICTIONARY:
				if (te != null && connect.oreDictionaryRefreshColddown <= 0) {
					NBTTagList oreDictionaryList = new NBTTagList();
					te.getOreDictionarys().forEach((k, v) -> {
						NBTTagCompound t = new NBTTagCompound();
						t.setInteger("id", k);
						t.setTag("data", v.writeToNBT(new NBTTagCompound()));
						oreDictionaryList.appendTag(t);
					});
					tag.setTag(name(), oreDictionaryList);
					connect.oreDictionaryRefreshColddown += ConfigLoader.oreDictionaryUpdateRate;
				}
				connect.oreDictionaryRefreshColddown = connect.oreDictionaryRefreshColddown - 1 < 0 ? 0
						: connect.oreDictionaryRefreshColddown - 1;
				break;
			}
		}
	}
}
