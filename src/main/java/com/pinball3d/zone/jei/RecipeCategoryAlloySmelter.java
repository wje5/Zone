package com.pinball3d.zone.jei;

import com.pinball3d.zone.block.BlockLoader;
import com.pinball3d.zone.inventory.GuiContainerAlloySmelter;

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

public class RecipeCategoryAlloySmelter implements IRecipeCategory<RecipeWrapperAlloySmelter> {
	private final IDrawable icon;
	private final IDrawable background;
	private final IDrawableAnimated arrow;
	private final IDrawableAnimated energy;

	public RecipeCategoryAlloySmelter(IGuiHelper helper) {
		background = helper.createDrawable(GuiContainerAlloySmelter.TEXTURE, 37, 16, 100, 54);
		arrow = helper.drawableBuilder(GuiContainerAlloySmelter.TEXTURE, 176, 14, 24, 17).buildAnimated(200,
				IDrawableAnimated.StartDirection.LEFT, false);
		energy = helper.drawableBuilder(GuiContainerAlloySmelter.TEXTURE, 176, 0, 14, 14).buildAnimated(200,
				IDrawableAnimated.StartDirection.TOP, true);
		icon = helper.createDrawableIngredient(new ItemStack(BlockLoader.alloy_smelter_1));
	}

	@Override
	public String getUid() {
		return "zone:alloy_smelter";
	}

	@Override
	public String getTitle() {
		return I18n.format("container.alloy_smelter");
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
		energy.draw(minecraft, 20, 20);
		arrow.draw(minecraft, 42, 18);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperAlloySmelter recipeWrapper,
			IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, true, 18, 0);
		guiItemStacks.init(2, true, 36, 0);
		guiItemStacks.init(3, false, 78, 18);
		guiItemStacks.set(ingredients);
	}
}
