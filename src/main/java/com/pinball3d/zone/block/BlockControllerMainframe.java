package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.sphinx.ScreenLoadSphinx;
import com.pinball3d.zone.sphinx.ScreenSphinxOpenPassword;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

		pos = pos.offset(state.getValue(FACING).getOpposite(), 3);
		pos = pos.add(0, 2, 0);
		Block block = worldIn.getBlockState(pos).getBlock();
		if (block instanceof BlockProcessingCenter && ((BlockProcessingCenter) block).isFullStructure(worldIn, pos)) {
			if (worldIn.isRemote) {
				openScreen(worldIn, pos);
			}
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void openScreen(World worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		Minecraft mc = Minecraft.getMinecraft();
		TEProcessingCenter te = (TEProcessingCenter) worldIn.getTileEntity(pos);
		if (block == BlockLoader.processing_center) {
			mc.displayGuiScreen(new ScreenSphinxOpenPassword((TEProcessingCenter) worldIn.getTileEntity(pos), true));
		} else {
			if (te.isLoading()) {
				mc.displayGuiScreen(new ScreenLoadSphinx((TEProcessingCenter) worldIn.getTileEntity(pos)));
			}
//			else if (te.needInit()) {
//				mc.displayGuiScreen(new ScreenSphinxController((TEProcessingCenter) worldIn.getTileEntity(pos), ""));
//			}
			else {
				mc.displayGuiScreen(
						new ScreenSphinxOpenPassword((TEProcessingCenter) worldIn.getTileEntity(pos), false));
			}
		}

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
		world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, true, true);
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
	}
}
