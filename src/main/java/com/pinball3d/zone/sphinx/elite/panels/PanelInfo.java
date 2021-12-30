package com.pinball3d.zone.sphinx.elite.panels;

import com.pinball3d.zone.FluidHandler;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.map.MapRenderManager;
import com.pinball3d.zone.sphinx.elite.ui.component.ItemShow;
import com.pinball3d.zone.sphinx.elite.ui.component.Label;
import com.pinball3d.zone.sphinx.elite.ui.core.Panel;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.BoxLayout;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class PanelInfo extends Panel {
	private InfoType type;
	private Runnable refresh;

	public PanelInfo(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, new FormattedString(I18n.format("elite.panel.info")));
		Subpanel root = getRoot();
	}

	@Override
	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
		refreshInfo();
		super.doRenderPre(mouseX, mouseY, partialTicks);
	}

	public void refreshInfo() {
		Subpanel root = getRoot();
		EliteMainwindow parent = getParent();
		Panel panel = getParent().getFocusPanel().getChosenPanel();
		if (panel instanceof PanelMap) {
			PanelMap map = (PanelMap) panel;
			MapRenderManager manager = map.getRenderManager();
			if (manager == null || manager.selectedRayTraceResult == null) {
				if (type != null) {
					type = null;
					root.clearComponents();
					System.out.println(type);
				}
			} else if (manager.selectedRayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
				if (type != InfoType.BLOCK) {
					type = InfoType.BLOCK;
					root.clearComponents();
					System.out.println(type);

					PanelBlockData p = new PanelBlockData(parent, root, manager);
					root.addComponent(p);
					refresh = p::refresh;

					root.addComponent(new Label(parent, root, getName(), Color.TEXT_LIGHT));

//					Subpanel panel1 = new Subpanel(parent, root, 200, 60, new PosLayout());
//					panel1.addComponent(
//							new Label(parent, panel1, new FormattedString("DR R R R RRRRR"), Color.TEXT_LIGHT),
//							new Pos2i(5, 0));
//					panel1.addComponent(new Label(parent, panel1, new FormattedString("III"), Color.TEXT_LIGHT),
//							new Pos2i(15, 15));
//					root.addComponent(panel1);
//					for (int i = 0; i < 100; i++) {
//						root.addComponent(
//								new Label(parent, root, new FormattedString("DR R R R RRRRR" + i), Color.TEXT_LIGHT));
//					}
				} else {
					refresh.run();
				}
			}
		}
	}

	public static enum InfoType {
		BLOCK;
	}

	public static class PanelBlockData extends Subpanel {
		private MapRenderManager manager;
		private ItemStack stack;
		private FormattedString blockName, modName;

		public PanelBlockData(EliteMainwindow parent, Subpanel parentPanel, MapRenderManager manager) {
			super(parent, parentPanel, new BoxLayout());
			addComponent(new ItemShow(parent, this, () -> stack));
			addComponent(new Label(parent, this, () -> blockName, Color.TEXT_LIGHT));
			addComponent(new Label(parent, this, () -> modName, Color.TEXT_LIGHT));
			this.manager = manager;
		}

		@Override
		public void refresh() {
			RayTraceResult result = manager.selectedRayTraceResult;
			if (result == null) {
				stack = ItemStack.EMPTY;
				blockName = null;
				modName = null;
			} else {
				World world = manager.getWorld();
				BlockPos pos = result.getBlockPos();
				IBlockState blockstate = world.getBlockState(result.getBlockPos());
				Block block = blockstate.getBlock();
				stack = FluidHandler.getFluidFromBlock(block);
				if (stack.isEmpty()) {
					stack = block.getPickBlock(blockstate, result, world, pos, null);
				}
				blockName = new FormattedString(stack.getDisplayName());
//			modName = new FormattedString(block.)
			}
			super.refresh();
		}
	}
}
