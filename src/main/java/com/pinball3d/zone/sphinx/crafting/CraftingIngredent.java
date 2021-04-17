package com.pinball3d.zone.sphinx.crafting;

import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;

public class CraftingIngredent {
	private Type type;

	public CraftingIngredent(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public CraftingIngredent(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void readFromNBT(NBTTagCompound tag) {
		type = Type.values()[tag.getInteger("type")];
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("type", type.ordinal());
		return tag;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CraftingIngredent && ((CraftingIngredent) obj).type.equals(type);
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return writeToNBT(new NBTTagCompound()).toString();
	}

	public static enum Type {
		ITEM(CraftingIngredentItem::new);

		private Function<NBTTagCompound, CraftingIngredent> cons;

		private Type(Function<NBTTagCompound, CraftingIngredent> s) {
			cons = s;
		}

		public CraftingIngredent newInstance(NBTTagCompound tag) {
			return cons.apply(tag);
		}
	}

	public static CraftingIngredent readLogFromNBT(NBTTagCompound tag) {
		Type type = Type.values()[tag.getInteger("type")];
		return type.newInstance(tag);
	}
}
