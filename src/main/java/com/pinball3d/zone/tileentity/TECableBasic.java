package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinball3d.zone.util.Pair;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class TECableBasic extends TileEntity implements ITickable {
	protected EnergyStorage energy = new EnergyStorage(0);
	private long skipped;

	public TECableBasic() {

	}

	@Override
	public void update() {
		if (world.isRemote) {
			return;
		}
		if (skipped == world.getTotalWorldTime()) {
			return;
		}
		List<BlockPos> l = new ArrayList<BlockPos>();
		Set<BlockPos> set = new HashSet<BlockPos>();
		Map<TileEntity, Pair<List<EnumFacing>, List<EnumFacing>>> t = new HashMap<TileEntity, Pair<List<EnumFacing>, List<EnumFacing>>>(),
				dynamos = new HashMap<TileEntity, Pair<List<EnumFacing>, List<EnumFacing>>>(),
				capacitors = new HashMap<TileEntity, Pair<List<EnumFacing>, List<EnumFacing>>>(),
				devices = new HashMap<TileEntity, Pair<List<EnumFacing>, List<EnumFacing>>>();
		l.add(pos);
		for (int i = 0; i < l.size(); i++) {
			BlockPos p = l.get(i);
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos p2 = p.offset(facing);
				if (!set.contains(p2) && !l.contains(p2)) {
					TileEntity te = world.getTileEntity(p2);
					if (te instanceof TECableBasic) {
						((TECableBasic) te).skipped = world.getTotalWorldTime();
						l.add(p2);
					} else if (te != null) {
						if (te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
							IEnergyStorage s = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
							boolean extract = s.canExtract(), receive = s.canReceive();
							if (extract || receive) {
								Pair<List<EnumFacing>, List<EnumFacing>> pair = t.get(te);
								if (pair == null) {
									pair = new Pair<List<EnumFacing>, List<EnumFacing>>(new ArrayList<>(),
											new ArrayList<>());
									t.put(te, pair);
								}
								if (extract) {
									pair.key().add(facing.getOpposite());
								}
								if (receive) {
									pair.value().add(facing.getOpposite());
								}
							}
						}
					} else {
						set.add(p2);
					}
				}
			}
		}
		t.forEach((te, p) -> {
			if (p.key().isEmpty()) {
				devices.put(te, p);
			} else if (p.value().isEmpty()) {
				dynamos.put(te, p);
			} else {
				capacitors.put(te, p);
			}
		});
		int totalInput = 0, totalOutput = 0;
		System.out.println("dynamos:" + dynamos);
		System.out.println("capacitors:" + capacitors);
		System.out.println("devices:" + devices);
		System.out.println("#####" + pos);
	}

	public boolean isConnect(EnumFacing facing) {
		TileEntity te = world.getTileEntity(getPos().offset(facing));
		if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
			IEnergyStorage s = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
			if (s.canExtract() || s.canReceive()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			return (T) energy;
		}
		return super.getCapability(capability, facing);
	}

	public boolean canExtractEnergy(EnumFacing facing) {
		return true;
	}

	public boolean canReceiveEnergy(EnumFacing facing) {
		return true;
	}

	public boolean activeOutput(EnumFacing facing) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		return compound;
	}

	private static class DeviceWrapper {
		public final TileEntity tileentity;
		public final List<EnumFacing> input = new ArrayList<EnumFacing>();
		public final List<EnumFacing> output = new ArrayList<EnumFacing>();

		public DeviceWrapper(TileEntity tileentity) {
			this.tileentity = tileentity;
		}
	}
}
