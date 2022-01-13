package com.pinball3d.zone.sphinx.elite.ui.components;

import java.util.function.Supplier;

import com.pinball3d.zone.sphinx.elite.Drag;
import com.pinball3d.zone.sphinx.elite.EliteMainwindow;
import com.pinball3d.zone.sphinx.elite.EliteRenderHelper;
import com.pinball3d.zone.sphinx.elite.TextureLocation;
import com.pinball3d.zone.sphinx.elite.ui.core.Component;
import com.pinball3d.zone.sphinx.elite.ui.core.Subpanel;
import com.pinball3d.zone.util.Pair;

public class ImageButton extends Component {
	private Supplier<Pair<TextureLocation, Float>> texture;
	private Runnable onClick;

	public ImageButton(EliteMainwindow parent, Subpanel parentPanel, Pair<TextureLocation, Float> texture,
			Runnable onClick) {
		this(parent, parentPanel, () -> texture, onClick);
	}

	public ImageButton(EliteMainwindow parent, Subpanel parentPanel, Supplier<Pair<TextureLocation, Float>> texture,
			Runnable onClick) {
		super(parent, parentPanel, (int) (texture.get().key().uWidth * texture.get().value()),
				(int) (texture.get().key().vHeight * texture.get().value()));
		this.texture = texture;
		this.onClick = onClick;
	}

	@Override
	public Drag mouseClicked(int mouseX, int mouseY, int mouseButton) {
		onClick.run();
		return new Drag(mouseButton);
	}

	@Override
	public void doRender(int mouseX, int mouseY, float partialTicks) {
		super.doRender(mouseX, mouseY, partialTicks);
		Pair<TextureLocation, Float> texture = getTexture();
		EliteRenderHelper.drawTexture(texture.key(), 0, 0, texture.value());
	}

	public Pair<TextureLocation, Float> getTexture() {
		return texture.get();
	}

	public void setTexture(Supplier<Pair<TextureLocation, Float>> texture) {
		this.texture = texture;
	}

	@Override
	public int getWidth() {
		return (int) (getTexture().key().uWidth * getTexture().value());
	}

	@Override
	public int getHeight() {
		return (int) (getTexture().key().vHeight * getTexture().value());
	}
}
