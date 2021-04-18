package com.pinball3d.zone.sphinx.log;

import com.pinball3d.zone.sphinx.log.component.LogComponentString;

import net.minecraft.nbt.NBTTagCompound;

public class LogRescanRecipesFinish extends Log {
	private int addCount, changeCount;

	public LogRescanRecipesFinish(int id, int addCount, int changeCount) {
		super(Level.INFO, Type.RESCANRECIPESFINISH, id);
		this.addCount = addCount;
		this.changeCount = changeCount;
	}

	public LogRescanRecipesFinish(NBTTagCompound tag) {
		super(tag);
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
				new LogComponentString(addCount + ""), new LogComponentString(changeCount + ""));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		addCount = tag.getInteger("addCount");
		changeCount = tag.getInteger("changeCount");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("addCount", addCount);
		tag.setInteger("changeCount", changeCount);
		return tag;
	}
}
