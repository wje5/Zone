package com.pinball3d.zone.item;

import java.util.Random;

import com.pinball3d.zone.TabZone;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ItemDrillHeavy extends ItemPickaxe {
	public static final SoundEvent SOUND = new SoundEvent(new ResourceLocation("zone:drill_heavy"));

	public ItemDrillHeavy() {
		super(ZoneToolMaterial.ETHERIUM);
		setRegistryName("zone:drill_heavy");
		setUnlocalizedName("drill");
		setCreativeTab(TabZone.tab);
		setMaxStackSize(1);
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (event.player == mc.player && mc.playerController.getIsHittingBlock()
				&& mc.player.getHeldItemMainhand().getItem() == ItemLoader.drill_heavy) {
			Random rand = new Random();
			mc.player.rotationYaw += (rand.nextFloat() - 0.5F) * 5F;
			mc.player.rotationPitch += (rand.nextFloat() - 0.5F) * 5F;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFOVUpdate(FOVUpdateEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getEntity() == mc.player && mc.playerController.getIsHittingBlock()
				&& mc.player.getHeldItemMainhand().getItem() == ItemLoader.drill_heavy) {
			Random rand = new Random();
			float f = event.getNewfov();
			f += 0.6F * (rand.nextFloat() - 0.5F);
			event.setNewfov(f);
		}
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return true;
	}
}
