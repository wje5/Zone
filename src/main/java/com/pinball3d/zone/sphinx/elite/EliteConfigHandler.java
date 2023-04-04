package com.pinball3d.zone.sphinx.elite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pinball3d.zone.util.Pair;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class EliteConfigHandler {
	private static File path;
	private static Map<UUID, WorkspaceConfig> map = new HashMap<UUID, WorkspaceConfig>();

	public static void onPreinit(FMLPreInitializationEvent event) {
		path = new File(event.getModConfigurationDirectory(), "elite");
		path.mkdirs();
	}

	public static WorkspaceConfig getConfig(UUID uuid) {
		if (map.containsKey(uuid)) {
			return map.get(uuid);
		}
		File f = new File(path, uuid.toString() + ".json");
		WorkspaceConfig config;
		if (f.isFile()) {
			config = WorkspaceConfig.readFronFile(f);
		} else {
			config = new WorkspaceConfig();
		}
		map.put(uuid, config);
		saveConfig(uuid);
		return config;
	}

	public static WorkspaceConfig resetConfig(UUID uuid) {
		WorkspaceConfig config = new WorkspaceConfig();
		map.put(uuid, config);
		saveConfig(uuid);
		return config;
	}

	public static void saveConfig(UUID uuid) {
		getConfig(uuid).saveToFile(new File(path, uuid.toString() + ".json"));
	}

	public static class WorkspaceConfig {
		public boolean init;
		public StateData stateData;

		public WorkspaceConfig() {

		}

		public static WorkspaceConfig readFronFile(File file) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				return new Gson().fromJson(reader, WorkspaceConfig.class);
			} catch (IOException e) {
				new IOException("Error loading workspace config file: " + file.getAbsolutePath(), e).printStackTrace();
				return new WorkspaceConfig();
			}
		}

		public void saveToFile(File file) {
			String text = new Gson().toJson(this);
			try (FileOutputStream s = new FileOutputStream(file)) {
				s.write(text.getBytes());
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static class StateData {
			public List<Pair<List<Pair<String, JsonObject>>, List<Float>>> panels = new ArrayList<Pair<List<Pair<String, JsonObject>>, List<Float>>>();
		}
	}
}
