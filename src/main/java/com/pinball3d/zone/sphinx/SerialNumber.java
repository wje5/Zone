package com.pinball3d.zone.sphinx;

import java.util.Comparator;

import com.pinball3d.zone.tileentity.TEProcessingCenter;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class SerialNumber {
	private Type type;
	private int number;
	private boolean dead;
	public static Comparator<SerialNumber> serialNumberComparator = (a, b) -> {
		int t = a.type.compareTo(b.type);
		if (t != 0) {
			return t;
		}
		return a.number - b.number;
	};

	public static final SerialNumber CENTER = new SerialNumber(Type.NODE, 0);

	public SerialNumber(Type type, int number) {
		this(type, number, false);
	}

	public SerialNumber(Type type, int number, boolean dead) {
		this.type = type;
		this.number = number;
		this.dead = dead;
	}

	public SerialNumber(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public SerialNumber(ByteBuf buf) {
		this(Type.values()[buf.readInt()], buf.readInt(), buf.readBoolean());
	}

	public Type getType() {
		return type;
	}

	public int getNumber() {
		return number;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public void check(TEProcessingCenter te) {
		setDead(te.getPosFromSerialNumber(this).isOrigin());
	}

	public void readFromNBT(NBTTagCompound tag) {
		type = Type.values()[tag.getInteger("type")];
		number = tag.getInteger("number");
		dead = tag.getBoolean("dead");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("type", type.ordinal());
		tag.setInteger("number", number);
		tag.setBoolean("dead", dead);
		return tag;
	}

	public void writeToByte(ByteBuf buf) {
		buf.writeInt(type.ordinal());
		buf.writeInt(number);
		buf.writeBoolean(dead);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SerialNumber && ((SerialNumber) obj).type == type
				&& ((SerialNumber) obj).number == number;
	}

	@Override
	public int hashCode() {
		return number * 31 + type.hashCode();
	}

	@Override
	public String toString() {
		String prefix = "";
		switch (type) {
		case NODE:
			prefix = "N";
			break;
		case STORAGE:
			prefix = "S";
			break;
		case DEVICE:
			prefix = "D";
			break;
		case PRODUCTION:
			prefix = "P";
			break;
		}
		return prefix + number;
	}

	public static enum Type {
		NODE, STORAGE, DEVICE, PRODUCTION
	}
}
