package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStoragePanel extends BlockDirectional {
	protected static final AxisAlignedBB PANEL_U_AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D,
			0.8125D);
	protected static final AxisAlignedBB PANEL_D_AABB = new AxisAlignedBB(0.1875D, 0.9375D, 0.1875D, 0.8125D, 1.0D,
			0.8125D);
	protected static final AxisAlignedBB PANEL_N_AABB = new AxisAlignedBB(0.1875D, 0.1875D, 0.9375D, 0.8125D, 0.8125D,
			1.0D);
	protected static final AxisAlignedBB PANEL_S_AABB = new AxisAlignedBB(0.1875D, 0.1875D, 0.0D, 0.8125D, 0.8125D,
			0.0625D);
	protected static final AxisAlignedBB PANEL_W_AABB = new AxisAlignedBB(0.9375D, 0.1875D, 0.1875D, 1.0D, 0.8125D,
			0.8125D);
	protected static final AxisAlignedBB PANEL_E_AABB = new AxisAlignedBB(0.0D, 0.1875D, 0.1875D, 0.0625D, 0.8125D,
			0.8125D);

	public BlockStoragePanel() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:storage_panel");
		setUnlocalizedName("storage_panel");
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
		setCreativeTab(TabZone.tab);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();
		iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(meta));
		return iblockstate;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withProperty(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
		case EAST:
		default:
			return PANEL_E_AABB;
		case WEST:
			return PANEL_W_AABB;
		case SOUTH:
			return PANEL_S_AABB;
		case NORTH:
			return PANEL_N_AABB;
		case UP:
			return PANEL_U_AABB;
		case DOWN:
			return PANEL_D_AABB;
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		IBlockState iblockstate = worldIn.getBlockState(pos.offset(facing.getOpposite()));
		if (iblockstate.getBlock() == Blocks.END_ROD) {
			EnumFacing enumfacing = iblockstate.getValue(FACING);
			if (enumfacing == facing) {
				return this.getDefaultState().withProperty(FACING, facing.getOpposite());
			}
		}
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}
}
