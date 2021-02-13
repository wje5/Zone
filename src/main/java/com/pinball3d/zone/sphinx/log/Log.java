package com.pinball3d.zone.sphinx.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

import net.minecraft.nbt.NBTTagCompound;

public class Log {
	private Level level;
	private Type logType;
	private int id;
	private long time;
	private static SimpleDateFormat formatter = new SimpleDateFormat("[HH:mm:ss]");
	private static Date date = new Date();

	public Log(Level level, Type logType, int id) {
		this(level, logType, id, System.currentTimeMillis());
	}

	public Log(Level level, Type logType, int id, long date) {
		this.level = level;
		this.logType = logType;
		this.id = id;
		this.time = date;
	}

	public Log(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		level = Level.values()[tag.getInteger("level")];
		logType = Type.values()[tag.getInteger("type")];
		id = tag.getInteger("id");
		time = tag.getLong("time");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("level", level.ordinal());
		tag.setInteger("type", logType.ordinal());
		tag.setInteger("id", id);
		tag.setLong("time", time);
		return tag;
	}

	public Level getLevel() {
		return level;
	}

	public Type getLogType() {
		return logType;
	}

	public int getId() {
		return id;
	}

	public long getTime() {
		return time;
	}

	public String getDateString() {
		date.setTime(time);
		return formatter.format(date);
	}

	@Override
	public String toString() {
		return getDateString() + " " + level;
	}

	public static enum Level {
		IMPORTANT("IMPORTANT"), INFO("INFO"), DEBUG("DEBUG"), CHAT("CHAT");

		public final String text;

		private Level(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return "[" + text + "]";
		}
	}

	public static enum Type {
		SENDPACK(LogSendPack::new);

		private Function<NBTTagCompound, Log> cons;

		private Type(Function<NBTTagCompound, Log> s) {
			cons = s;
		}

		public Log newInstance(NBTTagCompound tag) {
			return cons.apply(tag);
		}
	}

	public static Log readLogFromNBT(NBTTagCompound tag) {
		Type type = Type.values()[tag.getInteger("type")];
		return type.newInstance(tag);
	}
}
