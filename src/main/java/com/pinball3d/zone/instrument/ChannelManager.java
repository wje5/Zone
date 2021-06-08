package com.pinball3d.zone.instrument;

import net.minecraft.entity.player.EntityPlayer;

public class ChannelManager {
	private EntityPlayer[] channels = new EntityPlayer[15];

	private static ChannelManager instance = new ChannelManager();

	public static ChannelManager getInstance() {
		return instance;
	}

	public int getChannel(EntityPlayer player) {
		for (int i = 0; i < channels.length; i++) {
			if (channels[i] != null && channels[i].getName().equals(player.getName())) {
				return i;
			}
		}
		for (int i = 0; i < channels.length; i++) {
			if (channels[i] == null) {
				channels[i] = player;
				return i;
			}
		}
		throw new RuntimeException("Can't alloc midi channel.");
	}

	public int closeChannel(EntityPlayer player) {
		for (int i = 0; i < channels.length; i++) {
			if (channels[i] != null && channels[i].getName().equals(player.getName())) {
				channels[i] = null;
				return i;
			}
		}
		return -1;
	}
}
