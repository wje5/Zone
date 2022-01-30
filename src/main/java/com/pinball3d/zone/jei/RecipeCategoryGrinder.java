package com.pinball3d.zone.jei;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiContainerGrinder;

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

public class RecipeCategoryGrinder implements IRecipeCategory<RecipeWrapperGrinder> {
	private final IDrawable icon;
	private final IDrawable background;
	private final IDrawableAnimated arrow;
	private final IDrawableAnimated energy;

	public RecipeCategoryGrinder(IGuiHelper helper) {
		background = helper.createDrawable(GuiContainerGrinder.TEXTURE, 55, 16, 82, 54);
		arrow = helper.drawableBuilder(GuiContainerGrinder.TEXTURE, 176, 14, 24, 17).buildAnimated(200,
				IDrawableAnimated.StartDirection.LEFT, false);
		energy = helper.drawableBuilder(GuiContainerGrinder.TEXTURE, 176, 0, 14, 14).buildAnimated(400,
				IDrawableAnimated.StartDirection.TOP, true);
		icon = helper.createDrawableIngredient(new ItemStack(BlockLoader.grinder_1));
	}

	@Override
	public String getUid() {
		return "zone:grinder";
	}

	@Override
	public String getTitle() {
		return I18n.format("container.grinder");
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
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperGrinder recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, false, 60, 18);
		guiItemStacks.set(ingredients);
	}
}
