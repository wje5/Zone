package com.pinball3d.zone.sphinx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.pinball3d.zone.network.MessageRequestMapData;
import com.pinball3d.zone.network.NetworkHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

public class MapHandler {
	private static BufferBuilder bufferbuilder;
	private int xOffset, yOffset;
	private PointerPlayer pointerPlayer;
	private Map<Integer, PointerLiving> livings;
	private PointerProcessingCenter processingCenter;
	private Set<PointerNode> nodes;
	private Set<PointerStorage> storages;
	private Set<PointerDevice> devices;
	private Set<PointerProduction> productions;
	private Set<PointerPack> packs;
	public WorldPos network;
	private int dim;
	private NBTTagCompound data;
	private int[] lines;
	private long updateTick;
	public static Minecraft mc = Minecraft.getMinecraft();
	private static MapHandler instance;

	private MapHandler(WorldPos netWork) {
		ChunkRenderCache.init();
		this.network = netWork;
		NetworkHandler.instance.sendToServer(new MessageRequestMapData(mc.player, network));
		livings = new HashMap<Integer, PointerLiving>();
		BlockPos pos = mc.player.getPosition();
		xOffset = pos.getX();
		yOffset = pos.getZ();
		pointerPlayer = new PointerPlayer(xOffset, yOffset);
		dim = mc.player.world.provider.getDimension();
		processingCenter = new PointerProcessingCenter(network);
		nodes = new TreeSet<PointerNode>(new Comparator<PointerNode>() {
			@Override
			public int compare(PointerNode o1, PointerNode o2) {
				return o1.pos.getPos().getX() < o2.pos.getPos().getX() ? -1
						: o1.pos.getPos().getX() > o2.pos.getPos().getX() ? 1
								: o1.pos.getPos().getX() < o2.pos.getPos().getZ() ? -1
										: o1.pos.getPos().getZ() > o2.pos.getPos().getZ() ? 1 : 0;
			};
		});
		storages = new TreeSet<PointerStorage>(new Comparator<PointerStorage>() {
			@Override
			public int compare(PointerStorage o1, PointerStorage o2) {
				return o1.pos.getPos().getX() < o2.pos.getPos().getX() ? -1
						: o1.pos.getPos().getX() > o2.pos.getPos().getX() ? 1
								: o1.pos.getPos().getZ() < o2.pos.getPos().getZ() ? -1
										: o1.pos.getPos().getZ() > o2.pos.getPos().getZ() ? 1 : 0;
			};
		});
		devices = new TreeSet<PointerDevice>(new Comparator<PointerDevice>() {
			@Override
			public int compare(PointerDevice o1, PointerDevice o2) {
				return o1.pos.getPos().getX() < o2.pos.getPos().getX() ? -1
						: o1.pos.getPos().getX() > o2.pos.getPos().getX() ? 1
								: o1.pos.getPos().getZ() < o2.pos.getPos().getZ() ? -1
										: o1.pos.getPos().getZ() > o2.pos.getPos().getZ() ? 1 : 0;
			};
		});
		productions = new TreeSet<PointerProduction>(new Comparator<PointerProduction>() {
			@Override
			public int compare(PointerProduction o1, PointerProduction o2) {
				return o1.pos.getPos().getX() < o2.pos.getPos().getX() ? -1
						: o1.pos.getPos().getX() > o2.pos.getPos().getX() ? 1
								: o1.pos.getPos().getZ() < o2.pos.getPos().getZ() ? -1
										: o1.pos.getPos().getZ() > o2.pos.getPos().getZ() ? 1 : 0;
			};
		});
		packs = new TreeSet<PointerPack>(new Comparator<PointerPack>() {
			@Override
			public int compare(PointerPack o1, PointerPack o2) {
				return o1.x < o2.x ? -1 : o1.x > o2.x ? 1 : o1.z < o2.z ? -1 : o1.z > o2.z ? 1 : 0;
			};
		});
	}

	private boolean checkNetwork(WorldPos network) {
		return network.equals(this.network);
	}

	private static void changeNetwork(WorldPos network) {
		instance = new MapHandler(network);
	}

	public static void draw(WorldPos network, int width, int height) {
		if (instance == null || !instance.checkNetwork(network)) {
			changeNetwork(network);
		}
		instance.updatePlayer();
		instance.updateLiving();
		instance.updateProcessingCenter();
		instance.drawMap(width, height);
		instance.drawPointer(width, height);
		instance.drawLines(width, height);
		if (instance.updateTick < mc.world.getTotalWorldTime()) {
			NetworkHandler.instance.sendToServer(new MessageRequestMapData(mc.player, network));
			instance.updateTick = mc.world.getTotalWorldTime() + 20;
		}
	}

	public static void setData(WorldPos network, NBTTagCompound data, int[] lines) {
		if (instance != null && instance.network.equals(network)) {
			instance.data = data;
			instance.updateDevices();
			instance.lines = lines;
		}
	}

	public static void dragMap(int dragX, int dragY) {
		if (instance != null) {
			instance.xOffset += dragX;
			instance.yOffset += dragY;
		}
	}

	public static void onClick(int width, int height, int x, int y) {
		if (instance == null) {
			return;
		}
		List<PointerNeedNetwork> list = new ArrayList<PointerNeedNetwork>();
		list.add(instance.processingCenter);
		list.addAll(instance.nodes);
		list.addAll(instance.storages);
		list.addAll(instance.devices);
		list.addAll(instance.productions);
		List<Pointer> l = new ArrayList<Pointer>();
		list.forEach(e -> {
			if (e.pos.getDim() == mc.player.dimension
					&& e.isClick(x + instance.getRenderOffsetX(width), y + instance.getRenderOffsetY(height))) {
				l.add(e);
			}
		});
		if (mc.currentScreen instanceof ScreenTerminal) {
			((ScreenTerminal) mc.currentScreen).setChosen(l);
		}
	}

	private void updatePlayer() {
		BlockPos pos = mc.player.getPosition();
		pointerPlayer.x = pos.getX();
		pointerPlayer.z = pos.getZ();
		pointerPlayer.angle = mc.player.rotationYaw;
		int temp = mc.player.world.provider.getDimension();
		if (temp != dim) {
			xOffset = pos.getX();
			yOffset = pos.getZ();
		}
		dim = temp;
	}

	private void updateLiving() {
		Predicate<Entity> selector = new Predicate<Entity>() {
			@Override
			public boolean apply(Entity input) {
				return input instanceof EntityLiving;
			};
		};
		List<Entity> entitys = mc.player.world.getEntities(EntityLiving.class, selector);
		Iterator<Entity> it = entitys.iterator();
		Map<Integer, PointerLiving> temp = new HashMap<Integer, PointerLiving>();
		while (it.hasNext()) {
			EntityLiving entity = (EntityLiving) it.next();
			PointerLiving pointer = livings.get(entity.getEntityId());
			if (pointer == null) {
				BlockPos pos = entity.getPosition();
				pointer = new PointerLiving(pos.getX(), pos.getZ(), entity instanceof IMob);
			} else {
				BlockPos pos = entity.getPosition();
				pointer.x = pos.getX();
				pointer.z = pos.getZ();
			}
			temp.put(entity.getEntityId(), pointer);
		}
		livings = temp;
	}

	private void updateProcessingCenter() {
		if (network.getDim() == mc.player.dimension) {
			processingCenter.pos = network;
		} else {
			processingCenter = null;
		}
	}

	private void updateDevices() {
		if (data == null) {
			return;
		}
		NBTTagList list = data.getTagList("nodes", 10);
		nodes.clear();
		list.forEach(e -> {
			NBTTagCompound tag = (NBTTagCompound) e;
			WorldPos pos = WorldPos.load(tag);
			if (pos.getDim() == mc.player.dimension) {
				nodes.add(new PointerNode(pos, tag.getBoolean("connected")));
			}
		});
		list = data.getTagList("storages", 10);
		storages.clear();
		list.forEach(e -> {
			NBTTagCompound tag = (NBTTagCompound) e;
			WorldPos pos = WorldPos.load(tag);
			if (pos.getDim() == mc.player.dimension) {
				storages.add(new PointerStorage(pos, tag.getBoolean("connected")));
			}
		});
		list = data.getTagList("devices", 10);
		devices.clear();
		list.forEach(e -> {
			NBTTagCompound tag = (NBTTagCompound) e;
			WorldPos pos = WorldPos.load(tag);
			if (pos.getDim() == mc.player.dimension) {
				devices.add(new PointerDevice(pos, tag.getBoolean("connected")));
			}
		});
		list = data.getTagList("productions", 10);
		productions.clear();
		list.forEach(e -> {
			NBTTagCompound tag = (NBTTagCompound) e;
			WorldPos pos = WorldPos.load(tag);
			if (pos.getDim() == mc.player.dimension) {
				productions.add(new PointerProduction(pos, tag.getBoolean("connected")));
			}
		});
//		Set<LogisticPack> packset = te.getPacks();
//		packs.clear();
//		packset.forEach(e -> {
//			if (e.dim == mc.player.dimension) {
//				packs.add(new PointerPack((int) e.x, (int) e.z));
//			}
//		});
	}

	private void drawMap(int width, int height) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		EntityPlayer player = mc.player;
		int xChunk = getRenderOffsetX(width) < 0 ? getRenderOffsetX(width) / 16 - 1 : getRenderOffsetX(width) / 16;
		int yChunk = getRenderOffsetY(height) < 0 ? getRenderOffsetY(height) / 16 - 1 : getRenderOffsetY(height) / 16;
		int xEnd = getRenderOffsetX(width) + width - 1 < 0 ? (getRenderOffsetX(width) + width - 1) / 16 - 1
				: (getRenderOffsetX(width) + width - 1) / 16;
		int yEnd = getRenderOffsetY(height) + height - 1 < 0 ? (getRenderOffsetY(height) + height - 1) / 16 - 1
				: (getRenderOffsetY(height) + height - 1) / 16;
		for (int i = xChunk; i <= xEnd + 1; i++) {
			for (int j = yChunk; j <= yEnd + 1; j++) {
				ChunkRenderCache data = ClientMapDataHandler.getData(player.world.provider.getDimension(), i, j);
				drawChunk(data, i * 16 - getRenderOffsetX(width), j * 16 - getRenderOffsetY(height));
			}
		}
		GlStateManager.popMatrix();
	}

	private int getRenderOffsetX(int width) {
		int xRenderRange = width / 2;
		return xOffset - xRenderRange + 1;
	}

	private int getRenderOffsetY(int height) {
		int yRenderRange = height / 2;
		return yOffset - yRenderRange + 1;
	}

	private void drawGrid(int x, int y) {
		int color = 0x48C0C0C0;
		Gui.drawRect(x + 15, y, x + 16, y + 16, color);
		Gui.drawRect(x, y + 15, x + 15, y + 16, color);
	}

	private void drawChunk(ChunkRenderCache cache, int xOffset, int zOffset) {
		GlStateManager.pushMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				if (cache == null) {
					applyQuad(0x000000, xOffset + x, zOffset + z);
				} else {
					int color = cache.getColor(x, z);
					applyQuad(color, xOffset + x, zOffset + z);
				}
			}
		}
		tessellator.draw();
		GlStateManager.popMatrix();
		drawGrid(xOffset, zOffset);
	}

	private void applyQuad(int color, int x, int y) {
		float r = (color >> 16) / 255F;
		float g = ((color >> 8) & 0x0000FF) / 255F;
		float b = (color & 0x0000FF) / 255F;
		bufferbuilder.pos(x, y + 1, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + 1, y + 1, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x + 1, y, 0).color(r, g, b, 1.0F).endVertex();
		bufferbuilder.pos(x, y, 0).color(r, g, b, 1.0F).endVertex();
	}

	private void drawPointer(int width, int height) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//		Iterator<PointerLiving> it = livings.values().iterator();
//		while (it.hasNext()) {
//			PointerLiving pointer = it.next();
//			pointer.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
//		}
		if (processingCenter != null) {
			processingCenter.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
		}
		pointerPlayer.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
		nodes.forEach(e -> {
			e.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
		});
		storages.forEach(e -> {
			e.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
		});
		devices.forEach(e -> {
			e.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
		});
		productions.forEach(e -> {
			e.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
		});
		packs.forEach(e -> {
			e.doRender(getRenderOffsetX(width), getRenderOffsetY(height));
		});
		GlStateManager.popMatrix();
	}

	private void drawLines(int width, int height) {
		if (lines == null) {
			return;
		}
		List<PointerNeedNetwork> list = new ArrayList<PointerNeedNetwork>();
		list.add(processingCenter);
		list.addAll(nodes);
		list.addAll(storages);
		list.addAll(devices);
		list.addAll(productions);
		int color = 0x48C0C0C0;
		for (int i = 0; i < lines.length; i += 2) {
			PointerNeedNetwork e = list.get(lines[i]);
			PointerNeedNetwork e2 = list.get(lines[i + 1]);
			Util.drawLine(e.pos.getPos().getX() - getRenderOffsetX(width),
					e.pos.getPos().getZ() - getRenderOffsetY(height), e2.pos.getPos().getX() - getRenderOffsetX(width),
					e2.pos.getPos().getZ() - getRenderOffsetY(height), color);
		}
	}
}
