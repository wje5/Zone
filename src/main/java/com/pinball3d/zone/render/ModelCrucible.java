package com.pinball3d.zone.render;

//Made with Blockbench
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCrucible extends ModelBase {
	private final ModelRenderer bb_main;

	public ModelCrucible() {
		textureWidth = 128;
		textureHeight = 128;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 1, -8.0F, -5.0F, -8.0F, 16, 1, 16, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 16, 18, 5.0F, -4.0F, -8.0F, 3, 2, 3, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 18, 5.0F, -4.0F, 5.0F, 3, 2, 3, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 16, 23, -8.0F, -4.0F, -8.0F, 3, 2, 3, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 23, -8.0F, -4.0F, 5.0F, 3, 2, 3, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 32, 27, -8.0F, -12.0F, -8.0F, 16, 8, 1, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 32, 18, -8.0F, -12.0F, 7.0F, 16, 8, 1, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 16, 26, 7.0F, -12.0F, -7.0F, 1, 8, 14, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 18, -8.0F, -12.0F, -7.0F, 1, 8, 14, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 8, 28, 7.0F, -15.0F, -4.0F, 1, 3, 1, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 1, 7.0F, -15.0F, 3.0F, 1, 3, 1, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 8, 7.0F, -15.0F, -3.0F, 1, 1, 6, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 1, -8.0F, -15.0F, -3.0F, 1, 1, 6, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 8, -8.0F, -15.0F, -4.0F, 1, 3, 1, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 27, 22, -8.0F, -15.0F, 3.0F, 1, 3, 1, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 16, 28, 6.0F, -2.0F, -8.0F, 2, 2, 2, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 8, 9, -8.0F, -2.0F, -8.0F, 2, 2, 2, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 28, -8.0F, -2.0F, 6.0F, 2, 2, 2, 0.0F));
		bb_main.cubeList.add(new ModelBox(bb_main, 8, 0, 6.0F, -2.0F, 6.0F, 2, 2, 2, 0.0F));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bb_main.render(f5);
	}

	public void setRotationAngle(float x, float y, float z) {
		bb_main.rotateAngleX = x;
		bb_main.rotateAngleY = y;
		bb_main.rotateAngleZ = z;
	}
}