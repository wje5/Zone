package com.pinball3d.zone.sphinx.subscreen;

import java.util.Set;

import com.pinball3d.zone.gui.IHasSubscreen;
import com.pinball3d.zone.gui.Subscreen;
import com.pinball3d.zone.gui.component.DropDownList;
import com.pinball3d.zone.gui.component.HyperTextButton;
import com.pinball3d.zone.gui.component.TextButton;
import com.pinball3d.zone.gui.component.TextInputBox;
import com.pinball3d.zone.gui.component.TexturedButton;
import com.pinball3d.zone.network.ConnectionHelper.Type;
import com.pinball3d.zone.sphinx.component.QueueChart;
import com.pinball3d.zone.util.ItemType;
import com.pinball3d.zone.util.Util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class SubscreenNewQueue extends Subscreen {
	public int productPage = 1, maxProductPage = 1, consumePage = 1, maxConsumePage = 1;
	private QueueChart chart;
	private TextInputBox box;
	private DropDownList list;

	public SubscreenNewQueue(IHasSubscreen parent, ItemType target) {
		this(parent, getDisplayWidth() / 2 - 150, getDisplayHeight() / 2 - 100, target);
	}

	public SubscreenNewQueue(IHasSubscreen parent, int x, int y, ItemType target) {
		super(parent, x, y, 300, 200, true);
		addComponent(new TextButton(this, 190, 175, I18n.format("sphinx.confirm"), () -> {
			parent.removeScreen(SubscreenNewQueue.this);
		}));
		addComponent(new TextButton(this, 235, 175, I18n.format("sphinx.cancel"), () -> {
			parent.removeScreen(SubscreenNewQueue.this);
		}));
		addComponent(chart = new QueueChart(this, 21, 29, 183, 108, target));
		addComponent(new TexturedButton(this, 214, 79, ICONS, 92, 32, 5, 9, 1.0F, () -> {
			productPage = productPage - 1 < 1 ? maxProductPage : productPage - 1;
		}));
		addComponent(new TexturedButton(this, 269, 79, ICONS, 97, 32, 5, 9, 1.0F, () -> {
			productPage = productPage + 1 > maxProductPage ? 1 : productPage + 1;
		}));
		addComponent(new TexturedButton(this, 214, 141, ICONS, 92, 32, 5, 9, 1.0F, () -> {
			consumePage = consumePage - 1 < 1 ? maxConsumePage : consumePage - 1;
		}));
		addComponent(new TexturedButton(this, 269, 141, ICONS, 97, 32, 5, 9, 1.0F, () -> {
			consumePage = consumePage + 1 > maxConsumePage ? 1 : consumePage + 1;
		}));
		addComponent(new TexturedButton(this, 189, 142, ICONS_5, 0, 60, 60, 60, 0.25F, () -> {
			// TODO
		}));
		addComponent(new TexturedButton(this, 108, 162, ICONS_5, 0, 241, 70, 15, 1.0F, () -> {
			// TODO
		}));
		addComponent(new HyperTextButton(this, 21, 140, I18n.format("sphinx.export"), () -> chart.exportCurve()));
//		addComponent(box = new TextInputBox(this, 130, 142, 60, 14, 13, () -> box.isFocus = true, 4));
		addComponent(box = new TextInputBox(this, 130, 142, 60, 15, 13, () -> box.isFocus = true, 4));
		box.text = "1";
		box.isFocus = true;
	}

	@Override
	public Set<Type> getDataTypes() {
		Set<Type> s = super.getDataTypes();
		return s;
	}

	@Override
	public void doRenderBackground(int mouseX, int mouseY) {
		super.doRenderBackground(mouseX, mouseY);
		Util.drawTexture(UI_BORDER, -5, -5, 0, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, -5, 99, 0, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, -5, 155, 0, 99, 99, 99, 0.5F);
		Util.drawTexture(UI_BORDER, 255, 155, 99, 99, 99, 99, 0.5F);
		Gui.drawRect(44, 0, 255, 44, 0x2F000000);
		Gui.drawRect(0, 44, 300, 155, 0x2F000000);
		Gui.drawRect(44, 155, 255, 200, 0x2F000000);
		Util.renderGlowHorizonLine(10, 20, 280);
		Util.renderGlowBorder(15, 23, 270, 172);
		Util.renderGlowString(I18n.format("sphinx.new_queue"), 15, 8);
		Util.drawBorder(20, 28, 185, 110, 1, 0xFF1ECCDE);
		Gui.drawRect(16, 24, 284, 28, 0x651CC3B5);
		Gui.drawRect(16, 28, 20, 194, 0x651CC3B5);
		Gui.drawRect(205, 28, 284, 194, 0x651CC3B5);
		Gui.drawRect(20, 138, 205, 194, 0x651CC3B5);
		Gui.drawRect(16, 24, 284, 28, 0x30000000);
		Gui.drawRect(16, 28, 20, 194, 0x30000000);
		Gui.drawRect(205, 28, 284, 194, 0x30000000);
		Gui.drawRect(20, 138, 205, 194, 0x30000000);
		Util.renderGlowString(I18n.format("sphinx.total_product"), 210, 28);
		Util.renderGlowString(I18n.format("sphinx.total_consume"), 210, 90);
		String text = productPage + "/" + maxProductPage;
		FontRenderer fr = Util.getFontRenderer();
		Util.renderGlowString(text, 244 - fr.getStringWidth(text) / 2, 80);
		text = consumePage + "/" + maxConsumePage;
		Util.renderGlowString(text, 244 - fr.getStringWidth(text) / 2, 142);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				Util.drawBorder(207 + i * 19, 39 + j * 19, 18, 18, 1, 0xFF1ECCDE);
				Util.drawBorder(207 + i * 19, 101 + j * 19, 18, 18, 1, 0xFF1ECCDE);
			}
		}
		Util.drawBorder(80, 140, 18, 18, 1, 0xFF1ECCDE);
		Util.renderItem(chart.getRoot().getType().createStack(), 81, 141, 1.0F);
		Util.renderGlowString(I18n.format("sphinx.amount"), 102, 145);
		Util.renderGlowString(I18n.format("sphinx.recipe"), 80, 165);
	}

	@Override
	public boolean isBlockOtherSubscreen() {
		return true;
	}
}