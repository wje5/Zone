package com.pinball3d.zone.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class ZoneTieredMachine extends ZoneMachineNew {
	private Tier tier;

	public ZoneTieredMachine() {
		super();
	}

	public ZoneTieredMachine(Tier tier, int maxEnergy) {
		super(maxEnergy * tier.getMultiple());
		this.tier = tier;
	}

	public Tier getTier() {
		return tier;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tier = Tier.values()[compound.getInteger("tier")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("tier", tier.ordinal());
		return compound;
	}

	public static enum Tier {
		T1, T2, T3;

		public int getMultiple() {
			switch (this) {
			case T1:
				return 1;
			case T2:
				return 4;
			case T3:
				return 16;
			}
			return 0;
		}

		public int getTier() {
			switch (this) {
			case T1:
				return 1;
			case T2:
				return 2;
			case T3:
				return 3;
			}
			return 0;
		}

		public float getHardness() {
			switch (this) {
			case T1:
				return 5;
			case T2:
				return 200;
			case T3:
				return 300;
			}
			return 0;
		}

		public float getResistance() {
			switch (this) {
			case T1:
				return 10;
			case T2:
				return 5000;
			case T3:
				return 7500;
			}
			return 0;
		}
	}
}
