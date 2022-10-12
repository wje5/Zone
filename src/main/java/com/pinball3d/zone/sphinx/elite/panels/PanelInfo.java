package com.pinball3d.zone.sphinx.elite.panels;

import java.util.Map.Entry;
import java.util.function.Supplier;

import com.pinball3d.zone.FluidHandler;
import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.sphinx.elite.Color;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.FoldablePanel;
import com.pinball3d.zone.sphinx.elite.FormattedString;
import com.pinball3d.zone.sphinx.elite.PanelGroup;
import com.pinball3d.zone.sphinx.elite.PanelHolder;
import com.pinball3d.zone.sphinx.elite.Subpanel;
import com.pinball3d.zone.sphinx.elite.components.BlockShow;
import com.pinball3d.zone.sphinx.elite.components.Label;
import com.pinball3d.zone.sphinx.elite.layout.BoxLayout;
import com.pinball3d.zone.sphinx.elite.layout.LinearLayout;
import com.pinball3d.zone.sphinx.elite.layout.PosLayout;
import com.pinball3d.zone.sphinx.elite.map.MapRenderManager;
import com.pinball3d.zone.util.VanillaTranslateHandler;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class PanelInfo extends Panel {
	private InfoType type;
	private Panel lastFocusPanel;

	public PanelInfo(EliteMainwindow parent, PanelGroup parentGroup) {
		super(parent, parentGroup, "info", new FormattedString(I18n.format("elite.panel.info")));
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
		Panel panel = getParent().getFocusPanel().getChosenPanel();
		InfoType t = getTypeFromPanel(panel);
		if (t == null) {
			t = getTypeFromPanel(lastFocusPanel);
		}
		if (t != type) {
			type = t;
			root.clearComponents();
			if (t != null) {
				lastFocusPanel = panel;
				Subpanel s = getInfoFromPanel(panel);
				root.addComponent(s);
				EliteMainwindow parent = getParent();
				root.addComponent(new Label(parent, root, getName(), Color.TEXT_LIGHT));
				Subpanel panel1 = new Subpanel(parent, root, 200, 60, new PosLayout());
				panel1.addComponent(new Label(parent, panel1, new FormattedString("DR R R R RRRRR"), Color.TEXT_LIGHT),
						new Pos2i(5, 0));
				panel1.addComponent(new Label(parent, panel1, new FormattedString("III"), Color.TEXT_LIGHT),
						new Pos2i(15, 15));
				root.addComponent(panel1);
				for (int i = 0; i < 100; i++) {
					root.addComponent(
							new Label(parent, root, new FormattedString("DR R R R RRRRR" + i), Color.TEXT_LIGHT));
				}
			} else {
				lastFocusPanel = null;
			}
		}
	}

	public InfoType getTypeFromPanel(Panel panel) {
		if (panel instanceof PanelMap) {
			PanelMap map = (PanelMap) panel;
			MapRenderManager manager = map.getRenderManager();
			if (manager == null || manager.getWorld() == null || manager.selectedRayTraceResult == null) {
				return null;
			} else if (manager.selectedRayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
				return InfoType.BLOCK;
			}
		}
		return null;
	}

	public Subpanel getInfoFromPanel(Panel panel) {
		if (panel instanceof PanelMap) {
			PanelMap map = (PanelMap) panel;
			MapRenderManager manager = map.getRenderManager();
			if (manager == null || manager.getWorld() == null || manager.selectedRayTraceResult == null) {
				return null;
			} else if (manager.selectedRayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
				return new PanelBlockData(getParent(), getRoot(), manager);
			}
		}
		return null;
	}

	public static enum InfoType {
		BLOCK;
	}

	public static class PanelBlockData extends Subpanel {
		private MapRenderManager manager;
		private ItemStack stack;
		private String blockName, modName;
		private World world;
		private BlockPos pos;
		private IBlockState blockstate;
		private Subpanel blockstatePanel;
		private boolean flammability;

		@SuppressWarnings("deprecation")
		public PanelBlockData(EliteMainwindow parent, Subpanel parentPanel, MapRenderManager manager) {
			super(parent, parentPanel, new BoxLayout(true));
			PanelHolder holder = new PanelHolder(parent, this);

			{
				Subpanel p1 = new Subpanel(parent, holder, new BoxLayout(false));
				p1.setMarginLeft(6).setMarginRight(6).setMarginTop(11).setMarginDown(11);
				p1.addComponent(new BlockShow(parent, p1, () -> pos).setMarginRight(6));
				Subpanel p2 = new Subpanel(parent, p1, new BoxLayout(true));
				p2.addComponent(new Label(parent, p2, () -> new FormattedString(blockName), Color.TEXT_LIGHT));
				p2.addComponent(new Label(parent, p2, () -> new FormattedString(modName), Color.TEXT_LIGHT));
				p1.addComponent(p2, BoxLayout.Type.CENTER);
				holder.addComponent(p1);
			}
			{
				FoldablePanel p1 = new FoldablePanel(parent, holder,
						new FormattedString(I18n.format("elite.panel.info.block.common")), new BoxLayout(true))
								.setFold(false);
				p1.setMarginLeft(6).setMarginRight(6).setMarginTop(3).setMarginDown(3);
				Subpanel p2 = new Subpanel(parent, p1.panel, new BoxLayout(true));
				p2.addComponent(new CustomTextPanel(parent, p2, () -> new FormattedString("X:"),
						() -> new FormattedString(pos == null ? "" : pos.getX() + "")));
				p2.addComponent(new CustomTextPanel(parent, p2, () -> new FormattedString("Y:"),
						() -> new FormattedString(pos == null ? "" : pos.getY() + "")));
				p2.addComponent(new CustomTextPanel(parent, p2, () -> new FormattedString("Z:"),
						() -> new FormattedString(pos == null ? "" : pos.getZ() + "")));
				p1.panel.addComponent(p2);
				p1.panel.addComponent(blockstatePanel = new Subpanel(parent, p1.panel, new BoxLayout(true)));
				holder.addComponent(p1);
			}
			{
				FoldablePanel p1 = new FoldablePanel(parent, holder,
						new FormattedString(I18n.format("elite.panel.info.block.material")), new BoxLayout(true))
								.setFold(false);
				p1.setMarginLeft(6).setMarginRight(6).setMarginTop(3).setMarginDown(3);
				Subpanel p2 = new Subpanel(parent, p1.panel, new BoxLayout(true));
				p2.addComponent(new CustomTextPanel(parent, p2,
						() -> new FormattedString(I18n.format("elite.panel.info.block.material.material") + ":"),
						() -> new FormattedString(blockstate == null ? ""
								: VanillaTranslateHandler.getMaterialName(blockstate.getMaterial()))));
				p2.addComponent(new CustomTextPanel(parent, p2,
						() -> new FormattedString(I18n.format("elite.panel.info.block.material.flammability") + ":"),
						() -> new FormattedString(
								flammability ? I18n.format("elite.util.true") : I18n.format("elite.util.false"))));
				p2.addComponent(new CustomTextPanel(parent, p2,
						() -> new FormattedString(I18n.format("elite.panel.info.block.material.harvest_level") + ":"),
						() -> new FormattedString(
								blockstate == null ? "" : blockstate.getBlock().getHarvestLevel(blockstate) + "")));
				p2.addComponent(new CustomTextPanel(parent, p2,
						() -> new FormattedString(I18n.format("elite.panel.info.block.material.hardness") + ":"),
						() -> new FormattedString(
								blockstate == null ? "" : blockstate.getBlockHardness(world, pos) + "")));
				p2.addComponent(new CustomTextPanel(parent, p2,
						() -> new FormattedString(I18n.format("elite.panel.info.block.material.resistance") + ":"),
						() -> new FormattedString(
								blockstate == null ? "" : blockstate.getBlock().getExplosionResistance(null) + "")));
				p1.panel.addComponent(p2);
				holder.addComponent(p1);
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
				world = manager.getWorld();
				pos = result.getBlockPos();
				blockstate = world.getBlockState(result.getBlockPos());
				Block block = blockstate.getBlock();
				stack = FluidHandler.getFluidFromBlock(block);
				if (stack.isEmpty()) {
					stack = block.getPickBlock(blockstate, result, world, pos, null);
				}
				blockName = stack.getDisplayName();
				String modid = stack.getItem().getCreatorModId(stack);
				modName = Loader.instance().getIndexedModList().get(modid).getName();
				blockstatePanel.clearComponents();
				for (Entry<IProperty<?>, Comparable<?>> entry : blockstate.getProperties().entrySet()) {
					String name = VanillaTranslateHandler.getTranslated(entry.getKey().getName());
					String value = VanillaTranslateHandler.getTranslated(entry.getValue().toString());
					blockstatePanel.addComponent(new CustomTextPanel(parent, blockstatePanel,
							() -> new FormattedString(name + ":"), () -> new FormattedString(value)));
				}
				flammability = false;
				for (EnumFacing facing : EnumFacing.values()) {
					flammability |= block.isFlammable(world, pos, facing);
				}

			}
			super.refresh();
		}
	}

	public static class CustomTextPanel extends Subpanel {
		public CustomTextPanel(EliteMainwindow parent, Subpanel parentPanel, Supplier<FormattedString> text,
				Supplier<FormattedString> text2) {
			super(parent, parentPanel, new LinearLayout());
			setExpand(true);
			addComponent(new Label(parent, this, text, Color.TEXT_LIGHT));
			addComponent(new Label(parent, this, text2, Color.TEXT_LIGHT), BoxLayout.Type.EAST);
		}

		@Override
		public int getMarginLeft() {
			return parentPanel.getRenderWidth() / 2;
		}
	}
}
