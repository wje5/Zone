package com.pinball3d.zone.sphinx.elite.map;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.pinball3d.zone.core.LoadingPluginZone;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;

public class ServerChunkHelper {
	private static Field fieldViewRadius;
	private static Method methodGetOrCreateEntry, methodMarkSortPending;
	private static Map<UUID, BlockPos> cameraPos = new HashMap<UUID, BlockPos>();
	private static Map<UUID, BlockPos> managedCameraPos = new HashMap<UUID, BlockPos>();

	public static void updateCameraPos(EntityPlayerMP player) {
		UUID uuid = player.getUniqueID();
		PlayerChunkMap map = player.getServerWorld().getPlayerChunkMap();
		BlockPos pos = cameraPos.get(uuid);
		boolean flag = pos == null;
		pos = pos == null ? BlockPos.ORIGIN : pos;
		BlockPos pos2 = managedCameraPos.get(uuid);
		boolean flag2 = pos2 == null;
		pos2 = pos2 == null ? BlockPos.ORIGIN : pos2;
		int i = (int) player.posX >> 4;
		int j = (int) player.posZ >> 4;
		double d0 = player.managedPosX - player.posX;
		double d1 = player.managedPosZ - player.posZ;
		double d2 = d0 * d0 + d1 * d1;
		int cameraChunkX = pos.getX() >> 4;
		int cameraChunkZ = pos.getZ() >> 4;
		int managedCameraChunkX = pos2.getX() >> 4;
		int managedCameraChunkZ = pos2.getZ() >> 4;
		int dX = pos2.getX() - pos.getX();
		int dZ = pos2.getZ() - pos.getZ();
		int distSq = dX * dX + dZ * dZ;
		if (d2 >= 64.0D || distSq >= 64.0D) {
			int k = (int) player.managedPosX >> 4;
			int l = (int) player.managedPosZ >> 4;
			int i1 = getPlayerViewRadius(map);
			int j1 = i - k;
			int k1 = j - l;
			if (j1 != 0 || k1 != 0 || cameraChunkX != managedCameraChunkX || cameraChunkZ != managedCameraChunkZ) {
				for (int l1 = i - i1; l1 <= i + i1; ++l1) {
					for (int i2 = j - i1; i2 <= j + i1; ++i2) {
						if (!overlaps(l1, i2, k, l, i1)
								&& (flag2 || !overlaps(l1, i2, managedCameraChunkX, managedCameraChunkZ, i))) {
							getOrCreateEntry(map, l1, i2).addPlayer(player);
						}
						if (!flag && !overlaps(l1 - i + cameraChunkX, i2 - i + cameraChunkZ, k, l, i1)
								&& (flag2 || !overlaps(l1 - i + cameraChunkX, i2 - i + cameraChunkZ,
										managedCameraChunkX, managedCameraChunkZ, i))) {
							getOrCreateEntry(map, l1 - i + cameraChunkX, i2 - i + cameraChunkZ).addPlayer(player);
						}
						if (!overlaps(l1 - j1, i2 - k1, i, j, i1)
								&& (flag || !overlaps(l1 - j1, i2 - k1, cameraChunkX, cameraChunkZ, i1))) {
							PlayerChunkMapEntry playerchunkmapentry = map.getEntry(l1 - j1, i2 - k1);
							if (playerchunkmapentry != null) {
								playerchunkmapentry.removePlayer(player);
							}
						}
						if (!flag2 && !overlaps(l1 - i + managedCameraChunkX, i2 - i + managedCameraChunkZ, i, j, i1)
								&& (flag || !overlaps(l1 - i + managedCameraChunkX, i2 - i + managedCameraChunkZ,
										cameraChunkX, cameraChunkZ, i1))) {
							PlayerChunkMapEntry playerchunkmapentry = map.getEntry(l1 - i + managedCameraChunkX,
									i2 - i + managedCameraChunkZ);
							if (playerchunkmapentry != null) {
								playerchunkmapentry.removePlayer(player);
							}
						}
					}
				}
				player.managedPosX = player.posX;
				player.managedPosZ = player.posZ;
				if (flag) {
					managedCameraPos.remove(uuid);
				} else {
					managedCameraPos.put(uuid, pos);
				}
				markSortPending(map);
			}
		}
		System.out.println(cameraPos + "|" + player.getUniqueID() + "|" + player.getPosition());
	}

	public static void setCameraPos(UUID uuid, BlockPos pos) {
		if (pos == null) {
			cameraPos.remove(uuid);
		} else {
			cameraPos.put(uuid, pos);
		}
	}

	private static boolean overlaps(int x1, int z1, int x2, int z2, int radius) {
		int i = x1 - x2;
		int j = z1 - z2;
		if (i >= -radius && i <= radius) {
			return j >= -radius && j <= radius;
		} else {
			return false;
		}
	}

	private static int getPlayerViewRadius(PlayerChunkMap map) {
		try {
			if (fieldViewRadius == null) {
				fieldViewRadius = PlayerChunkMap.class
						.getDeclaredField(LoadingPluginZone.runtimeDeobf ? "field_72698_e" : "playerViewRadius");
				fieldViewRadius.setAccessible(true);
			}
			return fieldViewRadius.getInt(map);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static PlayerChunkMapEntry getOrCreateEntry(PlayerChunkMap map, int chunkX, int chunkZ) {
		try {
			if (methodGetOrCreateEntry == null) {
				methodGetOrCreateEntry = PlayerChunkMap.class.getDeclaredMethod(
						LoadingPluginZone.runtimeDeobf ? "func_187302_c" : "getOrCreateEntry", int.class, int.class);
				methodGetOrCreateEntry.setAccessible(true);
			}
			return (PlayerChunkMapEntry) methodGetOrCreateEntry.invoke(map, chunkX, chunkZ);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void markSortPending(PlayerChunkMap map) {
		try {
			if (methodMarkSortPending == null) {
				methodMarkSortPending = PlayerChunkMap.class
						.getDeclaredMethod(LoadingPluginZone.runtimeDeobf ? "func_187306_e" : "markSortPending");
				methodMarkSortPending.setAccessible(true);
			}
			methodMarkSortPending.invoke(map);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
