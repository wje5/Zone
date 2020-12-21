package com.pinball3d.zone.tileentity;

import com.pinball3d.zone.ConfigLoader;
import com.pinball3d.zone.block.BlockPump;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEPump extends ZoneMachine {
	protected int tick, energyTick;
	protected ItemStackHandler fluid, expectFluid;

	public TEPump() {
		super(1);
		fluid = new ItemStackHandler();
		expectFluid = new ItemStackHandler();
		explosionStrength = 6.0F;
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		boolean flag = energyTick > 0;
		tick = tick < 1 ? 0 : tick - 1;
		if (tick <= 0) {
			fluid.insertItem(0, expectFluid.getStackInSlot(0), false);
			expectFluid.setStackInSlot(0, ItemStack.EMPTY);
			if (tryUseEnergy(true) || energyTick >= 10) {
				label: for (int i = -15; i <= 15; i++) {
					for (int j = -15; j <= 15; j++) {
						for (int k = -15; k <= 15; k++) {
							BlockPos p = pos.add(i, j, k);
							IBlockState s = world.getBlockState(p);
							if (s.getMaterial().isLiquid()) {
								Block block = s.getBlock();
								ItemStack stack = ItemStack.EMPTY;
								if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER)
										&& s.getValue(BlockLiquid.LEVEL).intValue() == 0) {
									stack = new ItemStack(ItemLoader.water);
								} else if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
										&& s.getValue(BlockLiquid.LEVEL).intValue() == 0) {
									stack = new ItemStack(ItemLoader.lava);
								} else if (block instanceof IFluidBlock) {
									IFluidBlock f = (IFluidBlock) block;
									FluidStack fluidstack = f.drain(world, p, false);
									if (fluidstack != null && fluidstack.amount >= 1000) {
										stack = new ItemStack(Item
												.getByNameOrId("zone:" + block.getRegistryName().getResourcePath()));
										if (!stack.isEmpty() && fluid.insertItem(0, stack, true).isEmpty()) {
											expectFluid.insertItem(0, stack, false);
											tick = 10;
											if (!ConfigLoader.disableMachineSound) {
												NetworkHandler.instance.sendToAllAround(
														new MessagePlaySoundAtPos(pos, 9),
														new TargetPoint(world.provider.getDimension(),
																pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
																16));
											}
											f.drain(world, p, true);
											break label;
										}
										continue;
									}
								}
								if (!stack.isEmpty() && fluid.insertItem(0, stack, true).isEmpty()) {
									expectFluid.insertItem(0, stack, false);
									tick = 10;
									NetworkHandler.instance.sendToAllAround(new MessagePlaySoundAtPos(pos, 9),
											new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5F,
													pos.getY() + 0.5F, pos.getZ() + 0.5F, 16));
									world.setBlockToAir(p);
									break label;
								}
							}
						}
					}
				}
			}
		}
		energyTick = energyTick < 1 ? 0 : energyTick - 1;
		if (energyTick <= 0 && tick > 0) {
			if (tryUseEnergy(false)) {
				energyTick += 10;
			} else {
				tick = 0;
				expectFluid.setStackInSlot(0, ItemStack.EMPTY);
			}
		}
		if (energyTick > 0 != flag) {
			BlockPump.setState(energyTick > 0, world, pos);
		}
	}

	public int getEnergyTick() {
		return energyTick;
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
			switch (facing) {
			case DOWN:
				return (T) fluid;
			default:
				return (T) energy;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		fluid.deserializeNBT(compound.getCompoundTag("fuel"));
		expectFluid.deserializeNBT(compound.getCompoundTag("expectFluid"));
		tick = compound.getInteger("tick");
		energyTick = compound.getInteger("energyTick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("fluid", fluid.serializeNBT());
		compound.setTag("expectFluid", expectFluid.serializeNBT());
		compound.setInteger("tick", tick);
		compound.setInteger("energyTick", energyTick);
		return compound;
	}
}
