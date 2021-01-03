package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.item.ItemLoader;
import com.pinball3d.zone.tileentity.TEBeaconCore;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockBeaconCore extends BlockContainer {
	public BlockBeaconCore() {
		super(Material.IRON);
		setHardness(300.0F);
		setResistance(7500.0F);
		setLightLevel(1.0F);
		setRegistryName("zone:beacon_core");
		setUnlocalizedName("beacon_core");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.getHeldItem(hand).getItem() == ItemLoader.terminal
				&& playerIn.getHeldItem(hand).getItemDamage() == 1) {
			return false;
		}
		if (!worldIn.isRemote) {
			playerIn.openGui(Zone.instance, GuiElementLoader.SPHINX_NEED_NETWORK, worldIn, pos.getX(), pos.getY(),
					pos.getZ());
		}
		return true;
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 8.0F, true, true);
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
	}

	public boolean isFullStructure(World world, BlockPos pos) {
		if (world.getBlockState(pos.add(-1, -1, -1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(1, -1, -1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(0, -1, 0)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(-1, -1, 1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(1, -1, 1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(-1, 2, -1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(1, 2, -1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(0, 2, 0)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(-1, 2, 1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(1, 2, 1)).getBlock() != BlockLoader.etherium_hull
				|| world.getBlockState(pos.add(-1, -1, 0)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, -1, 0)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(0, -1, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(0, -1, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-2, -1, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-2, -1, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(2, -1, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(2, -1, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-1, -1, -2)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-1, -1, 2)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, -1, -2)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, -1, 2)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-1, 0, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, 0, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-1, 0, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, 0, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-1, 1, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, 1, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-1, 1, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, 1, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(-1, 2, 0)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(1, 2, 0)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(0, 2, -1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(0, 2, 1)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(0, 3, 0)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(0, 4, 0)).getBlock() != BlockLoader.etherium_frame
				|| world.getBlockState(pos.add(0, 5, 0)).getBlock() != BlockLoader.beacon_amplifier_matrix
				|| world.getBlockState(pos.add(-2, -1, -2)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, -1, -2)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(2, -1, -2)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(-2, -1, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(2, -1, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(-2, -1, 2)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, -1, 2)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(2, -1, 2)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(-1, 1, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(1, 1, 0)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 1, -1)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 1, 1)).getBlock() != Blocks.AIR
				|| world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
			return false;
		}
		for (int i = 0; i <= 5; i++) {
			if (world.getBlockState(pos.add(-2, i, -2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-1, i, -2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(0, i, -2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(1, i, -2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(2, i, -2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-2, i, -1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(2, i, -1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-2, i, 0)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(2, i, 0)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-2, i, 1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(2, i, 1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-2, i, 2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-1, i, 2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(0, i, 2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(1, i, 2)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(2, i, 2)).getBlock() != Blocks.AIR) {
				return false;
			}
		}
		for (int i = 3; i <= 5; i++) {
			if (world.getBlockState(pos.add(-1, i, -1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(0, i, -1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(1, i, -1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-1, i, 0)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(1, i, 0)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(-1, i, 1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(0, i, 1)).getBlock() != Blocks.AIR
					|| world.getBlockState(pos.add(1, i, 1)).getBlock() != Blocks.AIR) {
				return false;
			}
		}
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		((TEBeaconCore) worldIn.getTileEntity(pos)).unload();
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TEBeaconCore();
	}
}
