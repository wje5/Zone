package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.sphinx.log.component.LogComponentString;

import net.minecraft.nbt.NBTTagCompound;

public class LogRescanRecipesFinish extends Log {
	private int recipeAddCount, addCount, changeCount;

	public LogRescanRecipesFinish(int id, int recipeAddCount, int addCount, int changeCount) {
		super(Level.INFO, Type.RESCANRECIPESFINISH, id);
		this.recipeAddCount = recipeAddCount;
		this.addCount = addCount;
		this.changeCount = changeCount;
	}

	public LogRescanRecipesFinish(NBTTagCompound tag) {
		super(tag);
	}

	public int getRecipeAddCount() {
		return recipeAddCount;
	}

	public int getAddCount() {
		return addCount;
	}

	public int getChangeCount() {
		return changeCount;
	}

	@Override
	public FormattedLog format() {
		return new FormattedLog(getTime(), getLevel(), "log.rescan_recipes_finish",
				new LogComponentString(recipeAddCount + ""), new LogComponentString(addCount + ""),
				new LogComponentString(changeCount + ""));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		recipeAddCount = tag.getInteger("recipeAddCount");
		addCount = tag.getInteger("addCount");
		changeCount = tag.getInteger("changeCount");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("recipeAddCount", recipeAddCount);
		tag.setInteger("addCount", addCount);
		tag.setInteger("changeCount", changeCount);
		return tag;
	}
}
