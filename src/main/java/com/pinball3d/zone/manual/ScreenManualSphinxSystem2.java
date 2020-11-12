package com.pinball3d.zone.manual;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.item.ItemLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ScreenManualSphinxSystem2 extends ScreenManualBase {
	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(new BlockShowWithTip(this, getXOffset() + 65, getYOffset() + 22, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 65, getYOffset() + 38, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 65, getYOffset() + 54, 1.0F,
				new ItemStack(BlockLoader.dynavolt_restrainer)));
		components.add(new BlockShowWithTip(this, getXOffset() + 65, getYOffset() + 70, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 65, getYOffset() + 86, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 49, getYOffset() + 30, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 49, getYOffset() + 46, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 49, getYOffset() + 62, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 49, getYOffset() + 78, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 81, getYOffset() + 30, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 81, getYOffset() + 46, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 81, getYOffset() + 62, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 81, getYOffset() + 78, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 33, getYOffset() + 38, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 33, getYOffset() + 54, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 33, getYOffset() + 70, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 97, getYOffset() + 38, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 97, getYOffset() + 54, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 97, getYOffset() + 70, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 17, getYOffset() + 46, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 17, getYOffset() + 62, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 113, getYOffset() + 46, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 113, getYOffset() + 62, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 1, getYOffset() + 54, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));
		components.add(new BlockShowWithTip(this, getXOffset() + 129, getYOffset() + 54, 1.0F,
				new ItemStack(BlockLoader.construct_block_all)));

		components.add(new BlockShowWithTip(this, getXOffset() + 211, getYOffset() + 22, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(new BlockShowWithTip(this, getXOffset() + 211, getYOffset() + 38, 1.0F,
				new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(new BlockShowWithTip(this, getXOffset() + 211, getYOffset() + 70, 1.0F,
				new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(new BlockShowWithTip(this, getXOffset() + 211, getYOffset() + 86, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(new BlockShowWithTip(this, getXOffset() + 196, getYOffset() + 30, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 196, getYOffset() + 46, 1.0F,
				new ItemStack(ItemLoader.truss_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 196, getYOffset() + 62, 1.0F,
				new ItemStack(ItemLoader.truss_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 196, getYOffset() + 78, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 227, getYOffset() + 30, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 227, getYOffset() + 46, 1.0F,
				new ItemStack(ItemLoader.truss_z)));
		components.add(new BlockShowWithTip(this, getXOffset() + 227, getYOffset() + 62, 1.0F,
				new ItemStack(ItemLoader.truss_x)));
		components.add(new BlockShowWithTip(this, getXOffset() + 227, getYOffset() + 78, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 179, getYOffset() + 38, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 179, getYOffset() + 54, 1.0F,
				new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(new BlockShowWithTip(this, getXOffset() + 179, getYOffset() + 70, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 243, getYOffset() + 38, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 243, getYOffset() + 54, 1.0F,
				new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(new BlockShowWithTip(this, getXOffset() + 243, getYOffset() + 70, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 163, getYOffset() + 46, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 163, getYOffset() + 62, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 259, getYOffset() + 46, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 259, getYOffset() + 62, 1.0F,
				new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, getXOffset() + 147, getYOffset() + 54, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(new BlockShowWithTip(this, getXOffset() + 275, getYOffset() + 54, 1.0F,
				new ItemStack(ItemLoader.construct_block_axis_y)));
	}

	@Override
	public void onFlip(boolean flag) {
		if (flag) {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualSphinxSystem());
		} else {
			Minecraft.getMinecraft().displayGuiScreen(new ScreenManualSphinxSystem3());
		}
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.sphinx_system_3", x + 18, y + 16);
		drawTextBlock("manual.sphinx_system_4", x2 + 18, y + 16);
	}
}
