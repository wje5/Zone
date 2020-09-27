package com.pinball3d.zone.tileentity;

import java.util.List;

import com.pinball3d.zone.block.BlockCrucible;
import com.pinball3d.zone.block.BlockLoader;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TECrucible extends TileEntity implements ITickable {
	public ItemStackHandler ore = new ItemStackHandler();
	public float temp = 20;
	public int amount;

	@Override
	public void update() {
		if (world.isRemote) {
			return;
		}
		if (world.getBlockState(pos.add(0, -1, 0)).getBlock() == BlockLoader.burning_box_light) {
			temp = Math.min(temp + 1.2F, 1220.0F);
		} else {
			if (temp > 20.0F) {
				temp -= Math.sqrt(temp - 20.0F) / 20.0F;
			} else {
				temp = 20.0F;
			}
		}
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.getX(),
				pos.getY() + 0.5D, pos.getZ(), pos.getX() + 1.0D, pos.getY() + 1.5D, pos.getZ() + 1.0D));
		for (Entity entity : list) {
			if (entity instanceof EntityItem) {
				EntityItem entityItem = (EntityItem) entity;
				ItemStack stack = entityItem.getItem();
				if (stack.getItem() == Item.getItemFromBlock(Blocks.IRON_ORE)) {
					if (ore.getStackInSlot(0).isEmpty()) {
						ore.setStackInSlot(0, new ItemStack(Blocks.IRON_ORE, stack.getCount()));
						stack.setCount(0);
					} else {
						int a = Math.min(64 - ore.getStackInSlot(0).getCount(), stack.getCount());
						ore.setStackInSlot(0, new ItemStack(Blocks.IRON_ORE, ore.getStackInSlot(0).getCount() + a));
						stack.shrink(a);
					}
				}
			}
		}
		if (temp >= 935 && !ore.getStackInSlot(0).isEmpty()) {
			if (amount < 10) {
				amount += 2;
				ore.setStackInSlot(0, new ItemStack(Blocks.IRON_ORE, ore.getStackInSlot(0).getCount() - 1));
				temp -= 120;
			}
		}
		IBlockState state = world.getBlockState(pos);
		BlockCrucible.setState(state.withProperty(BlockCrucible.ORE, !ore.getStackInSlot(0).isEmpty())
				.withProperty(BlockCrucible.FLUID, amount > 0), world, pos);
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
			return (T) ore;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		ore.deserializeNBT(tag.getCompoundTag("ore"));
		temp = tag.getFloat("temp");
		amount = tag.getInteger("amount");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("ore", ore.serializeNBT());
		tag.setFloat("temp", temp);
		tag.setInteger("amount", amount);
		return tag;
	}
}