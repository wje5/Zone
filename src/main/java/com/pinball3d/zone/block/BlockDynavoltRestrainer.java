package com.pinball3d.zone.block;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockDynavoltRestrainer extends Block {
	public BlockDynavoltRestrainer() {
		super(Material.IRON);
		setHardness(100.0F);
		setResistance(2500.0F);
		setRegistryName("zone:dynavolt_restrainer");
		setUnlocalizedName("dynavolt_restrainer");
		setCreativeTab(TabZone.tab);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		Explosion explosion = world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, true, true);
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
	}
}
