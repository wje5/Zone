package com.pinball3d.zone.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.sphinx.WorldPos;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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
			}
			c.update();
		}
	}

	public static void refreshRequest(UUID uuid, UUID network, WorldPos needNetwork, Type... types) {
		pool.put(uuid,
				new Connect(
						FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid),
						network, needNetwork, types));
	}

	public static void disconnect(UUID uuid) {
		pool.remove(uuid);
	}

	public static class Connect {
		private final UUID uuid, network;
		private WorldPos needNetwork;
		private Set<Type> reqDataType;

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
			reqDataType.forEach(e -> {
				e.writeToNBT(data, network, needNetwork);
			});
			NetworkHandler.instance.sendTo(new MessageConnectionUpdate(network, data),
					FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid));
		}
	}

	public static enum Type {
		ITEMS, ISCONNECTED, NETWORKPOS;

		public void writeToNBT(NBTTagCompound tag, UUID network, WorldPos needNetworkPos) {
			WorldPos pos = null;
			TEProcessingCenter te = null;
			INeedNetwork needNetwork = null;
			if (network != null) {
				pos = GlobalNetworkData.getData(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0))
						.getNetwork(network);
				te = (TEProcessingCenter) pos.getTileEntity();
			}
			if (needNetworkPos != null) {
				needNetwork = (INeedNetwork) needNetworkPos.getTileEntity();
			}
			switch (this) {
			case ITEMS:
				tag.setTag(name(), te.getNetworkUseableItems().writeToNBT(new NBTTagCompound()));
				break;
			case ISCONNECTED:
				tag.setBoolean(name(), needNetwork.isConnected() && network.equals(needNetwork.getNetwork()));
				break;
			case NETWORKPOS:
				tag.setTag(name(), pos.writeToNBT(new NBTTagCompound()));
				break;
			}
		}
	}
}
