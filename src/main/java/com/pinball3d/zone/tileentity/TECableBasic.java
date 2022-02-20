package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinball3d.zone.util.Pair;

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
	public long networkEnergy, networkMaxEnergy, networkInput, networkOutput;

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
		Map<TileEntity, DeviceWrapper> t = new HashMap<TileEntity, DeviceWrapper>();
		Set<DeviceWrapper> dynamos = new HashSet<DeviceWrapper>(), capacitors = new HashSet<DeviceWrapper>(),
				devices = new HashSet<DeviceWrapper>();
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
								DeviceWrapper wrapper = t.get(te);
								if (wrapper == null) {
									wrapper = new DeviceWrapper(te);
									t.put(te, wrapper);
								}
								if (extract) {
									wrapper.output.add(new Pair<EnumFacing, Integer>(facing.getOpposite(),
											s.extractEnergy(Integer.MAX_VALUE, true)));
									int store = s.getEnergyStored();
									if (store < wrapper.store) {
										wrapper.store = store;
									}
								}
								if (receive) {
									wrapper.input.add(new Pair<EnumFacing, Integer>(facing.getOpposite(),
											s.receiveEnergy(Integer.MAX_VALUE, true)));
									int need = s.getMaxEnergyStored() - s.getEnergyStored();
									if (need < wrapper.store) {
										wrapper.need = need;
									}
								}
							}
						}
					} else {
						set.add(p2);
					}
				}
			}
		}
		t.forEach((te, wrapper) -> {
			if (wrapper.output.isEmpty()) {
				devices.add(wrapper);
				long temp = wrapper.input.stream().mapToLong(e -> e.value()).sum();
				wrapper.maxInputSpeed = Math.min(wrapper.need,
						temp >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) temp);
			} else if (wrapper.input.isEmpty()) {
				dynamos.add(wrapper);
				long temp = wrapper.output.stream().mapToLong(e -> e.value()).sum();
				wrapper.maxOutputSpeed = Math.min(wrapper.store,
						temp >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) temp);
			} else {
				capacitors.add(wrapper);
				long temp = wrapper.input.stream().mapToLong(e -> e.value()).sum();
				wrapper.maxInputSpeed = Math.min(wrapper.need,
						temp >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) temp);
				temp = wrapper.output.stream().mapToLong(e -> e.value()).sum();
				wrapper.maxOutputSpeed = Math.min(wrapper.need,
						temp >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) temp);
			}
		});
		long totalInput = dynamos.stream().mapToLong(e -> e.maxOutputSpeed).sum();
		long totalOutput = devices.stream().mapToLong(e -> e.maxInputSpeed).sum();
		long capacitorOutput = capacitors.stream().mapToLong(e -> e.maxInputSpeed).sum();
		if (totalInput >= totalOutput) {
			long energy = Math.min(totalInput, totalOutput + capacitorOutput);
			float avg = energy * 1.0F / dynamos.size();
			Iterator<DeviceWrapper> it = dynamos.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				if (w.maxOutputSpeed < avg) {
					energy -= w.extract(energy >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy);
					it.remove();
					if (energy <= 0) {
						break;
					}
				}
			}
			it = dynamos.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				avg = energy * 1.0F / dynamos.size();
				energy -= w.extract(Math.round(avg));
				it.remove();
				if (energy <= 0) {
					break;
				}
			}
			energy = Math.min(totalInput, totalOutput + capacitorOutput) - energy;
			networkInput = energy;
			for (DeviceWrapper e : devices) {
				energy -= e.receive(e.maxInputSpeed);
				if (energy <= 0) {
					break;
				}
			}
			avg = energy * 1.0F / capacitors.size();
			it = capacitors.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				if (w.maxInputSpeed < avg) {
					energy -= w.extract(energy >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy);
					it.remove();
					if (energy <= 0) {
						break;
					}
				}
			}
			networkOutput = networkInput - energy;
			it = capacitors.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				avg = energy * 1.0F / capacitors.size();
				energy -= w.receive(Math.round(avg));
				it.remove();
				if (energy <= 0) {
					break;
				}
			}
		} else {
			long energy = totalOutput;
			for (DeviceWrapper e : dynamos) {
				energy -= e.extract(e.maxOutputSpeed);
				if (energy <= 0) {
					break;
				}
			}
			networkInput = totalOutput - energy;
			float avg = energy * 1.0F / capacitors.size();
			Iterator<DeviceWrapper> it = capacitors.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				if (w.maxOutputSpeed < avg) {
					energy -= w.extract(energy >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy);
					it.remove();
					if (energy <= 0) {
						break;
					}
				}
			}
			it = capacitors.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				avg = energy * 1.0F / capacitors.size();
				energy -= w.extract(Math.round(avg));
				it.remove();
				if (energy <= 0) {
					break;
				}
			}
			energy = totalOutput - energy;
			networkOutput = energy;
			it = devices.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				if (w.maxInputSpeed < avg) {
					energy -= w.receive(energy >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy);
					it.remove();
					if (energy <= 0) {
						break;
					}
				}
			}
			it = devices.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				avg = energy * 1.0F / devices.size();
				energy -= w.receive(Math.round(avg));
				it.remove();
				if (energy <= 0) {
					break;
				}
			}
			networkOutput -= energy;
		}
	}

	public boolean isConnect(EnumFacing facing) {
		TileEntity te = world.getTileEntity(getPos().offset(facing));
		if (te instanceof TECableBasic) {
			return true;
		}
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

	private static class DeviceWrapper {
		public final TileEntity tileentity;
		public final List<Pair<EnumFacing, Integer>> input = new ArrayList<Pair<EnumFacing, Integer>>();
		public final List<Pair<EnumFacing, Integer>> output = new ArrayList<Pair<EnumFacing, Integer>>();
		public int store = Integer.MAX_VALUE, need = Integer.MAX_VALUE;
		public int maxInputSpeed, maxOutputSpeed;

		public DeviceWrapper(TileEntity tileentity) {
			this.tileentity = tileentity;
		}

		public int receive(int amount) {
			int received = 0;
			for (Pair<EnumFacing, Integer> p : input) {
				IEnergyStorage s = tileentity.getCapability(CapabilityEnergy.ENERGY, p.key());
				received += s.receiveEnergy(amount - received, false);
				if (amount <= received) {
					return received;
				}
			}
			return received;
		}

		public int extract(int amount) {
			int extracted = 0;
			for (Pair<EnumFacing, Integer> p : output) {
				IEnergyStorage s = tileentity.getCapability(CapabilityEnergy.ENERGY, p.key());
				extracted += s.extractEnergy(amount - extracted, false);
				if (amount <= extracted) {
					return extracted;
				}
			}
			return extracted;
		}
	}
}
