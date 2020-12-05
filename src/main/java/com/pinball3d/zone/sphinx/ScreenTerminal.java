package com.pinball3d.zone.sphinx;

import java.util.List;
import java.util.UUID;

import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.network.ConnectionHelper.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class ScreenTerminal extends ScreenSphinxAdvenced {
	private TexturedButton button, button2, button3;
	public ItemStack stack;

	public ScreenTerminal(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public List<Type> getDataTypes() {
		List<Type> list = super.getDataTypes();
		list.add(Type.PLAYERVALIDNETWORK);
		list.add(Type.NETWORKPOS);
		return list;
	}

	@Override
	protected void applyComponents() {
		super.applyComponents();
		components.add(new ButtonNetworkConfig(this, width - 10, 2, () -> {
			subscreens.push(new SubscreenNetworkConfig(ScreenTerminal.this));
		}, true));
		components.add(button = new TexturedButton(this, width - 20, 2, TEXTURE, 94, 68, 22, 30, 0.25F, () -> {
			subscreens.push(new SubscreenViewStorage(ScreenTerminal.this));
		}));
		components.add(button2 = new TexturedButton(this, width - 30, 2, TEXTURE, 24, 100, 23, 30, 0.25F, () -> {
			subscreens.push(new SubscreenSynodLibrary(ScreenTerminal.this));
		}));
		components.add(button3 = new TexturedButton(this, width - 40, 2, TEXTURE, 47, 100, 28, 25, 0.25F, () -> {
			subscreens.push(new SubscreenManageClassify(ScreenTerminal.this));
		}));
	}

	@Override
	public boolean isConnected() {
		return worldpos != null;
	}

	@Override
	public void resetNetwork() {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.removeTag("networkMost");
		tag.removeTag("networkLeast");
		tag.removeTag("password");
	}

	@Override
	public ItemStack getTerminal() {
		return stack;
	}

	@Override
	public boolean canOpen() {
		return mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ItemLoader.terminal
				|| mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() == ItemLoader.terminal;
	}

	@Override
	public void preDraw(boolean online, int mouseX, int mouseY, float partialTicks) {
		super.preDraw(online, mouseX, mouseY, partialTicks);
		button.setEnabled(online);
		button2.setEnabled(online);
		button3.setEnabled(online);
	}

	@Override
	public UUID getNetworkUUID() {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		if (tag.hasUniqueId("network")) {
			return tag.getUniqueId("network");
		}
		return null;
	}
}
