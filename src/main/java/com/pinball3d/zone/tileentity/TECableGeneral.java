package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig.ItemIOType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import scala.actors.threadpool.Arrays;

public class TECableGeneral extends TECableBasic {
	private CableConfig[] configs = new CableConfig[] { new CableConfig(), new CableConfig(), new CableConfig(),
			new CableConfig(), new CableConfig(), new CableConfig() };
	private long skipped;

	public TECableGeneral() {

	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		if (skipped == world.getTotalWorldTime()) {
			return;
		}
		List<BlockPos> l = new ArrayList<BlockPos>();
		Set<BlockPos> noentity = new HashSet<BlockPos>();
		Set<IODeviceWrapper> inputs = new HashSet<IODeviceWrapper>(), outputs = new HashSet<IODeviceWrapper>(),
				storages = new HashSet<IODeviceWrapper>();
		Set<TECableGeneral> cables = new HashSet<TECableGeneral>();
		l.add(pos);
		cables.add(this);
		for (int i = 0; i < l.size(); i++) {
			BlockPos p = l.get(i);
			TECableGeneral cable = (TECableGeneral) world.getTileEntity(p);
			for (EnumFacing facing : EnumFacing.VALUES) {
				ItemIOType type = cable.getConfig(facing).getItemIOType();
				if (type == ItemIOType.DISABLE) {
					continue;
				}
				BlockPos p2 = p.offset(facing);
				if (!noentity.contains(p2) && !l.contains(p2)) {
					TileEntity te = world.getTileEntity(p2);
					if (te instanceof TECableGeneral) {
						if (((TECableGeneral) te).getConfig(facing.getOpposite())
								.getItemIOType() != ItemIOType.DISABLE) {
							((TECableGeneral) te).skipped = world.getTotalWorldTime();
							l.add(p2);
							cables.add((TECableGeneral) te);
						}
					} else if (te != null) {
						if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
							IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
									facing.getOpposite());
							switch (type) {
							case INPUT:
								inputs.add(new IODeviceWrapper(handler, cable.getConfig(facing)));
								break;
							case OUTPUT:
								outputs.add(new IODeviceWrapper(handler, cable.getConfig(facing)));
								break;
							case STORAGE:
								storages.add(new IODeviceWrapper(handler, cable.getConfig(facing)));
								break;
							}
						}
					} else {
						noentity.add(p2);
					}
				}
			}
		}
		System.out.println(l);
		System.out.println("inputs:" + inputs);
		System.out.println("outputs:" + outputs);
		System.out.println("storages:" + storages);
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

		public boolean canEnergyTransmit() {
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
		}
	}

	public static class IODeviceWrapper {
		public final IItemHandler handler;
		public final CableConfig config;

		public IODeviceWrapper(IItemHandler handler, CableConfig config) {
			this.handler = handler;
			this.config = config;
		}
	}
}
