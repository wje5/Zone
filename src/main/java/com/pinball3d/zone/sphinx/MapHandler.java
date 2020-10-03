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
import com.pinball3d.zone.tileentity.INeedNetwork;
import com.pinball3d.zone.tileentity.TENode;
import com.pinball3d.zone.tileentity.TEProcessingCenter;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class MapHandler {
	private static BufferBuilder bufferbuilder;
	private int xOffset, yOffset;
	private PointerPlayer pointerPlayer;
	private Map<Integer, PointerLiving> livings;
	private PointerProcessingCenter processingCenter;
	private Set<PointerNode> nodes;
	private Set<PointerStorage> storges;
	private Set<PointerDevice> devices;
	private Set<PointerProduction> productions;
	private Set<PointerPack> packs;
	public WorldPos network;
	private int dim;
	public static Minecraft mc = Minecraft.getMinecraft();
	private static MapHandler instance;

	private MapHandler(WorldPos netWork) {
		ChunkRenderCache.init();
		this.network = netWork;
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
		storges = new TreeSet<PointerStorage>(new Comparator<PointerStorage>() {
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
		instance.updateDevices();
		instance.drawMap(width, height);
		instance.drawPointer(width, height);
		instance.drawLines(width, height);
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
		list.addAll(instance.storges);
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
		TEProcessingCenter te = (TEProcessingCenter) network.getTileEntity();
		Set<WorldPos> set = te.getNodes();
		nodes.clear();
		set.forEach(e -> {
			if (e.getDim() == mc.player.dimension) {
				TileEntity t = e.getTileEntity();
				if (t != null) {
					nodes.add(new PointerNode(e, ((INeedNetwork) t).isConnected()));
				}
			}
		});
		set = te.getStorages();
		storges.clear();
		set.forEach(e -> {
			if (e.getDim() == mc.player.dimension) {
				TileEntity t = e.getTileEntity();
				if (t != null) {
					storges.add(new PointerStorage(e, ((INeedNetwork) t).isConnected()));
				}
			}
		});
		set = te.getDevices();
		devices.clear();
		set.forEach(e -> {
			if (e.getDim() == mc.player.dimension) {
				TileEntity t = e.getTileEntity();
				if (t != null) {
					devices.add(new PointerDevice(e, ((INeedNetwork) t).isConnected()));
				}
			}
		});
		set = te.getProductions();
		productions.clear();
		set.forEach(e -> {
			if (e.getDim() == mc.player.dimension) {
				TileEntity t = e.getTileEntity();
				if (t != null) {
					productions.add(new PointerProduction(e, ((INeedNetwork) t).isConnected()));
				}
			}
		});
		Set<LogisticPack> packset = te.getPacks();
		packs.clear();
		packset.forEach(e -> {
			if (e.dim == mc.player.dimension) {
				packs.add(new PointerPack((int) e.x, (int) e.z));
			}
		});
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
		storges.forEach(e -> {
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
		List<PointerNeedNetwork> list = new ArrayList<PointerNeedNetwork>();
		list.add(processingCenter);
		list.addAll(nodes);
		list.addAll(storges);
		list.addAll(devices);
		list.addAll(productions);
		int color = 0x48C0C0C0;
		for (int i = 0; i < list.size(); i++) {
			PointerNeedNetwork e = list.get(i);
			for (int j = i + 1; j < list.size(); j++) {
				PointerNeedNetwork e2 = list.get(j);
				if (e instanceof PointerProcessingCenter && Math.sqrt(e.pos.getPos().distanceSq(e2.pos.getPos().getX(),
						e2.pos.getPos().getY(), e2.pos.getPos().getZ())) < 25) {
					Util.drawLine(e.pos.getPos().getX() - getRenderOffsetX(width),
							e.pos.getPos().getZ() - getRenderOffsetY(height),
							e2.pos.getPos().getX() - getRenderOffsetX(width),
							e2.pos.getPos().getZ() - getRenderOffsetY(height), color);
				} else if (e instanceof PointerNode && ((TENode) e.pos.getTileEntity()).isPointInRange(e2.pos.getDim(),
						e2.pos.getPos().getX(), e2.pos.getPos().getY(), e2.pos.getPos().getZ())) {
					Util.drawLine(e.pos.getPos().getX() - getRenderOffsetX(width),
							e.pos.getPos().getZ() - getRenderOffsetY(height),
							e2.pos.getPos().getX() - getRenderOffsetX(width),
							e2.pos.getPos().getZ() - getRenderOffsetY(height), color);
				} else if (e2 instanceof PointerNode && ((TENode) e2.pos.getTileEntity()).isPointInRange(e.pos.getDim(),
						e.pos.getPos().getX(), e.pos.getPos().getY(), e.pos.getPos().getZ())) {
					Util.drawLine(e2.pos.getPos().getX() - getRenderOffsetX(width),
							e2.pos.getPos().getZ() - getRenderOffsetY(height),
							e.pos.getPos().getX() - getRenderOffsetX(width),
							e.pos.getPos().getZ() - getRenderOffsetY(height), color);
				}
			}
		}
	}
}
