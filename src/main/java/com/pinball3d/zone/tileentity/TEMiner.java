package com.pinball3d.zone.tileentity;

import java.util.List;

import com.pinball3d.zone.FluidHandler;
import com.pinball3d.zone.block.BlockTieredMachineLightable;
import com.pinball3d.zone.network.MessagePlaySoundAtPos;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TEMiner extends ZoneTieredMachine {
	protected int tick;
	protected ItemStackHandler storage = new ItemStackHandler(27);
	protected BlockPos target;

	public TEMiner() {
		super();
	}

	public TEMiner(Tier tier) {
		super(tier, 8000);
	}

	@SuppressWarnings("deprecation")
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
					break;
				} else {
					tick -= m;
					work -= m;
				}
			}
			if (tick <= 0) {
				if (target != null) {
					IBlockState state = world.getBlockState(target);
					Block block = state.getBlock();
					if (!world.isAirBlock(target) && FluidHandler.getFluidFromBlock(block).isEmpty()
							&& state.getBlockHardness(world, target) >= 0) {
						List<ItemStack> list = block.getDrops(world, target, state, 0);
						for (ItemStack stack : list) {
							for (int i = 0; i < 27; i++) {
								stack = storage.insertItem(i, stack, false);
								if (stack.isEmpty()) {
									break;
								}
							}
							if (!stack.isEmpty()) {
								Block.spawnAsEntity(world, pos.add(0, 1, 0), stack);
							}
						}
						world.setBlockToAir(target);
					}
					target = null;
				}
				if (pos.getY() > 3 && energy.extractEnergy(200, true) == 200) {
					Chunk chunk = world.getChunkFromBlockCoords(pos);
					for (int i = pos.getY() - 4; i >= 0; i--) {
						for (int j = -16; j <= 31; j++) {
							for (int k = -16; k <= 31; k++) {
								BlockPos p = new BlockPos((chunk.x << 4) + j, i, (chunk.z << 4) + k);
								IBlockState state = world.getBlockState(p);
								Block block = state.getBlock();
								if (!world.isAirBlock(p) && FluidHandler.getFluidFromBlock(block).isEmpty()
										&& state.getBlockHardness(world, p) >= 0) {
									target = p;
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
			return (T) storage;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		storage.deserializeNBT(compound.getCompoundTag("storage"));
		target = compound.hasKey("target") ? BlockPos.fromLong(compound.getLong("target")) : null;
		tick = compound.getInteger("tick");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("storage", storage.serializeNBT());
		if (target != null) {
			compound.setLong("target", target.toLong());
		}
		compound.setInteger("tick", tick);
		return compound;
	}
}