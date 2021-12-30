package com.pinball3d.zone.item;

import com.pinball3d.zone.FluidHandler;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemPipette extends ZoneItem {
	public ItemPipette() {
		super("pipette");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn) {
		boolean flag = true;
		ItemStack stack = playerIn.getHeldItem(handIn);
		RayTraceResult result = rayTrace(world, playerIn, flag);

		if (result == null) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		} else if (result.typeOfHit != RayTraceResult.Type.BLOCK) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		}

		BlockPos pos = result.getBlockPos();
		if (!world.isBlockModifiable(playerIn, pos)) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		} else if (!playerIn.canPlayerEdit(pos.offset(result.sideHit), result.sideHit, stack)) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}

		IBlockState blockstate = world.getBlockState(pos);
		Material material = blockstate.getMaterial();

		if (material == Material.WATER && blockstate.getValue(BlockLiquid.LEVEL).intValue() == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			playerIn.addStat(StatList.getObjectUseStats(this));
			playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
			giveItem(playerIn, new ItemStack(FluidHandler.water));
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		if (material == Material.LAVA && blockstate.getValue(BlockLiquid.LEVEL).intValue() == 0) {
			playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL_LAVA, 1.0F, 1.0F);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			playerIn.addStat(StatList.getObjectUseStats(this));
			giveItem(playerIn, new ItemStack(FluidHandler.lava));
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
	}

	private void giveItem(EntityPlayer player, ItemStack stack) {
		if (!player.inventory.addItemStackToInventory(stack)) {
			player.dropItem(stack, false);
		}
	}
}