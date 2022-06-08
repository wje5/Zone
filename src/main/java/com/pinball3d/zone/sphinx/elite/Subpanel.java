package com.pinball3d.zone.sphinx.elite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.math.Box4i;
import com.pinball3d.zone.math.Pos2i;
import com.pinball3d.zone.math.ZoneMathHelper;
import com.pinball3d.zone.sphinx.elite.MouseHandler.MouseType;

import net.minecraft.client.renderer.GlStateManager;

public class Subpanel extends Component {
	public Map<Component, Pos2i> components = new HashMap<Component, Pos2i>();
	protected Map<Component, List<Object>> componentsOrigin = new LinkedHashMap<Component, List<Object>>();
	private Component focus;
	private ILayout layout;
	private int width, scrollingDistance, length;
	private Runnable onClick;
	private boolean expand;

	public Subpanel(EliteMainwindow parent, Subpanel parentPanel, ILayout layout) {
		this(parent, parentPanel, 1000000, 1000000, layout);
	}

	public Subpanel(EliteMainwindow parent, Subpanel parentPanel, int width, int height, ILayout layout) {
		super(parent, parentPanel, width, height);
		this.layout = layout;
	}

	public Subpanel setLayout(ILayout layout) {
		this.layout = layout;
		return this;
	}

	public Subpanel setOnClick(Runnable onClick) {
		this.onClick = onClick;
		return this;
	}

	public void addComponent(Component c, Object... layoutData) {
		List<Object> data = new ArrayList<Object>();
		data.addAll(Arrays.asList(layoutData));
		componentsOrigin.put(c, data);
	}

	public void removeComponent(Component c) {
		componentsOrigin.remove(c);
	}

	public void clearComponents() {
		componentsOrigin.clear();
	}

	public int getLength() {
		return length;

	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getScrollingDistance() {
		return scrollingDistance;
	}

	@Override
	public int getMinWidth() {
		return 0;
	}

	@Override
	public int getWidth() {
		return expand ? 10000 : width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return getLength();
	}

	public Subpanel setExpand(boolean expand) {
		this.expand = expand;
		return this;
	}

	@Override
	public void refresh() {
		super.refresh();
		componentsOrigin.forEach((c, data) -> {
			c.refresh();
		});
		arrange(true);
		arrange(false);
	}

	public void arrange(boolean isPreArrange) {
		if (isHide()) {
			return;
		}
		if (isPreArrange) {
			componentsOrigin.forEach((c, data) -> {
				if (c instanceof Subpanel) {
					((Subpanel) c).arrange(isPreArrange);
				}
			});
			components = layout.arrange(this, componentsOrigin, 1000000, 1000000, true);
		} else {
			components = layout.arrange(this, componentsOrigin, getRenderWidth(), getLength(), false);
			componentsOrigin.forEach((c, data) -> {
				if (c instanceof Subpanel) {
					((Subpanel) c).arrange(isPreArrange);
				}
			});
		}
	}

	@Override
	public void doRenderPre(int mouseX, int mouseY, float partialTicks) {
		refresh();
		super.doRenderPre(mouseX, mouseY, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -scrollingDistance, 0);
		components.forEach((c, pos) -> {
			Box4i box = null;
			box = c.getRenderBox();
			if (box == null) {
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(pos.x, pos.y, 0);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor(box.x, parent.getHeight() - (box.y + box.height), box.width, box.height);
			c.doRenderPre(mouseX - pos.x, mouseY - pos.y, partialTicks);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GlStateManager.popMatrix();
		});
		GlStateManager.popMatrix();
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -scrollingDistance, 0);
		components.forEach((c, pos) -> {
			Box4i box = c.getRenderBox();
			if (box == null) {
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(pos.x, pos.y, 0);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor(box.x, parent.getHeight() - (box.y + box.height), box.width, box.height);
			c.doRender(mouseX - pos.x, mouseY - pos.y, partialTicks);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GlStateManager.popMatrix();
		});
		GlStateManager.popMatrix();
	}

	@Override
	public void doRenderPost(int mouseX, int mouseY, float partialTicks) {
		super.doRenderPost(mouseX, mouseY, partialTicks);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -scrollingDistance, 0);
		components.forEach((c, pos) -> {
			Box4i box = c.getRenderBox();
			if (box == null) {
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(pos.x, pos.y, 0);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			GL11.glScissor(box.x, parent.getHeight() - (box.y + box.height), box.width, box.height);
			c.doRenderPost(mouseX - pos.x, mouseY - pos.y, partialTicks);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GlStateManager.popMatrix();
		});
		GlStateManager.popMatrix();
	}

	@Override
	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		Iterator<Entry<Component, Pos2i>> it = components.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Component, Pos2i> e = it.next();
			Component c = e.getKey();
			int x = e.getValue().x;
			int y = e.getValue().y;
			if (mouseX >= x && mouseX <= x + c.getWidth() && mouseY >= y && mouseY <= y + c.getHeight()) {
				Drag drag = c.mouseClicked(mouseX - x, mouseY - y, mouseButton);
				if (drag != null) {
					return drag;
				}
			}
		}
		if (onClick != null) {
			onClick.run();
			return new Drag(mouseButton);
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean onMouseScrolled(int mouseX, int mouseY, int distance) {
		Iterator<Entry<Component, Pos2i>> it = components.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Component, Pos2i> e = it.next();
			Component c = e.getKey();
			int x = e.getValue().x;
			int y = e.getValue().y;
			if (mouseX >= x && mouseX <= x + c.getWidth() && mouseY >= y && mouseY <= y + c.getHeight()) {
				boolean flag = c.onMouseScrolled(mouseX - x, mouseY - y, distance);
				if (flag) {
					return true;
				}
			}
		}
		int max = getLength() - getRenderBox().height;
		System.out.println(max + "|" + scrollingDistance + "|" + distance);
		if (max < 0) {
			scrollingDistance = 0;
			return super.onMouseScrolled(mouseX, mouseY, distance);
		}
		if (scrollingDistance >= max) {
			scrollingDistance = Math.max(0, max);
			if (distance < 0) {
				return super.onMouseScrolled(mouseX, mouseY, distance);
			}
		}
		if (scrollingDistance <= 0) {
			scrollingDistance = 0;
			if (distance > 0) {
				return super.onMouseScrolled(mouseX, mouseY, distance);
			}
		}
		scrollingDistance = ZoneMathHelper.mid(0, scrollingDistance - distance, max);
		return true;
	}

	@Override
	public MouseType getMouseType(int mouseX, int mouseY) {
		Iterator<Entry<Component, Pos2i>> it = components.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Component, Pos2i> e = it.next();
			Component c = e.getKey();
			int x = e.getValue().x;
			int y = e.getValue().y;
			if (mouseX >= x && mouseX <= x + c.getWidth() && mouseY >= y && mouseY <= y + c.getHeight()) {
				MouseType type = c.getMouseType(mouseX - x, mouseY - y);
				if (type != null) {
					return type;
				}
			}
		}
		return super.getMouseType(mouseX, mouseY);
	}
}
