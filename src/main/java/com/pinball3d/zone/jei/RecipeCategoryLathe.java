package com.pinball3d.zone.jei;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiContainerLathe;

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

public class RecipeCategoryLathe implements IRecipeCategory<RecipeWrapperLathe> {
	private final IDrawable icon;
	private final IDrawable background;
	private final IDrawableAnimated arrow;
	private final IDrawableAnimated energy;

	public RecipeCategoryLathe(IGuiHelper helper) {
		background = helper.createDrawable(GuiContainerLathe.TEXTURE, 55, 16, 82, 54);
		arrow = helper.drawableBuilder(GuiContainerLathe.TEXTURE, 176, 14, 24, 17).buildAnimated(50,
				IDrawableAnimated.StartDirection.LEFT, false);
		energy = helper.drawableBuilder(GuiContainerLathe.TEXTURE, 176, 0, 14, 14).buildAnimated(50,
				IDrawableAnimated.StartDirection.TOP, true);
		icon = helper.createDrawableIngredient(new ItemStack(BlockLoader.lathe));
	}

	@Override
	public String getUid() {
		return "zone:lathe";
	}

	@Override
	public String getTitle() {
		return I18n.format("container.lathe");
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
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperLathe recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, false, 60, 18);
		guiItemStacks.set(ingredients);
	}
}
