package com.pinball3d.zone.inventory;

import com.pinball3d.zone.tileentity.ZoneTieredMachine;
import com.pinball3d.zone.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerTieredMachine extends Container {
	private int tier, energy, maxEnergy;
	private short energyData, maxEnergyData;
	protected ZoneTieredMachine tileEntity;

	public ContainerTieredMachine(EntityPlayer player, ZoneTieredMachine tileEntity) {
		this.tileEntity = tileEntity;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (!tileEntity.getWorld().isRemote) {
			tier = tileEntity.getTier().getTier();
			energy = tileEntity.getEnergyStored();
			maxEnergy = tileEntity.getMaxEnergyStored();
		}
		for (IContainerListener i : listeners) {
			i.sendWindowProperty(this, 0, tier);
			short[] s = Util.retractIntToShort(energy);
			i.sendWindowProperty(this, 1, s[0]);
			i.sendWindowProperty(this, 2, s[1]);
			s = Util.retractIntToShort(maxEnergy);
			i.sendWindowProperty(this, 3, s[0]);
			i.sendWindowProperty(this, 4, s[1]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 0) {
			tier = data;
		} else if (id == 1) {
			energyData = (short) data;
		} else if (id == 2) {
			energy = Util.combineShort(energyData, (short) data);
		} else if (id == 3) {
			maxEnergyData = (short) data;
		} else if (id == 4) {
			maxEnergy = Util.combineShort(maxEnergyData, (short) data);

		}
	}

	public int getTier() {
		return tier;
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
	}
}
