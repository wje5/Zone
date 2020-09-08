package com.pinball3d.zone.render;
//Made with Blockbench

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCastingTable extends ModelBase {
	private final ModelRenderer bone;

	public ModelCastingTable() {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 1, -8.0F, -10.0F, -8.0F, 16, 1, 16, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 24, 27, 6.0F, -9.0F, 6.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 27, -8.0F, -9.0F, 6.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 8, 27, -8.0F, -9.0F, -8.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 16, 27, 6.0F, -9.0F, -8.0F, 2, 9, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 22, -8.0F, -11.0F, -8.0F, 16, 1, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 22, 25, -2.0F, -11.0F, -6.0F, 10, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 8, 1.0F, -11.0F, -5.0F, 7, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 32, 29, 4.0F, -11.0F, -4.0F, 4, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 8, 2, -8.0F, -11.0F, -6.0F, 3, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 1, -8.0F, -11.0F, -5.0F, 2, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 18, -8.0F, -11.0F, 5.0F, 16, 1, 3, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 25, -8.0F, -11.0F, 4.0F, 10, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 30, 27, -8.0F, -11.0F, 3.0F, 7, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 32, 31, -8.0F, -11.0F, 2.0F, 4, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 3, 6.0F, -11.0F, 2.0F, 2, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 32, 33, 4.0F, -11.0F, 4.0F, 4, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 8, 0, 5.0F, -11.0F, 3.0F, 3, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 10, 7.0F, -11.0F, -3.0F, 1, 1, 5, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 1, -8.0F, -11.0F, -4.0F, 1, 1, 6, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 5, -7.0F, -11.0F, 1.0F, 1, 1, 1, 0.0F));
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