package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.tileentity.TEGrinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class BlockGrinder extends BlockContainer {
	public BlockGrinder() {
		super(Material.IRON);
		setRegistryName("zone:grinder");
		setUnlocalizedName("grinder");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(Zone.instance, GuiElementLoader.GRINDER, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TEGrinder te = (TEGrinder) worldIn.getTileEntity(pos);

		IItemHandler input = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		IItemHandler energy = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.WEST);
		IItemHandler output = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		for (int i = input.getSlots() - 1; i >= 0; --i) {
			if (input.getStackInSlot(i) != null) {
				Block.spawnAsEntity(worldIn, pos, input.getStackInSlot(i));
				((IItemHandlerModifiable) input).setStackInSlot(i, ItemStack.EMPTY);
			}
		}
		for (int i = energy.getSlots() - 1; i >= 0; --i) {
			if (energy.getStackInSlot(i) != null) {
				Block.spawnAsEntity(worldIn, pos, energy.getStackInSlot(i));
				((IItemHandlerModifiable) energy).setStackInSlot(i, ItemStack.EMPTY);
			}
		}
		for (int i = output.getSlots() - 1; i >= 0; --i) {
			if (output.getStackInSlot(i) != null) {
				Block.spawnAsEntity(worldIn, pos, output.getStackInSlot(i));
				((IItemHandlerModifiable) output).setStackInSlot(i, ItemStack.EMPTY);
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		Explosion explosion = world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, true, true);
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TEGrinder();
	}
}
