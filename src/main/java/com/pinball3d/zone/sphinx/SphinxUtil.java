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
			if (te.isOn() && !te.needInit() && te.isPointInRange(dim, x, y, z)) {
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

	public static NBTTagCompound getValidNetworkData(WorldPos pos, EntityPlayer player, boolean isPlayer) {
		List<WorldPos> list = SphinxUtil.getValidNetworks(player.dimension, pos.getPos().getX(), pos.getPos().getY(),
				pos.getPos().getZ());
		NBTTagCompound tag = new NBTTagCompound();
		if (!isPlayer) {
			TileEntity tileentity = pos.getTileEntity();
			if (tileentity instanceof INeedNetwork) {
				WorldPos network = ((INeedNetwork) tileentity).getNetworkPos();
				if (network != null) {
					tag.setTag("connected", network.writeToNBT(new NBTTagCompound()));
				}
			}
		} else {
			ItemStack stack = player.getHeldItemMainhand();
			if (stack.getItem() != ItemLoader.terminal) {
				stack = player.getHeldItemOffhand();
			}
			if (stack.getItem() == ItemLoader.terminal) {
				NBTTagCompound t = stack.getTagCompound();
				if (t == null) {
					t = new NBTTagCompound();
					stack.setTagCompound(t);
				}
				if (t.hasUniqueId("network")) {
					UUID uuid = t.getUniqueId("network");
					GlobalNetworkData data = GlobalNetworkData.getData(pos.getWorld());
					WorldPos network = data.getNetwork(uuid);
					if (network != null && list.contains(network)) {
						tag.setTag("connected", network.writeToNBT(new NBTTagCompound()));
					} else {
						t.removeTag("networkMost");
						t.removeTag("networkLeast");
						t.removeTag("password");
					}
				}
			}
		}
		NBTTagList taglist = new NBTTagList();
		list.forEach(e -> {
			TEProcessingCenter te = (TEProcessingCenter) e.getTileEntity();
			NBTTagCompound t = new NBTTagCompound();
			t.setString("name", te.getName());
			e.writeToNBT(t);
			taglist.appendTag(t);
		});
		tag.setTag("list", taglist);
		return tag;
	}
}
