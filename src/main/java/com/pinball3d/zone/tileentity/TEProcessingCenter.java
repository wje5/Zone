package com.pinball3d.zone.tileentity;

import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TEProcessingCenter extends TileEntity implements ITickable {
	private String name = "";
	private String adminPassword = "";
	private String loginPassword = "";

	public TEProcessingCenter() {

	}

	public boolean needInit() {
		return name == "";
	}

	public void saveWizardData(String adminPassword, String name, String loginPassword) {
		this.adminPassword = adminPassword;
		this.name = name;
		this.loginPassword = loginPassword;
		markDirty();

	}

	@Override
	public void tick() {

	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound tag = compound.getCompoundTag("sphinx");
		name = tag.getString("name");
		adminPassword = tag.getString("adminPassword");
		loginPassword = tag.getString("loginPassword");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		tag.setString("adminPassword", adminPassword);
		tag.setString("loginPassword", loginPassword);
		compound.setTag("sphinx", tag);
		return super.writeToNBT(compound);
	}
}
