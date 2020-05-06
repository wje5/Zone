package com.pinball3d.zone.item;

import com.pinball3d.zone.entity.EntityBullet;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemMachineGun extends ZoneItem {
	public ItemMachineGun() {
		super("machine_gun");
		setMaxStackSize(1);
		setMaxDamage(60);
		setContainerItem(this);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (isSelected && entityIn instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityIn;
//			if (player.isElytraFlying()) {
			Vec3d vec3d = player.getLook(1.0F);
			EntityBullet bullet = new EntityBullet(worldIn, player, 0, 0, 0);
			bullet.posX = player.posX + vec3d.x * 1.0D;
			bullet.posY = player.posY + vec3d.y * 1.0D + player.height / 2.0F + 0.5D;
			bullet.posZ = player.posZ + vec3d.z * 1.0D;
			bullet.motionX = vec3d.x * 20 + player.motionX;
			bullet.motionY = vec3d.y * 20 + player.motionY;
			bullet.motionZ = vec3d.z * 20 + player.motionZ;
			worldIn.spawnEntity(bullet);
//			}
		}
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public void setTileEntityItemStackRenderer(TileEntityItemStackRenderer teisr) {
		super.setTileEntityItemStackRenderer(teisr);
	}
}
