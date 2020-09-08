package com.pinball3d.zone.render;

import org.lwjgl.opengl.GL11;

import com.pinball3d.zone.tileentity.TECastingTable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderCastingTable extends TileEntitySpecialRenderer<TECastingTable> {
	private ModelCastingTable modelCastingTable = new ModelCastingTable();
	private ModelCastingTableFilled modelCastingTableFilled = new ModelCastingTableFilled();

	@Override
	public void render(TECastingTable tileentity, double x, double y, double z, float tick, int destroyStage,
			float alpha) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y, z + 0.5D);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		int meta = tileentity.getBlockMetadata();
		double rot = 0;
		switch (meta % 4) {
		case 0:
			break;
		case 1:
			rot = Math.PI / 2;
			break;
		case 2:
			rot = Math.PI;
			break;
		case 3:
			rot = -Math.PI / 2;
		}
		if (meta < 4) {
			bindTexture(new ResourceLocation("zone:textures/blocks/casting_table.png"));
			modelCastingTable.setRotationAngle(0, (float) rot, 0);
			modelCastingTable.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		} else if (meta < 8) {
			bindTexture(new ResourceLocation("zone:textures/blocks/casting_table_filled.png"));
			modelCastingTableFilled.setRotationAngle(0, (float) rot, 0);
			modelCastingTableFilled.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		} else {
			bindTexture(new ResourceLocation("zone:textures/blocks/casting_table_solid.png"));
			modelCastingTableFilled.setRotationAngle(0, (float) rot, 0);
			modelCastingTableFilled.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		}
		GL11.glPopMatrix();
	}
}
