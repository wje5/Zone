package com.pinball3d.zone.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig.ItemIOType;
import com.pinball3d.zone.util.HugeItemStack;
import com.pinball3d.zone.util.StorageWrapper;
import com.pinball3d.zone.util.Util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TECableGeneral extends TECableBasic {
	private CableConfig[] configs = new CableConfig[] { new CableConfig(), new CableConfig(), new CableConfig(),
			new CableConfig(), new CableConfig(), new CableConfig() };
	private long skipped;

	public TECableGeneral() {

	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		if (skipped == world.getTotalWorldTime()) {
			return;
		}
		List<BlockPos> l = new ArrayList<BlockPos>();
		Set<BlockPos> noentity = new HashSet<BlockPos>();
		List<IODeviceWrapper> inputs = new ArrayList<IODeviceWrapper>(), outputs = new ArrayList<IODeviceWrapper>(),
				storages = new ArrayList<IODeviceWrapper>();
		Set<TECableGeneral> cables = new HashSet<TECableGeneral>();
		l.add(pos);
		cables.add(this);
		for (int i = 0; i < l.size(); i++) {
			BlockPos p = l.get(i);
			TECableGeneral cable = (TECableGeneral) world.getTileEntity(p);
			for (EnumFacing facing : EnumFacing.VALUES) {
				ItemIOType type = cable.getConfig(facing).getItemIOType();
				if (type == ItemIOType.DISABLE) {
					continue;
				}
				BlockPos p2 = p.offset(facing);
				if (!noentity.contains(p2) && !l.contains(p2)) {
					TileEntity te = world.getTileEntity(p2);
					if (te instanceof TECableGeneral) {
						if (((TECableGeneral) te).getConfig(facing.getOpposite())
								.getItemIOType() != ItemIOType.DISABLE) {
							((TECableGeneral) te).skipped = world.getTotalWorldTime();
							l.add(p2);
							cables.add((TECableGeneral) te);
						}
					} else if (te != null) {
						if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
							IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
									facing.getOpposite());
							switch (type) {
							case INPUT:
								outputs.add(new IODeviceWrapper(handler, cable.getConfig(facing)));
								break;
							case OUTPUT:
								inputs.add(new IODeviceWrapper(handler, cable.getConfig(facing)));
								break;
							case STORAGE:
								storages.add(new IODeviceWrapper(handler, cable.getConfig(facing)));
								break;
							case DISABLE:
								break;
							}
						}
					} else {
						noentity.add(p2);
					}
				}
			}
		}
		List<List<ItemStack>> inputData = new ArrayList<List<ItemStack>>();
		inputs.forEach(e -> {
			List<ItemStack> list = new ArrayList<ItemStack>();
			for (int i = 0; i < e.handler.getSlots(); i++) {
				ItemStack stack = e.handler.getStackInSlot(i);
				ItemStack s = ItemStack.EMPTY;
				boolean flag = true;
				for (int j = 0; j < 15; j++) {
					if (!e.config.whitelist[j].isEmpty()) {
						flag = false;
					}
					if (Util.isItemStackEqualEgnoreCount(e.config.whitelist[j], stack)) {
						flag = true;
						break;
					}
				}
				if (flag) {
					s = e.handler.extractItem(i, stack.getCount(), true).copy();// XXX need not copy
				}
				list.add(s);
			}
			inputData.add(list);
		});
		// System.out.println("inputData:" + inputData);
		List<StorageWrapper> inputWrappers = inputData.stream().map(e -> {
			StorageWrapper w = new StorageWrapper();
			e.forEach(stack -> w.merge(stack));
			return w;
		}).collect(Collectors.toList());
		// System.out.println("inputWrappers:" + inputWrappers);

		StorageWrapper inputTotal = new StorageWrapper();
		inputWrappers.forEach(e -> inputTotal.merge(e));
		// System.out.println("inputTotal:" + inputTotal);

		List<ItemStack> fix = new ArrayList<ItemStack>();
		inputTotal.storges.forEach(hugestack -> {
			int[][] insertData = new int[outputs.size()][];
			int[] maxInsert = new int[outputs.size()];
			Iterator<IODeviceWrapper> it = outputs.iterator();
			for (int i = 0; i < outputs.size(); i++) {
				IODeviceWrapper e = it.next();
				insertData[i] = new int[e.handler.getSlots()];
				for (int j = 0; j < 15; j++) {
					if (Util.isItemStackEqualEgnoreCount(e.config.whitelist[j], hugestack.stack)) {
						for (int k = 0; k < e.handler.getSlots(); k++) {
							ItemStack stack = hugestack.stack.copy();
							int amount = Math.min(hugestack.count, hugestack.stack.getMaxStackSize());
							stack.setCount(amount);
							insertData[i][k] = amount - e.handler.insertItem(k, stack, true).getCount();
						}
						break;
					}
				}
				maxInsert[i] = Arrays.stream(insertData[i]).sum();
			}
//			//System.out.println("insertData:" + Arrays.toString(insertData));
			// System.out.println("maxInsert:" + Arrays.toString(maxInsert));
			int maxInsertTotal = Arrays.stream(maxInsert).sum();
			int[] maxExtract = new int[inputWrappers.size()];
			for (int i = 0; i < inputWrappers.size(); i++) {
				StorageWrapper w = inputWrappers.get(i);
				Iterator<HugeItemStack> it2 = w.storges.iterator();
				while (it2.hasNext()) {
					HugeItemStack e = it2.next();
					if (Util.isItemStackEqualEgnoreCount(hugestack.stack, e.stack)) {
						maxExtract[i] = e.count;
						break;
					}
				}
			}
			// System.out.println("maxExtract:" + Arrays.toString(maxExtract));
			int[] maxExtractAvg = getAvg(maxExtract, maxInsertTotal);
			for (int i = 0; i < maxExtract.length; i++) {
				maxExtract[i] -= maxExtractAvg[i];
			}
			// System.out.println("maxExtract(avg):" + Arrays.toString(maxExtractAvg));
			int maxExtractTotal = Arrays.stream(maxExtractAvg).sum();
			int[] maxInsertAvg = getAvg(maxInsert, maxExtractTotal);
			for (int i = 0; i < maxInsert.length; i++) {
				maxInsert[i] -= maxInsertAvg[i];
			}
			// System.out.println("maxInsert(avg):" + Arrays.toString(maxInsertAvg));
			it = inputs.iterator();
			Iterator<List<ItemStack>> it2 = inputData.iterator();
			ItemStack stack = ItemStack.EMPTY;
			for (int i = 0; i < maxExtractAvg.length; i++) {
				IODeviceWrapper device = it.next();
				List<ItemStack> list = it2.next();
				if (maxExtractAvg[i] > 0) {
					for (int j = 0; j < list.size(); j++) {
						if (Util.isItemStackEqualEgnoreCount(hugestack.stack, list.get(j))) {
							stack = device.handler.extractItem(j, Math.min(maxExtractAvg[i], list.get(j).getCount()),
									false);
							if (!stack.isEmpty()) {
								// System.out.println("start:" + stack);
								list.get(j).shrink(stack.getCount());
								maxExtractAvg[i] -= stack.getCount();
								while (!stack.isEmpty()) {
									Iterator<IODeviceWrapper> it3 = outputs.iterator();
									for (int k = 0; k < maxInsertAvg.length && !stack.isEmpty(); k++) {
										IODeviceWrapper insertDevice = it3.next();
										for (int index = 0; index < insertData[k].length && !stack.isEmpty()
												&& maxInsertAvg[k] > 0; index++) {
											if (insertData[k][index] > 0) {
												ItemStack s = stack.copy();
												int amount = Math.min(stack.getCount(),
														Math.min(maxInsertAvg[k], insertData[k][index]));
												s.setCount(amount);
												// System.out.println("before insert:" + s);
												if (s.isEmpty()) {
													// System.out.println(s);
												}
												s = insertDevice.handler.insertItem(index, s, false);
												// System.out.println("after insert:" + s);
												amount = amount - s.getCount();
												if (amount == 0) {
													// System.out.println("DRR");// debug
												}
												maxInsertAvg[k] -= amount;
												insertData[k][index] -= amount;
												stack.shrink(amount);
											}
										}
									}
									if (!stack.isEmpty()) {
										maxInsertAvg = getAvg(maxInsert, stack.getCount());
										boolean flag = true;
										for (int k = 0; k < maxInsert.length; k++) {
											if (maxInsertAvg[k] > 0) {
												flag = false;
											}
											maxInsert[k] -= maxInsertAvg[k];
										}
										if (flag) {
											// System.out.println("DRRRRRR");
											break;
										}
									}
								}
								// System.out.println("end:" + stack);
								if (maxExtractAvg[i] <= 0) {
									break;
								}
							}
						}
					}
				}
			}
			maxExtractTotal = Arrays.stream(maxExtract).sum() + stack.getCount();
			if (maxExtractTotal > 0) {
				// System.out.println("remain:maxExtract:" + Arrays.toString(maxExtract));
				insertData = new int[storages.size()][];
				maxInsert = new int[storages.size()];
				it = storages.iterator();
				maxInsertTotal = 0;
				int i = 0, k = 0;
				it = storages.iterator();
				tag: for (; i < storages.size(); i++) {
					IODeviceWrapper e = it.next();
					insertData[i] = new int[e.handler.getSlots()];
					boolean flag = true;
					for (int j = 0; j < 15; j++) {
						if (!e.config.whitelist[j].isEmpty()) {
							flag = false;
						}
						if (Util.isItemStackEqualEgnoreCount(e.config.whitelist[j], hugestack.stack)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						for (; k < e.handler.getSlots(); k++) {
							ItemStack s = hugestack.stack.copy();
							int amount = Math.min(maxExtractTotal, hugestack.stack.getMaxStackSize());
							s.setCount(amount);
							amount = amount - e.handler.insertItem(k, s, true).getCount();
							insertData[i][k] = amount;
							maxInsertTotal += amount;
							maxInsert[i] += amount;
							if (maxInsertTotal >= maxExtractTotal) {
								break tag;
							}
						}
					}
					if (i < storages.size() - 1) {
						k = 0;
					}
				}

				int a = 0;
				// DATA CORRECTION
				if (!stack.isEmpty()) {
					for (; a <= i && a < storages.size(); a++) {
						IODeviceWrapper insertDevice = storages.get(a);
						// System.out.println("debug:" + a);
						for (int index2 = 0; index2 < insertData[a].length && index2 <= k && !stack.isEmpty()
								&& maxInsert[a] > 0; index2++) {
							// System.out.println("debug:" + a + "|" + index2);
							if (insertData[a][index2] > 0) {
								ItemStack s = stack.copy();
								int amount = Math.min(stack.getCount(), Math.min(maxInsert[a], insertData[a][index2]));
								s.setCount(amount);
								// System.out.println("before insert:" + s);
								if (s.isEmpty()) {
									// System.out.println(s);
								}
								s = insertDevice.handler.insertItem(index2, s, false);
								// System.out.println("after insert:" + s);
								amount = amount - s.getCount();
								maxInsert[a] -= amount;
								insertData[a][index2] -= amount;
								stack.shrink(amount);
							}
						}
						if (stack.isEmpty()) {
							break;
						}
					}
				}

				// System.out.println(i + "|" + k + "|" + maxInsertTotal);
				maxExtractAvg = getAvg(maxExtract, maxInsertTotal);
				for (int index = 0; index < maxExtract.length; index++) {
					maxExtract[index] -= maxExtractAvg[index];
				}
				// System.out.println(Arrays.toString(maxExtractAvg));

				it = inputs.iterator();
				it2 = inputData.iterator();
				for (int index = 0; index < maxExtractAvg.length; index++) {
					IODeviceWrapper device = it.next();
					List<ItemStack> list = it2.next();
					if (maxExtractAvg[index] > 0) {
						for (int j = 0; j < list.size(); j++) {
							if (Util.isItemStackEqualEgnoreCount(hugestack.stack, list.get(j))) {
								stack = device.handler.extractItem(j,
										Math.min(maxExtractAvg[index], list.get(j).getCount()), false);
								if (!stack.isEmpty()) {
									// System.out.println("start:" + stack + "|" + i);
									list.get(j).shrink(stack.getCount());
									maxExtractAvg[index] -= stack.getCount();
									for (; a <= i && a < storages.size() && !stack.isEmpty(); a++) {
										IODeviceWrapper insertDevice = storages.get(a);
										// System.out.println("debug:" + a);
										for (int index2 = 0; index2 < insertData[a].length && index2 <= k
												&& !stack.isEmpty() && maxInsert[a] > 0; index2++) {
											// System.out.println("debug:" + a + "|" + index2);
											if (insertData[a][index2] > 0) {
												ItemStack s = stack.copy();
												int amount = Math.min(stack.getCount(),
														Math.min(maxInsert[a], insertData[a][index2]));
												s.setCount(amount);
												// System.out.println("before insert:" + s);
												if (s.isEmpty()) {
													// System.out.println(s);
												}
												s = insertDevice.handler.insertItem(index2, s, false);
												// System.out.println("after insert:" + s);
												amount = amount - s.getCount();
												maxInsert[a] -= amount;
												insertData[a][index2] -= amount;
												stack.shrink(amount);
											}
										}
										if (stack.isEmpty()) {
											break;
										}
									}
									// System.out.println("end:" + stack);
									if (!stack.isEmpty()) {
										// System.out.println("DRRRRRR1" + stack);
										fix.add(stack);
									}
									if (maxExtractAvg[index] <= 0) {
										break;
									}
								}
							}
						}
					}
				}
			}
		});
		List<List<ItemStack>> storageData = new ArrayList<List<ItemStack>>();
		storages.forEach(e -> {
			List<ItemStack> list = new ArrayList<ItemStack>();
			for (int i = 0; i < e.handler.getSlots(); i++) {
				ItemStack stack = e.handler.getStackInSlot(i);
				ItemStack s = ItemStack.EMPTY;
				boolean flag = true;
				for (int j = 0; j < 15; j++) {
					if (!e.config.whitelist[j].isEmpty()) {
						flag = false;
					}
					if (Util.isItemStackEqualEgnoreCount(e.config.whitelist[j], stack)) {
						flag = true;
						break;
					}
				}
				if (flag) {
					s = e.handler.extractItem(i, stack.getCount(), true).copy();// XXX need not copy
				}
				list.add(s);
			}
			storageData.add(list);
		});
		// System.out.println("storageData:" + storageData);
		List<StorageWrapper> storageWrappers = storageData.stream().map(e -> {
			StorageWrapper w = new StorageWrapper();
			e.forEach(stack -> w.merge(stack));
			return w;
		}).collect(Collectors.toList());
		// System.out.println("storageWrappers:" + storageWrappers);

		StorageWrapper storageTotal = new StorageWrapper();
		storageWrappers.forEach(e -> storageTotal.merge(e));
		// System.out.println("storageTotal:" + storageTotal);

		storageTotal.storges.forEach(hugestack -> {
			int[][] insertData = new int[outputs.size()][];
			int[] maxInsert = new int[outputs.size()];
			Iterator<IODeviceWrapper> it = outputs.iterator();
			for (int i = 0; i < outputs.size(); i++) {
				IODeviceWrapper e = it.next();
				insertData[i] = new int[e.handler.getSlots()];
				for (int j = 0; j < 15; j++) {
					if (Util.isItemStackEqualEgnoreCount(e.config.whitelist[j], hugestack.stack)) {
						for (int k = 0; k < e.handler.getSlots(); k++) {
							ItemStack stack = hugestack.stack.copy();
							int amount = Math.min(hugestack.count, hugestack.stack.getMaxStackSize());
							stack.setCount(amount);
							insertData[i][k] = amount - e.handler.insertItem(k, stack, true).getCount();
						}
						break;
					}
				}
				maxInsert[i] = Arrays.stream(insertData[i]).sum();
			}
//			//System.out.println("insertData:" + Arrays.toString(insertData));
			// System.out.println("maxInsert:" + Arrays.toString(maxInsert));
			int maxInsertTotal = Arrays.stream(maxInsert).sum();
			int[] maxExtract = new int[storageWrappers.size()];
			for (int i = 0; i < storageWrappers.size(); i++) {
				StorageWrapper w = storageWrappers.get(i);
				Iterator<HugeItemStack> it2 = w.storges.iterator();
				while (it2.hasNext()) {
					HugeItemStack e = it2.next();
					if (Util.isItemStackEqualEgnoreCount(hugestack.stack, e.stack)) {
						maxExtract[i] = e.count;
						break;
					}
				}
			}
			// System.out.println("maxExtract:" + Arrays.toString(maxExtract));
			int[] maxExtractAvg = getAvg(maxExtract, maxInsertTotal);
			for (int i = 0; i < maxExtract.length; i++) {
				maxExtract[i] -= maxExtractAvg[i];
			}
			// System.out.println("maxExtract(avg):" + Arrays.toString(maxExtractAvg));
			int maxExtractTotal = Arrays.stream(maxExtractAvg).sum();
			int[] maxInsertAvg = getAvg(maxInsert, maxExtractTotal);
			for (int i = 0; i < maxInsert.length; i++) {
				maxInsert[i] -= maxInsertAvg[i];
			}
			// System.out.println("maxInsert(avg):" + Arrays.toString(maxInsertAvg));
			it = storages.iterator();
			Iterator<List<ItemStack>> it2 = storageData.iterator();
			ItemStack stack = ItemStack.EMPTY;
			for (int i = 0; i < maxExtractAvg.length; i++) {
				IODeviceWrapper device = it.next();
				List<ItemStack> list = it2.next();
				if (maxExtractAvg[i] > 0) {
					for (int j = 0; j < list.size(); j++) {
						if (Util.isItemStackEqualEgnoreCount(hugestack.stack, list.get(j))) {
							stack = device.handler.extractItem(j, Math.min(maxExtractAvg[i], list.get(j).getCount()),
									false);
							if (!stack.isEmpty()) {
								// System.out.println("start:" + stack);
								list.get(j).shrink(stack.getCount());
								maxExtractAvg[i] -= stack.getCount();
								while (!stack.isEmpty()) {
									Iterator<IODeviceWrapper> it3 = outputs.iterator();
									for (int k = 0; k < maxInsertAvg.length && !stack.isEmpty(); k++) {
										IODeviceWrapper insertDevice = it3.next();
										for (int index = 0; index < insertData[k].length && !stack.isEmpty()
												&& maxInsertAvg[k] > 0; index++) {
											if (insertData[k][index] > 0) {
												ItemStack s = stack.copy();
												int amount = Math.min(stack.getCount(),
														Math.min(maxInsertAvg[k], insertData[k][index]));
												s.setCount(amount);
												// System.out.println("before insert:" + s);
												s = insertDevice.handler.insertItem(index, s, false);
												// System.out.println("after insert:" + s);
												amount = amount - s.getCount();
												if (amount == 0) {
													// System.out.println("DRR");// debug
												}
												maxInsertAvg[k] -= amount;
												insertData[k][index] -= amount;
												stack.shrink(amount);
											}
										}
									}
									if (!stack.isEmpty()) {
										maxInsertAvg = getAvg(maxInsert, stack.getCount());
										boolean flag = true;
										for (int k = 0; k < maxInsert.length; k++) {
											if (maxInsertAvg[k] > 0) {
												flag = false;
											}
											maxInsert[k] -= maxInsertAvg[k];
										}
										if (flag) {
											// System.out.println("DRRRRRR2" + stack);
											fix.add(stack);
											break;
										}
									}
								}
								// System.out.println("end:" + stack);
								if (maxExtractAvg[i] <= 0) {
									break;
								}
							}
						}
					}
				}
			}
		});

		// DATA CORRECTION
		fix.forEach(stack -> {
			for (int i = 0; i < storages.size(); i++) {
				IODeviceWrapper insertDevice = storages.get(i);
				for (int j = 0; j < insertDevice.handler.getSlots(); j++) {
					stack = insertDevice.handler.insertItem(j, stack, false);
					if (stack.isEmpty()) {
						return;
					}
				}
			}
		});

		///////////////
		// System.out.println("inputData:" + inputData);
		inputWrappers.clear();
		inputWrappers.addAll(inputData.stream().map(e -> {
			StorageWrapper w = new StorageWrapper();
			e.forEach(stack -> w.merge(stack));
			return w;
		}).collect(Collectors.toList()));
		// System.out.println("inputWrappers:" + inputWrappers);

		inputTotal.clear();
		inputWrappers.forEach(e -> inputTotal.merge(e));
		// System.out.println("inputTotal:" + inputTotal);

		inputTotal.storges.forEach(hugestack -> {
			int[] maxExtract = new int[inputWrappers.size()];
			for (int i = 0; i < inputWrappers.size(); i++) {
				StorageWrapper w = inputWrappers.get(i);
				Iterator<HugeItemStack> it2 = w.storges.iterator();
				while (it2.hasNext()) {
					HugeItemStack e = it2.next();
					if (Util.isItemStackEqualEgnoreCount(hugestack.stack, e.stack)) {
						maxExtract[i] = e.count;
						break;
					}
				}
			}

			int maxExtractTotal = Arrays.stream(maxExtract).sum();
			if (maxExtractTotal > 0) {
				// System.out.println("remain:maxExtract:" + Arrays.toString(maxExtract));
				int[][] insertData = new int[storages.size()][];
				int[] maxInsert = new int[storages.size()];
				Iterator<IODeviceWrapper> it = storages.iterator();
				int maxInsertTotal = 0;
				int i = 0, k = 0;
				it = storages.iterator();
				tag: for (; i < storages.size(); i++) {
					IODeviceWrapper e = it.next();
					insertData[i] = new int[e.handler.getSlots()];
					boolean flag = true;
					for (int j = 0; j < 15; j++) {
						if (!e.config.whitelist[j].isEmpty()) {
							flag = false;
						}
						if (Util.isItemStackEqualEgnoreCount(e.config.whitelist[j], hugestack.stack)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						for (; k < e.handler.getSlots(); k++) {
							ItemStack s = hugestack.stack.copy();
							int amount = Math.min(maxExtractTotal, hugestack.stack.getMaxStackSize());
							s.setCount(amount);
							amount = amount - e.handler.insertItem(k, s, true).getCount();
							insertData[i][k] = amount;
							maxInsertTotal += amount;
							maxInsert[i] += amount;
							if (maxInsertTotal >= maxExtractTotal) {
								break tag;
							}
						}
					}
					if (i < storages.size() - 1) {
						k = 0;
					}
				}

				int a = 0;
				// DATA

				// System.out.println(i + "|" + k + "|" + maxInsertTotal);
				int[] maxExtractAvg = getAvg(maxExtract, maxInsertTotal);
				for (int index = 0; index < maxExtract.length; index++) {
					maxExtract[index] -= maxExtractAvg[index];
				}
				// System.out.println(Arrays.toString(maxExtractAvg));

				it = inputs.iterator();
				Iterator<List<ItemStack>> it2 = inputData.iterator();
				for (int index = 0; index < maxExtractAvg.length; index++) {
					IODeviceWrapper device = it.next();
					List<ItemStack> list = it2.next();
					if (maxExtractAvg[index] > 0) {
						for (int j = 0; j < list.size(); j++) {
							if (Util.isItemStackEqualEgnoreCount(hugestack.stack, list.get(j))) {
								ItemStack stack = device.handler.extractItem(j,
										Math.min(maxExtractAvg[index], list.get(j).getCount()), false);
								if (!stack.isEmpty()) {
									// System.out.println("start:" + stack + "|" + i);
									list.get(j).shrink(stack.getCount());
									maxExtractAvg[index] -= stack.getCount();
									for (; a <= i && a < storages.size() && !stack.isEmpty(); a++) {
										IODeviceWrapper insertDevice = storages.get(a);
										// System.out.println("debug:" + a);
										for (int index2 = 0; index2 < insertData[a].length && index2 <= k
												&& !stack.isEmpty() && maxInsert[a] > 0; index2++) {
											// System.out.println("debug:" + a + "|" + index2);
											if (insertData[a][index2] > 0) {
												ItemStack s = stack.copy();
												int amount = Math.min(stack.getCount(),
														Math.min(maxInsert[a], insertData[a][index2]));
												s.setCount(amount);
												// System.out.println("before insert:" + s);
												if (s.isEmpty()) {
													// System.out.println(s);
												}
												s = insertDevice.handler.insertItem(index2, s, false);
												// System.out.println("after insert:" + s);
												amount = amount - s.getCount();
												maxInsert[a] -= amount;
												insertData[a][index2] -= amount;
												stack.shrink(amount);
											}
										}
										if (stack.isEmpty()) {
											break;
										}
									}
									// System.out.println("end:" + stack);
									if (!stack.isEmpty()) {
										// System.out.println("DRRRRRR3" + stack);
									}
									if (maxExtractAvg[index] <= 0) {
										break;
									}
								}
							}
						}
					}
				}
			}
		});

	}

	public static int[] getAvg(int[] array, int total) {
		// System.out.println("total:" + total + " old:" + Arrays.toString(array));
		int t = Arrays.stream(array).sum();
		if (t <= total) {
			return Arrays.copyOf(array, array.length);
		}
		int[] newArray = new int[array.length];
		float avg = total * 1.0F / Arrays.stream(array).filter(e -> e > 0).count();
		for (int i = 0; i < array.length; i++) {
			if (array[i] > 0 && array[i] <= avg && avg > 0) {
				newArray[i] = array[i];
				array[i] = 0;
				total -= newArray[i];
			}
		}
		while (total > 0) {
			avg = total * 1.0F / Arrays.stream(array).filter(e -> e > 0).count();
			float remain = 0;
			for (int i = 0; i < array.length; i++) {
				if (array[i] > 0) {
					remain += avg;
					int amount = Math.min((int) Math.floor(remain), array[i]);
					newArray[i] += amount;
					array[i] -= amount;
					remain -= amount;
					total -= amount;
				}
			}
		}
		// System.out.println("new:" + Arrays.toString(newArray));
		return newArray;
	}

	public boolean isConnectItem(EnumFacing facing) {
		if (getConfig(facing).getItemIOType() == ItemIOType.DISABLE) {
			return false;
		}
		TileEntity te = world.getTileEntity(getPos().offset(facing));
		if (te instanceof TECableGeneral) {
			return true;
		}
		if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
			IItemHandler s = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
			return true;
		}
		return false;
	}

	public CableConfig getConfig(EnumFacing facing) {
		return configs[facing.getIndex()];
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
		NBTTagList tagList = compound.getTagList("configs", 10);
		for (int i = 0; i < configs.length; i++) {
			configs[i].readFromNBT(tagList.getCompoundTagAt(i));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagList tagList = new NBTTagList();
		for (CableConfig e : configs) {
			tagList.appendTag(e.writeToNBT(new NBTTagCompound()));
		}
		compound.setTag("configs", tagList);
		return compound;
	}

	public static class CableConfig {
		private ItemStack[] whitelist = new ItemStack[15];
		private boolean disableEnergyTransmit;
		private ItemIOType itemIOType = ItemIOType.INPUT;

		public CableConfig() {
			Arrays.fill(whitelist, ItemStack.EMPTY);
		}

		public ItemStack[] getWhitelist() {
			return whitelist;
		}

		public boolean canEnergyTransmit() {
			return !disableEnergyTransmit;
		}

		public void setEnergyTransmit(boolean energyTransmit) {
			this.disableEnergyTransmit = !energyTransmit;
		}

		public ItemIOType getItemIOType() {
			return itemIOType;
		}

		public void setItemIOType(ItemIOType itemIOType) {
			this.itemIOType = itemIOType;
		}

		public void readFromNBT(NBTTagCompound compound) {
			NBTTagList tagList = compound.getTagList("whitelist", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
				int slot = itemTags.getInteger("Slot");
				if (slot >= 0 && slot < 15) {
					whitelist[slot] = new ItemStack(itemTags);
				}
			}
			disableEnergyTransmit = compound.getBoolean("disableEnergyTransmit");
			itemIOType = ItemIOType.values()[compound.getInteger("itemIOType")];
		}

		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			NBTTagList nbtTagList = new NBTTagList();
			for (int i = 0; i < 15; i++) {
				if (!whitelist[i].isEmpty()) {
					NBTTagCompound itemTag = new NBTTagCompound();
					itemTag.setInteger("Slot", i);
					whitelist[i].writeToNBT(itemTag);
					nbtTagList.appendTag(itemTag);
				}
			}
			compound.setTag("whitelist", nbtTagList);
			compound.setBoolean("disableEnergyTransmit", disableEnergyTransmit);
			compound.setInteger("itemIOType", itemIOType.ordinal());
			return compound;
		}

		public static enum ItemIOType {
			INPUT, OUTPUT, STORAGE, DISABLE;

			public String getTranslateKey() {
				switch (this) {
				case INPUT:
					return "container.cable_2.item.input";
				case OUTPUT:
					return "container.cable_2.item.output";
				case STORAGE:
					return "container.cable_2.item.storage";
				default:
					return "container.cable_2.item.disable";
				}
			}
		}
	}

	public static class IODeviceWrapper {
		public final IItemHandler handler;
		public final CableConfig config;

		public IODeviceWrapper(IItemHandler handler, CableConfig config) {
			this.handler = handler;
			this.config = config;
		}

		@Override
		public String toString() {
			return handler + "|" + config;
		}
	}
}
