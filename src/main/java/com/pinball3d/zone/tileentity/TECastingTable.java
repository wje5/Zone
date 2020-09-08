package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.block.BlockCastingTable;
import com.pinball3d.zone.block.BlockCrucible;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TECastingTable extends TileEntity implements ITickable {
	private int tick = -1;

	public boolean tryFill() {
		if (this.tick < 0) {
			this.tick = 300;
			return true;
		} else {
			return false;
		}
	}

	public boolean tryTake() {
		if (this.tick == 0) {
			this.tick = -1;
			return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return this.tick < 0;
	}

	@Override
	public void update() {
		if (world.isRemote) {
			return;
		}
		IBlockState state = world.getBlockState(pos);
		BlockCrucible.setState(
				state.withProperty(BlockCastingTable.STATE,
						tick > 0 ? BlockCastingTable.State.FLUID
								: tick == 0 ? BlockCastingTable.State.INGOT : BlockCastingTable.State.EMPTY),
				world, pos);
		tick = tick < 0 ? -1 : tick - 1 < 0 ? 0 : tick - 1;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tick = tag.getInteger("tick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("tick", tick);
		return tag;
	}
}