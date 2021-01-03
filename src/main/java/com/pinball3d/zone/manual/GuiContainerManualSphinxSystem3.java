package com.pinball3d.zone.manual;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.manual.component.BlockShowWithTip;

import net.minecraft.item.ItemStack;

public class GuiContainerManualSphinxSystem3 extends GuiContainerManualBase {
	public GuiContainerManualSphinxSystem3(ContainerManual container) {
		super(container);
	}

	@Override
	public void addComponents() {
		super.addComponents();
		int x = width / 2 - 146;
		int y = height / 2 - 90;
		components.add(
				new BlockShowWithTip(this, x + 65, y + 22, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(new BlockShowWithTip(this, x + 65, y + 38, 1.0F, new ItemStack(BlockLoader.truss)));
		components.add(new BlockShowWithTip(this, x + 65, y + 54, 1.0F, new ItemStack(BlockLoader.processing_center)));
		components.add(new BlockShowWithTip(this, x + 65, y + 70, 1.0F, new ItemStack(BlockLoader.truss)));
		components.add(
				new BlockShowWithTip(this, x + 65, y + 86, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(new BlockShowWithTip(this, x + 49, y + 30, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 49, y + 78, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 81, y + 30, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 81, y + 78, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 33, y + 38, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 33, y + 54, 1.0F, new ItemStack(BlockLoader.truss)));
		components.add(new BlockShowWithTip(this, x + 33, y + 70, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 97, y + 38, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 97, y + 54, 1.0F, new ItemStack(BlockLoader.truss)));
		components.add(new BlockShowWithTip(this, x + 97, y + 70, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 17, y + 46, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 17, y + 62, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 113, y + 46, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 113, y + 62, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components
				.add(new BlockShowWithTip(this, x + 1, y + 54, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(
				new BlockShowWithTip(this, x + 129, y + 54, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));

		components.add(
				new BlockShowWithTip(this, x + 211, y + 22, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(
				new BlockShowWithTip(this, x + 211, y + 38, 1.0F, new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(
				new BlockShowWithTip(this, x + 211, y + 70, 1.0F, new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(
				new BlockShowWithTip(this, x + 211, y + 86, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(new BlockShowWithTip(this, x + 196, y + 30, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 196, y + 46, 1.0F, new ItemStack(ItemLoader.truss_x)));
		components.add(new BlockShowWithTip(this, x + 196, y + 62, 1.0F, new ItemStack(ItemLoader.truss_z)));
		components.add(new BlockShowWithTip(this, x + 196, y + 78, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 227, y + 30, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 227, y + 46, 1.0F, new ItemStack(ItemLoader.truss_z)));
		components.add(new BlockShowWithTip(this, x + 227, y + 62, 1.0F, new ItemStack(ItemLoader.truss_x)));
		components.add(new BlockShowWithTip(this, x + 227, y + 78, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 179, y + 38, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(
				new BlockShowWithTip(this, x + 179, y + 54, 1.0F, new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(new BlockShowWithTip(this, x + 179, y + 70, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 243, y + 38, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(
				new BlockShowWithTip(this, x + 243, y + 54, 1.0F, new ItemStack(BlockLoader.cluster_operation_module)));
		components.add(new BlockShowWithTip(this, x + 243, y + 70, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 163, y + 46, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 163, y + 62, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 259, y + 46, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(new BlockShowWithTip(this, x + 259, y + 62, 1.0F, new ItemStack(BlockLoader.charged_glass)));
		components.add(
				new BlockShowWithTip(this, x + 147, y + 54, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));
		components.add(
				new BlockShowWithTip(this, x + 275, y + 54, 1.0F, new ItemStack(ItemLoader.construct_block_axis_y)));
	}

	@Override
	public void onFlip(boolean flag) {
		mc.player.openGui(Zone.instance, GuiElementLoader.MANUAL, mc.world, 0, 0, flag ? 10 : 12);
	}

	@Override
	public void drawContents(int mouseX, int mouseY) {
		int x = width / 2 - 146;
		int x2 = width / 2;
		int y = height / 2 - 90;
		drawTextBlock("manual.sphinx_system_5", x + 18, y + 16);
		drawTextBlock("manual.sphinx_system_6", x2 + 18, y + 16);
	}
}
