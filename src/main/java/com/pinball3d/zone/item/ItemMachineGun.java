package com.pinball3d.zone.item;

import org.lwjgl.input.Mouse;

import com.pinball3d.zone.entity.EntityBullet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
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
			if (player.getHeldItem(EnumHand.OFF_HAND).isEmpty() && Mouse.isButtonDown(0)) {
				Vec3d vec3d = player.getLook(1.0F);
				EntityBullet bullet = new EntityBullet(worldIn, player, 0, 0, 0);
				bullet.posX = player.posX + vec3d.x * 1.0D;
				bullet.posY = player.posY + vec3d.y * 1.0D + player.height / 2.0F + 0.5D;
				bullet.posZ = player.posZ + vec3d.z * 1.0D;
				bullet.motionX = vec3d.x * 20 + player.motionX;
				bullet.motionY = vec3d.y * 20 + player.motionY;
				bullet.motionZ = vec3d.z * 20 + player.motionZ;
				worldIn.spawnEntity(bullet);
			}
		}
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
		if (handIn == EnumHand.MAIN_HAND) {
			player.setActiveHand(handIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(handIn));
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@SubscribeEvent
	public static void onMousePolling(MouseEvent event) {
		if (Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ItemLoader.machine_gun
				&& event.getButton() == 0) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onFOVUpdate(FOVUpdateEvent event) {
		if (event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() == ItemLoader.machine_gun) {
			float f = event.getNewfov();
			int i = event.getEntity().getItemInUseMaxCount();
			float f1 = i / 20.0F;
			if (f1 > 1.0F) {
				f1 = 1.0F;
			} else {
				f1 = f1 * f1;
			}
			f *= 1.0F - f1 * 0.45F;
			event.setNewfov(f);
		}
	}

	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.CROSSHAIRS && Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND)
				.getItem() == ItemLoader.machine_gun) {
			event.setCanceled(true);
		}
	}
}
