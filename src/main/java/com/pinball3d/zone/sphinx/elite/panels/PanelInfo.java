package com.pinball3d.zone.sphinx.elite.panels;

import java.util.stream.Collectors;

import com.pinball3d.zone.FluidHandler;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.map.MapRenderManager;
import com.pinball3d.zone.sphinx.elite.ui.component.BlockShow;
import com.pinball3d.zone.sphinx.elite.ui.component.Label;
import com.pinball3d.zone.sphinx.elite.ui.core.FoldablePanel;
import com.pinball3d.zone.sphinx.elite.ui.core.Panel;
import com.pinball3d.zone.sphinx.elite.ui.core.PanelHolder;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;
import com.pinball3d.zone.sphinx.elite.ui.core.layout.BoxLayout;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class PanelInfo extends Panel {
	private InfoType type;
	private Runnable refresh;
	private Panel lastFocusPanel;

	public PanelInfo(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, new FormattedString(I18n.format("elite.panel.info")));
	}

	@Override
	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
		refreshInfo();
		super.doRenderPre(mouseX, mouseY, partialTicks);
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		EliteRenderHelper.drawRect(0, 0, getWidth(), getHeight(), Color.COMP_BG_LIGHT);
		super.doRender(mouseX, mouseY, partialTicks);
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
				lastFocusPanel = null;
			} else if (manager.selectedRayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
				if (type != InfoType.BLOCK) {
					type = InfoType.BLOCK;
					root.clearComponents();
					System.out.println(type);
					lastFocusPanel = panel;

					PanelBlockData p = new PanelBlockData(parent, root, manager);
					root.addComponent(p);
					refresh = p::refresh;

//					root.addComponent(new Label(parent, root, getName(), Color.TEXT_LIGHT));

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
			} else if (lastFocusPanel == null || getParent().getPanels().stream().map(e -> e.getChosenPanel())
					.collect(Collectors.toSet()).contains(lastFocusPanel)) {
				if (type != null) {
					type = null;
					root.clearComponents();
					System.out.println(type);
				}
				lastFocusPanel = null;
			}
		}
	}

	public static enum InfoType {
		BLOCK;
	}

	public static class PanelBlockData extends Subpanel {
		private MapRenderManager manager;
		private ItemStack stack;
		private String blockName, modName;
		private BlockPos pos;

		public PanelBlockData(EliteMainwindow parent, Subpanel parentPanel, MapRenderManager manager) {
			super(parent, parentPanel, new BoxLayout(true));
			PanelHolder holder = new PanelHolder(parent, this);

			{
				Subpanel p1 = new Subpanel(parent, holder, new BoxLayout(false));
				p1.setMarginLeft(5).setMarginRight(5).setMarginTop(11).setMarginDown(11);
//				p1.addComponent(new ItemShow(parent, p1, () -> stack).setMarginRight(6));
				p1.addComponent(new BlockShow(parent, p1, () -> pos).setMarginRight(6));

				Subpanel p2 = new Subpanel(parent, p1, new BoxLayout(true));
				p2.addComponent(new Label(parent, p2, () -> new FormattedString(blockName), Color.TEXT_LIGHT));
				p2.addComponent(new Label(parent, p2, () -> new FormattedString(modName), Color.TEXT_LIGHT));
				p1.addComponent(p2, BoxLayout.Type.CENTER);

				holder.addComponent(p1, true);
			}
			{
				FoldablePanel p1 = new FoldablePanel(parent, holder,
						new FormattedString(I18n.format("elite.panel.info.block.base_properties")),
						new BoxLayout(true));
				p1.setMarginTop(3).setMarginDown(3);
				Subpanel p2 = new Subpanel(parent, p1.panel, new BoxLayout(true));

				p2.addComponent(new Label(parent, p2,
						() -> new FormattedString("X:" + (pos == null ? "" : pos.getX() + "")), Color.TEXT_LIGHT));
				p2.addComponent(new Label(parent, p2,
						() -> new FormattedString("Y:" + (pos == null ? "" : pos.getY() + "")), Color.TEXT_LIGHT));
				p2.addComponent(new Label(parent, p2,
						() -> new FormattedString("Z:" + (pos == null ? "" : pos.getZ() + "")), Color.TEXT_LIGHT));

				p1.panel.addComponent(p2);

				holder.addComponent(p1, true);
			}

			addComponent(holder);
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
				pos = result.getBlockPos();
				IBlockState blockstate = world.getBlockState(result.getBlockPos());
				Block block = blockstate.getBlock();
				stack = FluidHandler.getFluidFromBlock(block);
				if (stack.isEmpty()) {
					stack = block.getPickBlock(blockstate, result, world, pos, null);
				}
				blockName = stack.getDisplayName();
				String modid = stack.getItem().getCreatorModId(stack);
				modName = Loader.instance().getIndexedModList().get(modid).getName();
			}
			super.refresh();
		}
	}
}
