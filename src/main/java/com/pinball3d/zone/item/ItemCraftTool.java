package com.pinball3d.zone.item;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemCraftTool extends ZoneItem {
	public ItemCraftTool(String name) {
		super(name);
		setMaxStackSize(1);
		setMaxDamage(60);
		setContainerItem(this);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		stack = stack.copy();
		stack.attemptDamageItem(1, itemRand, null);
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (new Random().nextInt(4) != 0) {
			return super.onItemRightClick(worldIn, playerIn, handIn);
		}
		Vec3d vec3d = playerIn.getLook(1.0F);
		EntityLargeFireball entitylargefireball = new EntityLargeFireball(worldIn, playerIn, vec3d.x * 10, vec3d.y * 10,
				vec3d.z * 10);
		entitylargefireball.explosionPower = 4;
		entitylargefireball.posX = playerIn.posX + vec3d.x * 4.0D;
		entitylargefireball.posY = playerIn.posY + playerIn.height / 2.0F + 0.5D;
		entitylargefireball.posZ = playerIn.posZ + vec3d.z * 4.0D;
		worldIn.spawnEntity(entitylargefireball);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
