package com.pinball3d.zone.jei;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiContainerCentrifuge;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class RecipeCategoryCentrifuge implements IRecipeCategory<RecipeWrapperCentrifuge> {
	private final IDrawable icon;
	private final IDrawable background;
	private final IDrawableAnimated arrow;
	private final IDrawableAnimated energy;

	public RecipeCategoryCentrifuge(IGuiHelper helper) {
		background = helper.createDrawable(GuiContainerCentrifuge.TEXTURE, 38, 16, 110, 54);
		arrow = helper.drawableBuilder(GuiContainerCentrifuge.TEXTURE, 176, 14, 24, 17).buildAnimated(100,
				IDrawableAnimated.StartDirection.LEFT, false);
		energy = helper.drawableBuilder(GuiContainerCentrifuge.TEXTURE, 176, 0, 14, 14).buildAnimated(100,
				IDrawableAnimated.StartDirection.TOP, true);
		icon = helper.createDrawableIngredient(new ItemStack(BlockLoader.centrifuge_1));
	}

	@Override
	public String getUid() {
		return "zone:centrifuge";
	}

	@Override
	public String getTitle() {
		return I18n.format("container.centrifuge");
	}

	@Override
	public String getModName() {
		return "Zone";
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		energy.draw(minecraft, 2, 20);
		arrow.draw(minecraft, 24, 18);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperCentrifuge recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, false, 56, 18);
		guiItemStacks.init(2, false, 74, 18);
		guiItemStacks.init(3, false, 92, 18);
		guiItemStacks.set(ingredients);
	}
}
