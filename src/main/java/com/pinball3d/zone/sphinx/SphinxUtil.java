package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SphinxUtil {
	public static List<WorldPos> getValidNetworks(int dim, double x, double y, double z) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
		GlobalNetworkData data = GlobalNetworkData.getData(world);
		Map<UUID, WorldPos> map = data.getNetworks();
		List<WorldPos> list = new ArrayList<WorldPos>();
		map.forEach((uuid, pos) -> {
			TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
			if (te.getWorkingState() == WorkingState.WORKING && (te.isPointInRange(dim, x, y, z))) {
				list.add(pos);
			}
		});
		Collections.sort(list, new Comparator<WorldPos>() {
			@Override
			public int compare(WorldPos o1, WorldPos o2) {
				return Math.sqrt(o1.getPos().distanceSq(x, y, z)) > Math.sqrt(o2.getPos().distanceSq(x, y, z)) ? 1 : -1;
			}
		});
		return list;
	}

	public static List<WorldPos> getValidNetworksWithoutRange() {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
		GlobalNetworkData data = GlobalNetworkData.getData(world);
		Map<UUID, WorldPos> map = data.getNetworks();
		List<WorldPos> list = new ArrayList<WorldPos>();
		map.forEach((uuid, pos) -> {
			TEProcessingCenter te = (TEProcessingCenter) pos.getTileEntity();
			if (te.getWorkingState() == WorkingState.WORKING) {
				list.add(pos);
			}
		});
		Collections.sort(list, new Comparator<WorldPos>() {
			@Override
			public int compare(WorldPos o1, WorldPos o2) {
				return o1.compare(o2);
			}
		});
		return list;
	}

	public static NBTTagList getValidNetworkData(WorldPos pos, EntityPlayer player, boolean isPlayer) {
		List<WorldPos> list = SphinxUtil.getValidNetworks(pos.getDim(), pos.getPos().getX(), pos.getPos().getY(),
				pos.getPos().getZ());
		NBTTagList taglist = new NBTTagList();
		list.forEach(e -> {
			TEProcessingCenter te = (TEProcessingCenter) e.getTileEntity();
			NBTTagCompound t = new NBTTagCompound();
			t.setString("name", te.getName());
			e.writeToNBT(t);
			taglist.appendTag(t);
		});
		return taglist;
	}

	public static NBTTagList getValidNetworkDataWithoutRange(WorldPos pos, EntityPlayer player, boolean isPlayer) {
		List<WorldPos> list = SphinxUtil.getValidNetworksWithoutRange();
		NBTTagList taglist = new NBTTagList();
		list.forEach(e -> {
			TEProcessingCenter te = (TEProcessingCenter) e.getTileEntity();
			NBTTagCompound t = new NBTTagCompound();
			t.setString("name", te.getName());
			e.writeToNBT(t);
			taglist.appendTag(t);
		});
		return taglist;
	}

	public static WorldPos getNetworkPosFromTerminal(EntityPlayer player) {
		UUID uuid = getNetworkUUIDFromTerminal(player);
		if (uuid != null) {
			return GlobalNetworkData.getPos(uuid);
		}
		return WorldPos.ORIGIN;
	}

	public static UUID getNetworkUUIDFromTerminal(EntityPlayer player) {
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() != ItemLoader.terminal) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() == ItemLoader.terminal && stack.getItemDamage() == 0) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && tag.hasUniqueId("network")) {
				return tag.getUniqueId("network");
			}
		}
		return null;
	}

	public static UUID getNetworkUUIDFromNeedNetwork(WorldPos pos) {
		TileEntity te = pos.getTileEntity();
		if (te instanceof INeedNetwork) {
			return ((INeedNetwork) te).getNetwork();
		}
		return null;
	}
}
