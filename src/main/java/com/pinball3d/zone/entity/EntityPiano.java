package com.pinball3d.zone.entity;

import com.pinball3d.zone.instrument.ScreenPiano;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPiano extends Entity {
	public EntityPiano(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			openScreen();
		}
		player.startRiding(this, true);
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void openScreen() {
		Minecraft.getMinecraft().displayGuiScreen(new ScreenPiano());
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {

	}
}
