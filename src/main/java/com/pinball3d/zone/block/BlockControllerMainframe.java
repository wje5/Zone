package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.sphinx.log.LogSphinxOpen;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.tileentity.TEProcessingCenter.UserData;
import com.pinball3d.zone.tileentity.TEProcessingCenter.WorkingState;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockControllerMainframe extends Block {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockControllerMainframe() {
		super(Material.IRON);
		setHardness(300.0F);
		setResistance(7500.0F);
		setRegistryName("zone:controller_mainframe");
		setUnlocalizedName("controller_mainframe");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		WorldPos center = getProcessingCenterPos(new WorldPos(pos, worldIn));
		if (center.isOrigin()) {
			return false;
		}
		Block block = center.getBlockState().getBlock();
		if (block instanceof BlockProcessingCenter && ((BlockProcessingCenter) block).isFullStructure(center)) {
			if (!worldIn.isRemote) {
				TEProcessingCenter te = (TEProcessingCenter) center.getTileEntity();
				if (te.getWorkingState() == WorkingState.WORKING && te.isAdmin(playerIn)) {
					playerIn.openGui(Zone.instance, GuiElementLoader.SPHINX_CONTROLLER, worldIn, pos.getX(), pos.getY(),
							pos.getZ());
				} else {
					if (te.getUsers().isEmpty()) {
						te.addUser(new UserData(playerIn, true, false, true));
					}
					if (te.isAdmin(playerIn)) {
						te.open();
						playerIn.openGui(Zone.instance, GuiElementLoader.SPHINX_LOAD, worldIn, pos.getX(), pos.getY(),
								pos.getZ());
						te.fireLog(new LogSphinxOpen(te.getNextLogId(), playerIn));
					}
				}
			}
			return true;
		}
		return false;
	}

	public static WorldPos getProcessingCenterPos(WorldPos pos) {
		IBlockState state = pos.getBlockState();
		if (state != null && state.getBlock() == BlockLoader.controller_mainframe) {
			pos = new WorldPos(pos.getPos().offset(state.getValue(FACING).getOpposite(), 3).add(0, 2, 0), pos.getDim());
			if (pos.getBlockState().getBlock() instanceof BlockProcessingCenter) {
				return pos;
			}
		}
		return WorldPos.ORIGIN;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		this.setDefaultFacing(worldIn, pos, state);
	}

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			IBlockState iblockstate = worldIn.getBlockState(pos.north());
			IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
			IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
			IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			} else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			} else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			} else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}

			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getHorizontal(meta % 4);
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 8.0F, true, true);
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}
}
