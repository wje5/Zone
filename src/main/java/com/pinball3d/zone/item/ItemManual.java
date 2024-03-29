package com.pinball3d.zone.item;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.capability.CapabilityLoader;
import com.pinball3d.zone.capability.IZonePlayerCapability;
import com.pinball3d.zone.inventory.GuiElementLoader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber
public class ItemManual extends ZoneItem {
	public ItemManual() {
		super("manual");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!worldIn.isRemote) {
			playerIn.openGui(Zone.instance, GuiElementLoader.MANUAL, worldIn, 0, 0, 0);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			if (event.player.hasCapability(CapabilityLoader.PLAYER_CAPABILITY, null)) {
				IZonePlayerCapability cap = event.player.getCapability(CapabilityLoader.PLAYER_CAPABILITY, null);
				if (!cap.isInited()) {
					event.player.inventory.addItemStackToInventory(new ItemStack(ItemLoader.manual));
					cap.setInited(true);
				}
			}
		}
	}

}
