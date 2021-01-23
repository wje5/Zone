package com.pinball3d.zone.manual;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.SlotItemFrame;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerManualSphinxSystem4 extends ContainerManual {
	public ContainerManualSphinxSystem4(EntityPlayer player) {
		super(player);
		addSlotToContainer(new SlotItemFrame(65, 22, new ItemStack(BlockLoader.construct_block_all)));
		addSlotToContainer(new SlotItemFrame(65, 38, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(65, 54, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(65, 70, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(65, 86, new ItemStack(BlockLoader.construct_block_all)));
		addSlotToContainer(new SlotItemFrame(49, 30, new ItemStack(ItemLoader.construct_block_axis_z)));
		addSlotToContainer(new SlotItemFrame(49, 46, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(49, 62, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(49, 78, new ItemStack(ItemLoader.construct_block_axis_x)));
		addSlotToContainer(new SlotItemFrame(81, 30, new ItemStack(ItemLoader.construct_block_axis_x)));
		addSlotToContainer(new SlotItemFrame(81, 46, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(81, 62, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(81, 78, new ItemStack(ItemLoader.construct_block_axis_z)));
		addSlotToContainer(new SlotItemFrame(33, 38, new ItemStack(ItemLoader.construct_block_axis_z)));
		addSlotToContainer(new SlotItemFrame(33, 54, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(33, 70, new ItemStack(ItemLoader.construct_block_axis_x)));
		addSlotToContainer(new SlotItemFrame(97, 38, new ItemStack(ItemLoader.construct_block_axis_x)));
		addSlotToContainer(new SlotItemFrame(97, 54, new ItemStack(BlockLoader.charged_glass)));
		addSlotToContainer(new SlotItemFrame(97, 70, new ItemStack(ItemLoader.construct_block_axis_z)));
		addSlotToContainer(new SlotItemFrame(17, 46, new ItemStack(ItemLoader.construct_block_axis_z)));
		addSlotToContainer(new SlotItemFrame(17, 62, new ItemStack(ItemLoader.construct_block_axis_x)));
		addSlotToContainer(new SlotItemFrame(113, 46, new ItemStack(ItemLoader.construct_block_axis_x)));
		addSlotToContainer(new SlotItemFrame(113, 62, new ItemStack(ItemLoader.construct_block_axis_z)));
		addSlotToContainer(new SlotItemFrame(1, 54, new ItemStack(BlockLoader.construct_block_all)));
		addSlotToContainer(new SlotItemFrame(129, 54, new ItemStack(BlockLoader.construct_block_all)));
	}
}
