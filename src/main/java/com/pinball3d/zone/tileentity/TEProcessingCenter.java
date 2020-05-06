package com.pinball3d.zone.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TEProcessingCenter extends TileEntity implements ITickable {
	private String name = "";
	private String adminPassword = "";
	private String loginPassword = "";

	public TEProcessingCenter() {

	}

	public boolean needInit() {
		return name == "";
	}

	public String getName() {
		return name;
	}

	public boolean isCorrectAdminPassword(String password) {
		return password.equals(adminPassword);
	}

	public boolean isCorrectLoginPassword(String password) {
		return password.equals(loginPassword);
	}

	public void saveWizardData(String adminPassword, String name, String loginPassword) {
		this.adminPassword = adminPassword;
		this.name = name;
		this.loginPassword = loginPassword;
		markDirty();
	}

	@Override
	public void update() {
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		name = compound.getString("name");
		adminPassword = compound.getString("adminPassword");
		loginPassword = compound.getString("loginPassword");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("name", name);
		compound.setString("adminPassword", adminPassword);
		compound.setString("loginPassword", loginPassword);
		return super.writeToNBT(compound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
}
