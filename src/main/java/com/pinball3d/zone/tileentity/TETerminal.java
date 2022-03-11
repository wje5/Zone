package com.pinball3d.zone.tileentity;

import java.util.UUID;

import com.pinball3d.zone.block.BlockTerminal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TETerminal extends TileEntity implements ITickable {
	private UUID uuid;

	@Override
	public void update() {
		if (world.isRemote) {
			return;
		}
		if (isWorking()) {
			BlockPos pos = getPos();
			EntityPlayer player = world.getPlayerEntityByUUID(uuid);
			if (player == null || player.getDistance(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F) > 16F) {
				stopWorking();
			}
		}
	}

	public boolean isWorking() {
		return uuid != null;
	}

	public UUID getPlayerUuid() {
		return uuid;
	}

	public boolean startWorking(EntityPlayer player) {
		if (isWorking() || player == null) {
			return false;
		}
		BlockPos pos = getPos();
		if (player.getDistance(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F) > 16F) {
			return false;
		}
		uuid = player.getUniqueID();
		BlockTerminal.setState(world.getBlockState(pos).withProperty(BlockTerminal.WORKING, true), world, pos);
		System.out.println("Start" + uuid + "|" + this);
		return true;
	}

	public void forceStop() {
		if (!isWorking()) {
			return;
		}
		EntityPlayer player = world.getPlayerEntityByUUID(uuid);
	}

	public void stopWorking() {
		if (isWorking()) {
			System.out.println("stop");
			BlockTerminal.setState(world.getBlockState(pos).withProperty(BlockTerminal.WORKING, false), world, pos);
		}
		uuid = null;
	}

	public void stopWorking(UUID uuid) {
		if (uuid != null && uuid.equals(this.uuid)) {
			stopWorking();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("playerMost")) {
			uuid = compound.getUniqueId("player");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (uuid != null) {
			compound.setUniqueId("player", uuid);
		}
		return compound;
	}
}
