package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.sphinx.WorldPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TENode extends TileEntity implements ITickable, INeedNetwork {
	private WorldPos network;

	public TENode() {

	}

	@Override
	public void update() {
		markDirty();
	}

	@Override
	public WorldPos getNetwork() {
		return network;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		network = WorldPos.load(compound);
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (network != null) {
			network.save(compound);
		}
		return super.writeToNBT(compound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public void connect(WorldPos pos) {
		TileEntity te = pos.getTileEntity(FMLCommonHandler.instance().getMinecraftServerInstance());
		if (te instanceof TEProcessingCenter) {
			network = pos;
			this.markDirty();
		}
	}
}
