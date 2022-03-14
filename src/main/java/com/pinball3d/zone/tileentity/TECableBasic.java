package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
		Set<BlockPos> noentity = new HashSet<BlockPos>();
		Map<TileEntity, DeviceWrapper> t = new HashMap<TileEntity, DeviceWrapper>();
		Set<DeviceWrapper> dynamos = new HashSet<DeviceWrapper>(), capacitors = new HashSet<DeviceWrapper>(),
				devices = new HashSet<DeviceWrapper>();
		Set<TECableBasic> cables = new HashSet<TECableBasic>();
		l.add(pos);
		cables.add(this);
		for (int i = 0; i < l.size(); i++) {
			BlockPos p = l.get(i);
			TileEntity tileentity = world.getTileEntity(p);
			TECableGeneral cable = null;
			if (tileentity instanceof TECableGeneral) {
				cable = (TECableGeneral) tileentity;
			}
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (cable != null && !cable.getConfig(facing).canEnergyTransmit()) {
					continue;
				}
				BlockPos p2 = p.offset(facing);
				if (!noentity.contains(p2) && !l.contains(p2)) {
					TileEntity te = world.getTileEntity(p2);
					if (te instanceof TECableBasic) {
						TECableGeneral c = null;
						if (te instanceof TECableGeneral) {
							c = (TECableGeneral) te;
						}
						if (c == null || c.getConfig(facing.getOpposite()).canEnergyTransmit()) {
							((TECableBasic) te).skipped = world.getTotalWorldTime();
							l.add(p2);
							cables.add((TECableBasic) te);
						}
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
								}
								if (receive) {
									wrapper.input.add(new Pair<EnumFacing, Integer>(facing.getOpposite(),
											s.receiveEnergy(Integer.MAX_VALUE, true)));
								}
								int store = s.getEnergyStored();
								if (store < wrapper.store) {
									wrapper.store = store;
								}
								int need = s.getMaxEnergyStored() - s.getEnergyStored();
								if (need < wrapper.need) {
									wrapper.need = need;
								}
								int maxStore = s.getMaxEnergyStored();
								if (maxStore < wrapper.maxStore) {
									wrapper.maxStore = maxStore;
								}
							}
						}
					} else {
						noentity.add(p2);
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
//		System.out.println(dynamos + "|" + capacitors + "|" + devices);
		long totalInput = dynamos.stream().mapToLong(e -> e.maxOutputSpeed).sum();
		long totalOutput = devices.stream().mapToLong(e -> e.maxInputSpeed).sum();
		long capacitorOutput = capacitors.stream().mapToLong(e -> e.maxInputSpeed).sum();
//		System.out.println(totalInput + "|" + totalOutput + "|" + capacitorOutput);
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
			long maxCapacitorEnergy = capacitors.stream().mapToLong(e -> e.maxStore).sum();
			long capacitorEnergy = capacitors.stream().mapToLong(e -> e.store).sum() + energy;
			float store = capacitorEnergy * 1.0F / maxCapacitorEnergy;
			it = capacitors.iterator();
			while (it.hasNext()) {
				DeviceWrapper w = it.next();
				float f = w.store * 1.0F / w.maxStore;
				if (f >= store) {
					continue;
				}
				int input = Math.min((int) Math.min(Integer.MAX_VALUE, energy), (int) ((store - f) * w.maxStore));
				if (w.maxInputSpeed < input) {
					energy -= w.receive(w.maxInputSpeed);
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
				maxCapacitorEnergy = capacitors.stream().mapToLong(e -> e.maxStore).sum();
				capacitorEnergy = capacitors.stream().mapToLong(e -> e.store).sum() + energy;
				store = capacitorEnergy * 1.0F / maxCapacitorEnergy;
				float f = w.store * 1.0F / w.maxStore;
				if (f >= store) {
					continue;
				}
				int input = Math.min((int) Math.min(Integer.MAX_VALUE, energy), (int) ((store - f) * w.maxStore));
				energy -= w.receive(Math.min(w.maxInputSpeed, input));
				it.remove();
				if (energy <= 0) {
					break;
				}
			}
			if (energy < 0) {
				System.out.println("DRRR:" + energy);
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
		networkEnergy = t.values().stream().mapToLong(e -> e.store).sum();
		networkMaxEnergy = t.values().stream().mapToLong(e -> e.maxStore).sum();
		cables.forEach(e -> {
			e.networkEnergy = networkEnergy;
			e.networkMaxEnergy = networkMaxEnergy;
			e.networkInput = networkInput;
			e.networkOutput = networkOutput;

		});
//		System.out.println(networkEnergy + "|" + networkMaxEnergy + "|" + networkInput + "|" + networkOutput);
//		System.out.println("==================");
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

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		networkEnergy = compound.getLong("networkEnergy");
		networkMaxEnergy = compound.getLong("networkMaxEnergy");
		networkInput = compound.getLong("networkInput");
		networkOutput = compound.getLong("networkOutput");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setLong("networkEnergy", networkEnergy);
		compound.setLong("networkMaxEnergy", networkMaxEnergy);
		compound.setLong("networkInput", networkInput);
		compound.setLong("networkOutput", networkOutput);
		return compound;
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
		public int store = Integer.MAX_VALUE, need = Integer.MAX_VALUE, maxStore = Integer.MAX_VALUE;
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
