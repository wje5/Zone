package com.pinball3d.zone.block;

import java.util.List;

import com.google.common.collect.Lists;
import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPoweredPiston extends BlockPistonBase {
	public final boolean isSticky;

	public BlockPoweredPiston(boolean isSticky) {
		super(isSticky);
		this.isSticky = isSticky;
		setHardness(200.0F);
		setResistance(5000.0F);
		setRegistryName("zone:powered_piston" + (isSticky ? "_sticky" : ""));
		setUnlocalizedName("powered_piston" + (isSticky ? "_sticky" : ""));
		setCreativeTab(TabZone.tab);
	}

	private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (enumfacing != facing && worldIn.isSidePowered(pos.offset(enumfacing), enumfacing)) {
				return true;
			}
		}
		if (worldIn.isSidePowered(pos, EnumFacing.DOWN)) {
			return true;
		} else {
			BlockPos blockpos = pos.up();

			for (EnumFacing enumfacing1 : EnumFacing.values()) {
				if (enumfacing1 != EnumFacing.DOWN
						&& worldIn.isSidePowered(blockpos.offset(enumfacing1), enumfacing1)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		EnumFacing enumfacing = state.getValue(FACING);
		if (!worldIn.isRemote) {
			boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);
			if (flag && id == 1) {
				worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
				return false;
			}
			if (!flag && id == 0) {
				return false;
			}
		}
		if (id == 0) {
			if (!this.doMove(worldIn, pos, enumfacing, true)) {
				return false;
			}
			worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 3);
			worldIn.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F,
					worldIn.rand.nextFloat() * 0.25F + 0.6F);
		} else if (id == 1) {
			TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));
			if (tileentity1 instanceof TileEntityPiston) {
				((TileEntityPiston) tileentity1).clearPistonTileEntity();
			}
			worldIn.setBlockState(pos,
					BlockLoader.powered_piston_moving.getDefaultState()
							.withProperty(BlockPistonMoving.FACING, enumfacing).withProperty(BlockPistonMoving.TYPE,
									this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY
											: BlockPistonExtension.EnumPistonType.DEFAULT),
					3);
			worldIn.setTileEntity(pos,
					BlockPoweredPistonMoving.createTilePiston(this.getStateFromMeta(param), enumfacing, false, true));
			if (this.isSticky) {
				BlockPos blockpos = pos.add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2,
						enumfacing.getFrontOffsetZ() * 2);
				IBlockState iblockstate = worldIn.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				boolean flag1 = false;
				if (block == BlockLoader.powered_piston_moving) {
					TileEntity tileentity = worldIn.getTileEntity(blockpos);
					if (tileentity instanceof TileEntityPiston) {
						TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;
						if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending()) {
							tileentitypiston.clearPistonTileEntity();
							flag1 = true;
						}
					}
				}
				if (!flag1 && !iblockstate.getBlock().isAir(iblockstate, worldIn, blockpos)
						&& canPush(iblockstate, worldIn, blockpos, enumfacing.getOpposite(), false, enumfacing)
						&& (iblockstate.getMobilityFlag() == EnumPushReaction.NORMAL
								|| block == BlockLoader.powered_piston || block == BlockLoader.powered_piston_sticky)) {
					this.doMove(worldIn, pos, enumfacing, false);
				}
			} else {
				worldIn.setBlockToAir(pos.offset(enumfacing));
			}
			worldIn.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F,
					worldIn.rand.nextFloat() * 0.15F + 0.6F);
		}
		return true;
	}

	private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending) {
		if (!extending) {
			worldIn.setBlockToAir(pos.offset(direction));
		}

		BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(worldIn, pos, direction,
				extending);

		if (!blockpistonstructurehelper.canMove()) {
			return false;
		} else {
			List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
			List<IBlockState> list1 = Lists.<IBlockState>newArrayList();

			for (int i = 0; i < list.size(); ++i) {
				BlockPos blockpos = list.get(i);
				list1.add(worldIn.getBlockState(blockpos).getActualState(worldIn, blockpos));
			}

			List<BlockPos> list2 = blockpistonstructurehelper.getBlocksToDestroy();
			int k = list.size() + list2.size();
			IBlockState[] aiblockstate = new IBlockState[k];
			EnumFacing enumfacing = extending ? direction : direction.getOpposite();

			for (int j = list2.size() - 1; j >= 0; --j) {
				BlockPos blockpos1 = list2.get(j);
				IBlockState iblockstate = worldIn.getBlockState(blockpos1);
				// Forge: With our change to how snowballs are dropped this needs to disallow to
				// mimic vanilla behavior.
				float chance = iblockstate.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;
				iblockstate.getBlock().dropBlockAsItemWithChance(worldIn, blockpos1, iblockstate, chance, 0);
				worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 4);
				--k;
				aiblockstate[k] = iblockstate;
			}

			for (int l = list.size() - 1; l >= 0; --l) {
				BlockPos blockpos3 = list.get(l);
				IBlockState iblockstate2 = worldIn.getBlockState(blockpos3);
				worldIn.setBlockState(blockpos3, Blocks.AIR.getDefaultState(), 2);
				blockpos3 = blockpos3.offset(enumfacing);
				worldIn.setBlockState(blockpos3,
						BlockLoader.powered_piston_moving.getDefaultState().withProperty(FACING, direction), 4);
				worldIn.setTileEntity(blockpos3,
						BlockPoweredPistonMoving.createTilePiston(list1.get(l), direction, extending, false));
				--k;
				aiblockstate[k] = iblockstate2;
			}

			BlockPos blockpos2 = pos.offset(direction);

			if (extending) {
				BlockPistonExtension.EnumPistonType blockpistonextension$enumpistontype = this.isSticky
						? BlockPistonExtension.EnumPistonType.STICKY
						: BlockPistonExtension.EnumPistonType.DEFAULT;
				IBlockState iblockstate3 = BlockLoader.powered_piston_head.getDefaultState()
						.withProperty(BlockPistonExtension.FACING, direction)
						.withProperty(BlockPistonExtension.TYPE, blockpistonextension$enumpistontype);
				IBlockState iblockstate1 = BlockLoader.powered_piston_moving.getDefaultState()
						.withProperty(BlockPistonMoving.FACING, direction)
						.withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY
								: BlockPistonExtension.EnumPistonType.DEFAULT);
				worldIn.setBlockState(blockpos2, iblockstate1, 4);
				worldIn.setTileEntity(blockpos2,
						BlockPoweredPistonMoving.createTilePiston(iblockstate3, direction, true, true));
			}

			for (int i1 = list2.size() - 1; i1 >= 0; --i1) {
				worldIn.notifyNeighborsOfStateChange(list2.get(i1), aiblockstate[k++].getBlock(), false);
			}

			for (int j1 = list.size() - 1; j1 >= 0; --j1) {
				worldIn.notifyNeighborsOfStateChange(list.get(j1), aiblockstate[k++].getBlock(), false);
			}

			if (extending) {
				worldIn.notifyNeighborsOfStateChange(blockpos2, BlockLoader.powered_piston_head, false);
			}

			return true;
		}
	}
}
