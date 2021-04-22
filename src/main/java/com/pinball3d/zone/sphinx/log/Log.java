package com.pinball3d.zone.sphinx.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

import com.pinball3d.zone.tileentity.TEProcessingCenter;

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

	public void check(TEProcessingCenter te) {

	}

	@Override
	public String toString() {
		return format().toString();
	}

	public FormattedLog format() {
		return new FormattedLog(time, level, "");
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
		SENDPACK(LogSendPack::new), RECVPACK(LogRecvPack::new), IOPANELREQUEST(LogIOPanelRequest::new),
		IOPANELDISPENSE(LogIOPanelDispense::new), CONNECTTONETWORK(LogConnectToNetwork::new),
		DISCONNECTFROMNETWORK(LogDisconnectFromNetwork::new), MANAGECLASSIFY(LogManageClassify::new),
		DELETECLASSIFY(LogDeleteClassify::new), RENAMECLASSIFY(LogRenameClassify::new),
		NEEDNETWORKDESTROYED(LogNeedNetworkDestroyed::new), REQUESTPERMISSION(LogRequestPermission::new),
		APPROVEPERMISSION(LogApprovePermission::new), DENYPERMISSION(LogDenyPermission::new),
		DELETEUSER(LogDeleteUser::new), TRANSFERADMIN(LogTransferAdmin::new), STORAGEFULL(LogStorageFull::new),
		SPHINXOPEN(LogSphinxOpen::new), SPHINXOPENFINISH(LogSphinxOpenFinish::new),
		SPHINXSHUTDOWN(LogSphinxShutdown::new), SPHINXSHUTDOWNENERGY(LogSphinxShutdownEnergy::new),
		SPHINXSHUTDOWNSTRUCTURE(LogSphinxShutdownStructure::new), PACKLOST(LogPackLost::new),
		RECVPACKFULL(LogRecvPackFull::new), CHANGENAME(LogChangeName::new),
		CHANGEOREDICTIONARYPRIORITY(LogChangeOreDictionaryPriority::new), RESCANRECIPES(LogRescanRecipes::new),
		RESCANRECIPESFINISH(LogRescanRecipesFinish::new), NEWOREDICTIONARY(LogNewOreDictionary::new);

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
