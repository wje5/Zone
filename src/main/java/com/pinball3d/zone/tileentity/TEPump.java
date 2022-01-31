package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.FluidHandler;
import com.pinball3d.zone.block.BlockTieredMachineLightable;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEPump extends ZoneTieredMachine {
	protected int tick;
	protected ItemStackHandler fluid = new ItemStackHandler();
	protected ItemStackHandler expectFluid = new ItemStackHandler();

	public TEPump() {
		super();
	}

	public TEPump(Tier tier) {
		super(tier, 8000);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		boolean flag = tick > 0;
		int work = getTier().getMultiple();
		label: while (work > 0) {
			if (tick > 0) {
				int m = Math.min(work, tick);
				if (energy.extractEnergy(m * 20, false) < m * 20) {
					tick = 0;
					expectFluid.setStackInSlot(0, ItemStack.EMPTY);
					break;
				} else {
					tick -= m;
					work -= m;
				}
			}
			if (tick <= 0) {
				fluid.insertItem(0, expectFluid.getStackInSlot(0), false);
				expectFluid.setStackInSlot(0, ItemStack.EMPTY);
				if (pos.getY() > 0 && energy.extractEnergy(200, true) == 200) {
					Chunk chunk = world.getChunkFromBlockCoords(pos);
					for (int i = pos.getY() - 1; i >= 0; i--) {
						for (int j = 0; j <= 15; j++) {
							for (int k = 0; k <= 15; k++) {
								BlockPos p = new BlockPos((chunk.x << 4) + j, i, (chunk.z << 4) + k);
								ItemStack stack = FluidHandler.tryDrainFluidFromWorld(world, p,
										e -> fluid.insertItem(0, e, true).isEmpty());
								if (!stack.isEmpty()) {
									expectFluid.insertItem(0, stack, false);
									tick = 10;
									NetworkHandler.instance.sendToAllAround(new MessagePlaySoundAtPos(pos, 9),
											new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5F,
													pos.getY() + 0.5F, pos.getZ() + 0.5F, 16));
									continue label;
								}
							}
						}
					}
				}
				break;
			}
		}
		if (tick > 0 != flag) {
			BlockTieredMachineLightable.setState(tick > 0, world, pos);
		}
	}

	public int getTick() {
		return tick;
	}

	public int getTotalTick() {
		return 100;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			return (T) fluid;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		fluid.deserializeNBT(compound.getCompoundTag("output"));
		expectFluid.deserializeNBT(compound.getCompoundTag("expectOutput"));
		tick = compound.getInteger("tick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("output", fluid.serializeNBT());
		compound.setTag("expectOutput", expectFluid.serializeNBT());
		compound.setInteger("tick", tick);
		return compound;
	}
}
