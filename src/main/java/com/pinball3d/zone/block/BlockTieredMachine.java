package com.pinball3d.zone.block;

import java.util.function.Function;

import com.pinball3d.zone.TabZone;
import com.pinball3d.zone.Zone;
import com.pinball3d.zone.tileentity.ZoneTieredMachine.Tier;

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
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class BlockTieredMachine extends BlockContainer {
	private int gui;
	public final Tier tier;
	private Function<Tier, TileEntity> createTE;
	protected static boolean keepInventory;

	public BlockTieredMachine(String name, int gui, Function<Tier, TileEntity> createTE, Tier tier) {
		this(name, gui, createTE, tier, false);
	}

	public BlockTieredMachine(String name, int gui, Function<Tier, TileEntity> createTE, Tier tier,
			boolean hasCustomName) {
		super(Material.IRON);
		this.gui = gui;
		this.createTE = createTE;
		this.tier = tier;
		setHardness(tier.getHardness());
		setResistance(tier.getResistance());
		if (!hasCustomName) {
			setRegistryName("zone:" + name + "_" + tier.getTier());
		}
		setUnlocalizedName(name + tier.getTier());
		setCreativeTab(TabZone.tab);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(Zone.instance, gui, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!keepInventory) {
			TileEntity te = worldIn.getTileEntity(pos);
			for (EnumFacing facing : EnumFacing.VALUES) {
				IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
				if (handler != null) {
					for (int i = handler.getSlots() - 1; i >= 0; --i) {
						if (!handler.getStackInSlot(i).isEmpty()) {
							Block.spawnAsEntity(worldIn, pos, handler.getStackInSlot(i));
							((IItemHandlerModifiable) handler).setStackInSlot(i, ItemStack.EMPTY);
						}
					}
				}
			}
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (handler != null) {
				for (int i = handler.getSlots() - 1; i >= 0; --i) {
					if (!handler.getStackInSlot(i).isEmpty()) {
						Block.spawnAsEntity(worldIn, pos, handler.getStackInSlot(i));
						((IItemHandlerModifiable) handler).setStackInSlot(i, ItemStack.EMPTY);
					}
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return createTE.apply(tier);
	}
}
