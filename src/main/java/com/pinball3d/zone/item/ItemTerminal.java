package com.pinball3d.zone.item;

import com.pinball3d.zone.sphinx.ScreenTerminal;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTerminal extends ZoneItem {
	public ItemTerminal() {
		super("terminal");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		BlockPos pos = playerIn.getPosition();
		Minecraft.getMinecraft().displayGuiScreen(new ScreenTerminal());
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}
