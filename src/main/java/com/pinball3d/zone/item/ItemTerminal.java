package com.pinball3d.zone.item;

import java.util.UUID;

import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.sphinx.GlobalNetworkData;
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TEProcessingCenter;
import com.pinball3d.zone.util.WorldPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.server.command.TextComponentHelper;

public class ItemTerminal extends ZoneItem {
	public ItemTerminal() {
		super("terminal");
		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (playerIn.isSneaking()) {
			stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
		} else if (!worldIn.isRemote && stack.getItemDamage() == 0) {
//			openScreen(playerIn.getHeldItem(handIn));
			playerIn.openGui(Zone.instance, GuiElementLoader.SPHINX_TERMINAL, worldIn, (int) playerIn.posX,
					(int) playerIn.posY, (int) playerIn.posZ);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() == 1) {
			if (!worldIn.isRemote) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
					stack.setTagCompound(tag);
				}
				WorldPos p = new WorldPos(pos, worldIn);
				TileEntity tileentity = p.getTileEntity();
				if (tileentity instanceof INeedNetwork) {
					if (tag.hasUniqueId("network")) {
						UUID uuid = tag.getUniqueId("network");
						String password = tag.getString("password");
						if (!password.isEmpty()) {
							INeedNetwork te = (INeedNetwork) tileentity;
							GlobalNetworkData data = GlobalNetworkData.getData(worldIn);
							WorldPos network = data.getNetwork(uuid);
							if (network != null) {
								TileEntity t = network.getTileEntity();
								if (t instanceof TEProcessingCenter) {
									TEProcessingCenter pc = (TEProcessingCenter) t;
									if (pc.isDeviceInRange(p)) {
										if (pc.isCorrectLoginPassword(password)) {
											te.connect(uuid, password);
											te.setWorldPos(network, uuid);
											pc.addNeedNetwork(p);
											player.sendMessage(TextComponentHelper.createComponentTranslation(player,
													"sphinx.connect_to_network", pc.getName()));
											return EnumActionResult.SUCCESS;
										} else {
											player.sendMessage(TextComponentHelper.createComponentTranslation(player,
													"sphinx.password_incorrect"));
										}
									} else {
										player.sendMessage(TextComponentHelper.createComponentTranslation(player,
												"sphinx.no_network"));
									}
								} else {
									player.sendMessage(TextComponentHelper.createComponentTranslation(player,
											"sphinx.no_network"));
								}
							} else {
								player.sendMessage(
										TextComponentHelper.createComponentTranslation(player, "sphinx.no_network"));
							}
						} else {
							player.sendMessage(
									TextComponentHelper.createComponentTranslation(player, "sphinx.no_network"));
						}
					} else {
						player.sendMessage(TextComponentHelper.createComponentTranslation(player, "sphinx.no_network"));
					}
					return EnumActionResult.FAIL;
				}
			}
		}
		return EnumActionResult.PASS;

	}

//	@SideOnly(Side.CLIENT)
//	public void openScreen(ItemStack stack) {
//		Minecraft.getMinecraft().displayGuiScreen(new ScreenTerminal(stack));
//		Minecraft.getMinecraft().displayGuiScreen(
//				new GuiContainerSphinxController(new ContainerSphinxBase(Minecraft.getMinecraft().player)));
//	}
}
