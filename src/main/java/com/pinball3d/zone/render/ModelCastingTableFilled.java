package com.pinball3d.zone.render;
//Made with Blockbench

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCastingTableFilled extends ModelBase {
	private final ModelRenderer bone;

	public ModelCastingTableFilled() {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 18, -8.0F, -10.0F, -8.0F, 16, 1, 16, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 8, 18, 6.0F, -9.0F, 6.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 18, -8.0F, -9.0F, 6.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 8, 0, -8.0F, -9.0F, -8.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 1, 6.0F, -9.0F, -8.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 1, -8.0F, -11.0F, -8.0F, 16, 1, 16, 0.0F));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone.render(f5);
	}

	public void setRotationAngle(float x, float y, float z) {
		bone.rotateAngleX = x;
		bone.rotateAngleY = y;
		bone.rotateAngleZ = z;
	}
}