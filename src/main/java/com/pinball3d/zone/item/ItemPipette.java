package com.pinball3d.zone.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public class ItemPipette extends ZoneItem {
	public ItemPipette() {
		super("pipette");
		setMaxStackSize(1);
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			ItemStack s = event.player.getHeldItemMainhand();
			if (s.getItem() == ItemLoader.pipette) {
				FluidWrapper w = (FluidWrapper) s.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
						null);
				FluidStack f = w.getFluid();
				if (f != null && f.amount == 1000) {
					giveItem(event.player, ItemFluid.createStack(f.getFluid()));
					w.setFluid(null);
				}
			}
			s = event.player.getHeldItemOffhand();
			if (s.getItem() == ItemLoader.pipette) {
				FluidWrapper w = (FluidWrapper) s.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
						null);
				FluidStack f = w.getFluid();
				if (f != null && f.amount == 1000) {
					giveItem(event.player, ItemFluid.createStack(f.getFluid()));
					w.setFluid(null);
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		boolean flag = true;
		ItemStack stack = player.getHeldItem(handIn);
		RayTraceResult result = rayTrace(world, player, flag);

		if (result == null) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		} else if (result.typeOfHit != RayTraceResult.Type.BLOCK) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		}

		BlockPos pos = result.getBlockPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof IFluidBlock || block instanceof BlockLiquid) {
			IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(world, pos, result.sideHit);
			if (targetFluidHandler != null) {
				FluidStack fs = targetFluidHandler.drain(1000, false);
				if (fs != null && fs.amount == 1000) {
					targetFluidHandler.drain(fs, true);
					SoundEvent soundevent = fs.getFluid().getFillSound(fs);
					player.world.playSound(null, player.posX, player.posY + 0.5, player.posZ, soundevent,
							SoundCategory.BLOCKS, 1.0F, 1.0F);
					giveItem(player, ItemFluid.createStack(fs.getFluid()));
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
	}

	private static void giveItem(EntityPlayer player, ItemStack stack) {
		if (!player.inventory.addItemStackToInventory(stack)) {
			player.dropItem(stack, false);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new FluidWrapper(stack);
	}

	public static class FluidWrapper implements IFluidHandlerItem, ICapabilityProvider {
		protected ItemStack stack;

		public FluidWrapper(ItemStack stack) {
			this.stack = stack;
		}

		public FluidStack getFluid() {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			return FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("fluid"));
		}

		protected void setFluid(FluidStack fluid) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound tag = fluid == null ? new NBTTagCompound() : fluid.writeToNBT(new NBTTagCompound());
			stack.getTagCompound().setTag("fluid", tag);
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new FluidTankProperties[] { new FluidTankProperties(getFluid(), 1000) };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if (resource == null || resource.amount < 1000) {
				return 0;
			}
			if (doFill) {
				setFluid(new FluidStack(resource.getFluid(), 1000));
			}
			return 1000;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return null;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
				return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
			}
			return null;
		}

		@Override
		public ItemStack getContainer() {
			return stack;
		}
	}
}