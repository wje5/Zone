package com.pinball3d.zone.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import scala.actors.threadpool.Arrays;

public class TECableGeneral extends TECableBasic {
	private CableConfig[] configs = new CableConfig[] { new CableConfig(), new CableConfig(), new CableConfig(),
			new CableConfig(), new CableConfig(), new CableConfig() };

	public TECableGeneral() {

	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public boolean isConnect(EnumFacing facing) {
		TileEntity te = world.getTileEntity(getPos().offset(facing));
		if (te instanceof TECableGeneral) {
			return true;
		}
		if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
			IEnergyStorage s = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
			if (s.canExtract() || s.canReceive()) {
				return true;
			}
		}
		return false;
	}

	public CableConfig getConfig(EnumFacing facing) {
		return configs[facing.getIndex()];
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			return (T) energy;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList tagList = compound.getTagList("configs", 10);
		for (int i = 0; i < configs.length; i++) {
			configs[i].readFromNBT(tagList.getCompoundTagAt(i));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList tagList = new NBTTagList();
		for (CableConfig e : configs) {
			tagList.appendTag(e.writeToNBT(new NBTTagCompound()));
		}
		compound.setTag("configs", tagList);
		return compound;
	}

	public static class CableConfig {
		private ItemStack[] whitelist = new ItemStack[15];
		private boolean energyTransmit = true;
		private ItemIOType itemIOType = ItemIOType.INPUT;

		public CableConfig() {
			Arrays.fill(whitelist, ItemStack.EMPTY);
		}

		public ItemStack[] getWhitelist() {
			return whitelist;
		}

		public boolean isEnergyTransmit() {
			return energyTransmit;
		}

		public void setEnergyTransmit(boolean energyTransmit) {
			this.energyTransmit = energyTransmit;
		}

		public ItemIOType getItemIOType() {
			return itemIOType;
		}

		public void setItemIOType(ItemIOType itemIOType) {
			this.itemIOType = itemIOType;
		}

		public void readFromNBT(NBTTagCompound compound) {
			NBTTagList tagList = compound.getTagList("whitelist", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
				int slot = itemTags.getInteger("Slot");
				if (slot >= 0 && slot < 15) {
					whitelist[slot] = new ItemStack(itemTags);
				}
			}
			energyTransmit = compound.getBoolean("energyTransmit");
			itemIOType = ItemIOType.values()[compound.getInteger("itemIOType")];
		}

		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			NBTTagList nbtTagList = new NBTTagList();
			for (int i = 0; i < 15; i++) {
				if (!whitelist[i].isEmpty()) {
					NBTTagCompound itemTag = new NBTTagCompound();
					itemTag.setInteger("Slot", i);
					whitelist[i].writeToNBT(itemTag);
					nbtTagList.appendTag(itemTag);
				}
			}
			compound.setTag("whitelist", nbtTagList);
			compound.setBoolean("energyTransmit", energyTransmit);
			compound.setInteger("itemIOType", itemIOType.ordinal());
			return compound;
		}

		public static enum ItemIOType {
			INPUT, OUTPUT, STORAGE, DISABLE;

			public String getTranslateKey() {
				switch (this) {
				case INPUT:
					return "container.cable_2.item.input";
				case OUTPUT:
					return "container.cable_2.item.output";
				case STORAGE:
					return "container.cable_2.item.storage";
				case DISABLE:
				default:
					return "container.cable_2.item.disable";
				}
			}
		}
	}
}
