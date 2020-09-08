package com.pinball3d.zone.item;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.tileentity.TECastingTable;
import com.pinball3d.zone.tileentity.TECrucible;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCrucibleSpoon extends Item {
	private boolean filled;

	public ItemCrucibleSpoon(boolean filled) {
		setUnlocalizedName("crucible_spoon");
		setRegistryName("zone:crucible_spoon" + (filled ? "_filled" : ""));
		setCreativeTab(TabZone.tab);
		setMaxStackSize(1);
		this.filled = filled;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (filled) {
				if (worldIn.getBlockState(pos).getBlock() == BlockLoader.casting_table) {
					TECastingTable te = (TECastingTable) worldIn.getTileEntity(pos);
					if (te != null) {
						if (te.tryFill()) {
							player.setHeldItem(hand, new ItemStack(ItemLoader.crucible_spoon));
							return EnumActionResult.SUCCESS;
						}
					}
				}
			} else if (worldIn.getBlockState(pos).getBlock() == BlockLoader.crucible) {
				TECrucible te = (TECrucible) worldIn.getTileEntity(pos);
				if (te != null && te.amount > 0) {
					te.amount--;
					player.setHeldItem(hand, new ItemStack(ItemLoader.crucible_spoon_filled));
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}