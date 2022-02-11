package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.tileentity.TECableBasic;
import com.pinball3d.zone.tileentity.ZoneTieredMachine.Tier;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties.PropertyAdapter;

public class BlockCableBasic extends BlockContainer {
	public static final UnlistedPropertyBool DOWN = new UnlistedPropertyBool("down");
	public static final UnlistedPropertyBool UP = new UnlistedPropertyBool("up");
	public static final UnlistedPropertyBool NORTH = new UnlistedPropertyBool("north");
	public static final UnlistedPropertyBool SOUTH = new UnlistedPropertyBool("south");
	public static final UnlistedPropertyBool WEST = new UnlistedPropertyBool("west");
	public static final UnlistedPropertyBool EAST = new UnlistedPropertyBool("east");

	public BlockCableBasic() {
		super(Material.IRON);
		setHardness(Tier.T1.getHardness());
		setResistance(Tier.T1.getHardness());
		setRegistryName("zone:cable_1");
		setUnlocalizedName("cable_1");
		setCreativeTab(TabZone.tab);
	}

//	@Override
//	protected BlockStateContainer createBlockState() {
//		return new ExtendedBlockState(this, new IProperty[] {},
//				new IUnlistedProperty[] { DOWN, UP, NORTH, SOUTH, WEST, EAST });
//	}

//	@Override
//	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
//		TECableBasic te = (TECableBasic) world.getTileEntity(pos);
//		return ((IExtendedBlockState) state).withProperty(DOWN, te.isConnect(EnumFacing.DOWN))
//				.withProperty(UP, te.isConnect(EnumFacing.UP)).withProperty(NORTH, te.isConnect(EnumFacing.NORTH))
//				.withProperty(SOUTH, te.isConnect(EnumFacing.SOUTH)).withProperty(WEST, te.isConnect(EnumFacing.WEST))
//				.withProperty(EAST, te.isConnect(EnumFacing.EAST));
//	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return super.getBoundingBox(state, source, pos);// TODO
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TECableBasic();
	}

	public static class UnlistedPropertyBool extends PropertyAdapter<Boolean> {
		public UnlistedPropertyBool(String name) {
			super(PropertyBool.create(name));
		}
	}
}
