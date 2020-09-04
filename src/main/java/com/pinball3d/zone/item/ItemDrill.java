package com.pinball3d.zone.item;

import java.util.Random;

import com.pinball3d.zone.TabZone;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ItemDrill extends ItemPickaxe {
	public static final SoundEvent SOUND = new SoundEvent(new ResourceLocation("zone:drill"));

	public ItemDrill() {
		super(ZoneToolMaterial.ETHERIUM);
		setRegistryName("zone:drill");
		setUnlocalizedName("drill");
		setCreativeTab(TabZone.tab);
		setMaxStackSize(1);
	}

	public static boolean isCharged(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		return tag.getInteger("charge") > 0;
	}

	public static void charge(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setInteger("charge", 4);
	}

	public static void consume(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		int amount = tag.getInteger("charge") - 1;
		tag.setInteger("charge", amount <= 0 ? 0 : amount);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!isCharged(stack) && entityIn instanceof EntityPlayer) {
			InventoryPlayer inv = ((EntityPlayer) entityIn).inventory;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack s = inv.getStackInSlot(i);
				if (!s.isEmpty() && s.getItem() == ItemLoader.energy) {
					if (s.getCount() <= 1) {
						inv.setInventorySlotContents(i, ItemStack.EMPTY);
					} else {
						inv.setInventorySlotContents(i, new ItemStack(ItemLoader.energy, s.getCount() - 1));
					}
					charge(stack);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (event.player == mc.player && mc.playerController.getIsHittingBlock()
				&& mc.player.getHeldItemMainhand().getItem() == ItemLoader.drill
				&& isCharged(mc.player.getHeldItemMainhand())) {
			Random rand = new Random();
			mc.player.rotationYaw += (rand.nextFloat() - 0.5F) * 2F;
			mc.player.rotationPitch += (rand.nextFloat() - 0.5F) * 2F;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFOVUpdate(FOVUpdateEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getEntity() == mc.player && mc.playerController.getIsHittingBlock()
				&& mc.player.getHeldItemMainhand().getItem() == ItemLoader.drill
				&& isCharged(mc.player.getHeldItemMainhand())) {
			Random rand = new Random();
			float f = event.getNewfov();
			f += 0.3F * (rand.nextFloat() - 0.5F);
			event.setNewfov(f);
		}
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return isCharged(stack);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return isCharged(stack) ? super.getDestroySpeed(stack, state) : 0.5F;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
			EntityLivingBase entityLiving) {
		consume(stack);
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}
}
