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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ItemDrillHeavy extends ItemPickaxe {
	public ItemDrillHeavy() {
		super(ZoneToolMaterial.ETHERIUM);
		setRegistryName("zone:drill_heavy");
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
		return tag.getBoolean("charged");
	}

	public static void setCharged(ItemStack stack, boolean charged) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		tag.setBoolean("charged", charged);
	}

	public static boolean checkDamage(ItemStack stack) {
		if (stack.getItemDamage() >= 4096) {
			return true;
		}
		return false;
	}

	public static ItemStack genNewStack(ItemStack stack) {
		ItemStack r = new ItemStack(ItemLoader.drill_empty);
		NBTTagCompound tag = r.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			r.setTagCompound(tag);
		}
		tag.setTag("ench", stack.getEnchantmentTagList());
		return r;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (entityIn instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityIn;
			if (checkDamage(stack)) {
				player.setHeldItem(EnumHand.MAIN_HAND, genNewStack(stack));
				return;
			}
			if (!isCharged(stack)) {
				InventoryPlayer inv = player.inventory;
				int count = 0;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack s = inv.getStackInSlot(i);
					if (!s.isEmpty() && s.getItem() == ItemLoader.energy) {
						count += s.getCount();
					}
				}
				if (count >= 5) {
					count = 0;
					for (int i = 0; i < inv.getSizeInventory(); i++) {
						ItemStack s = inv.getStackInSlot(i);
						if (!s.isEmpty() && s.getItem() == ItemLoader.energy) {
							if (checkDamage(stack)) {
								player.setHeldItem(EnumHand.MAIN_HAND, genNewStack(stack));
								return;
							}
							int amount = s.getCount() >= 5 - count ? 5 - count : s.getCount();
							count += amount;
							if (s.getCount() <= amount) {
								inv.setInventorySlotContents(i, ItemStack.EMPTY);
							} else {
								inv.setInventorySlotContents(i,
										new ItemStack(ItemLoader.energy, s.getCount() - amount));
							}
							if (count >= 5) {
								setCharged(stack, true);
								return;
							}
						}
					}
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
				&& mc.player.getHeldItemMainhand().getItem() == ItemLoader.drill_heavy
				&& isCharged(mc.player.getHeldItemMainhand())) {
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
				&& mc.player.getHeldItemMainhand().getItem() == ItemLoader.drill_heavy
				&& isCharged(mc.player.getHeldItemMainhand())) {
			Random rand = new Random();
			float f = event.getNewfov();
			f += 0.6F * (rand.nextFloat() - 0.5F);
			event.setNewfov(f);
		}
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return isCharged(stack);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		if (isCharged(itemstack)) {
			IBlockState block = player.world.getBlockState(pos);
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						if (i != 0 || j != 0 || k != 0) {
							BlockPos p = pos.add(i, j, k);
							IBlockState b = player.world.getBlockState(p);
							float hard = b.getBlockHardness(player.world, p);
							if (checkDamage(itemstack)) {
								player.setHeldItem(EnumHand.MAIN_HAND, genNewStack(itemstack));
								return super.onBlockStartBreak(itemstack, pos, player);
							}
							if (canHarvestBlock(b) && hard <= block.getBlockHardness(player.world, pos) && hard > 0) {
								player.world.destroyBlock(p, false);
								b.getBlock().harvestBlock(player.world, player, p, b, player.world.getTileEntity(p),
										itemstack);
								onBlockDestroyed(itemstack, player.world, b, p, player);
							}
						}
					}
				}
			}
		}
		return super.onBlockStartBreak(itemstack, pos, player);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return isCharged(stack) ? super.getDestroySpeed(stack, state) * 0.5F : 0.5F;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
			EntityLivingBase entityLiving) {
		setCharged(stack, false);
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}
}
