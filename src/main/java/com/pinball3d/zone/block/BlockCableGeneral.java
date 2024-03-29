package com.pinball3d.zone.block;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.pinball3d.zone.Zone;
import com.pinball3d.zone.inventory.GuiElementLoader;
import com.pinball3d.zone.tileentity.TECableGeneral;
import com.pinball3d.zone.tileentity.TECableGeneral.CableConfig;
import com.pinball3d.zone.tileentity.ZoneTieredMachine.Tier;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class BlockCableGeneral extends BlockCableBasic {
	public static final PropertyBool ITEMDOWN = PropertyBool.create("itemdown");
	public static final PropertyBool ITEMUP = PropertyBool.create("itemup");
	public static final PropertyBool ITEMNORTH = PropertyBool.create("itemnorth");
	public static final PropertyBool ITEMSOUTH = PropertyBool.create("itemsouth");
	public static final PropertyBool ITEMWEST = PropertyBool.create("itemwest");
	public static final PropertyBool ITEMEAST = PropertyBool.create("itemeast");

	public BlockCableGeneral() {
		super(Tier.T2);
		getDefaultState().withProperty(ITEMDOWN, Boolean.valueOf(false)).withProperty(ITEMUP, Boolean.valueOf(false))
				.withProperty(ITEMNORTH, Boolean.valueOf(false)).withProperty(ITEMEAST, Boolean.valueOf(false))
				.withProperty(ITEMSOUTH, Boolean.valueOf(false)).withProperty(ITEMWEST, Boolean.valueOf(false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { DOWN, UP, NORTH, SOUTH, WEST, EAST, ITEMDOWN, ITEMUP,
				ITEMNORTH, ITEMSOUTH, ITEMWEST, ITEMEAST });
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		state = super.getActualState(state, world, pos);
		TECableGeneral te = (TECableGeneral) world.getTileEntity(pos);
		return state.withProperty(ITEMDOWN, te.isConnectItem(EnumFacing.DOWN))
				.withProperty(ITEMUP, te.isConnectItem(EnumFacing.UP))
				.withProperty(ITEMNORTH, te.isConnectItem(EnumFacing.NORTH))
				.withProperty(ITEMSOUTH, te.isConnectItem(EnumFacing.SOUTH))
				.withProperty(ITEMWEST, te.isConnectItem(EnumFacing.WEST))
				.withProperty(ITEMEAST, te.isConnectItem(EnumFacing.EAST));
	}

	@Override
	protected List<AxisAlignedBB> getCollisionBoxList(IBlockState state) {
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
		list.add(new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D));
		if (state.getValue(DOWN) | state.getValue(ITEMDOWN)) {
			list.add(new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.375D, 0.625D));
		}
		if (state.getValue(UP) | state.getValue(ITEMUP)) {
			list.add(new AxisAlignedBB(0.375D, 0.625D, 0.375D, 0.625D, 1.0D, 0.625D));
		}
		if (state.getValue(NORTH) | state.getValue(ITEMNORTH)) {
			list.add(new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 0.375D));
		}
		if (state.getValue(SOUTH) | state.getValue(ITEMSOUTH)) {
			list.add(new AxisAlignedBB(0.375D, 0.375D, 0.625D, 0.625D, 0.625D, 1.0D));
		}
		if (state.getValue(WEST) | state.getValue(ITEMWEST)) {
			list.add(new AxisAlignedBB(0.0D, 0.375D, 0.375D, 0.375D, 0.625D, 0.625D));
		}
		if (state.getValue(EAST) | state.getValue(ITEMEAST)) {
			list.add(new AxisAlignedBB(0.625D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D));
		}
		return list;
	}

	@SuppressWarnings("incomplete-switch")
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult result = mc.objectMouseOver;
		if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK
				&& mc.world.getTileEntity(result.getBlockPos()) instanceof TECableGeneral) {
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buffer = tess.getBuffer();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks();
			double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks();
			double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks();
			GlStateManager.translate(result.getBlockPos().getX() - d3 + 0.5F, result.getBlockPos().getY() - d4 + 0.5F,
					result.getBlockPos().getZ() - d5 + 0.5F);
			switch (result.sideHit) {
			case UP:
				GL11.glRotatef(180, 1, 0, 0);
				break;
			case NORTH:
				GL11.glRotatef(90, 1, 0, 0);
				break;
			case SOUTH:
				GL11.glRotatef(-90, 1, 0, 0);
				break;
			case WEST:
				GL11.glRotatef(-90, 0, 0, 1);
				break;
			case EAST:
				GL11.glRotatef(90, 0, 0, 1);
				break;
			}
			double offset = -0.0020000000949949026D;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			buffer.pos(-0.25, -0.5F + offset, -0.5).endVertex();
			buffer.pos(-0.25, -0.5F + offset, 0.5).endVertex();
			buffer.pos(0.25, -0.5F + offset, -0.5).endVertex();
			buffer.pos(0.25, -0.5F + offset, 0.5).endVertex();
			buffer.pos(-0.5, -0.5F + offset, -0.25).endVertex();
			buffer.pos(0.5, -0.5F + offset, -0.25).endVertex();
			buffer.pos(-0.5, -0.5F + offset, 0.25).endVertex();
			buffer.pos(0.5, -0.5F + offset, 0.25).endVertex();
			tess.draw();
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}
		int xGrid = 0, yGrid = 0;
		if (facing.getAxis() == Axis.X) {
			xGrid = hitZ < 0.25F ? 0 : hitZ <= 0.75F ? 1 : 2;
			yGrid = hitY < 0.25F ? 0 : hitY <= 0.75F ? 1 : 2;
		} else if (facing.getAxis() == Axis.Y) {
			xGrid = hitX < 0.25F ? 0 : hitX <= 0.75F ? 1 : 2;
			yGrid = hitZ < 0.25F ? 0 : hitZ <= 0.75F ? 1 : 2;
		} else {
			xGrid = hitX < 0.25F ? 0 : hitX <= 0.75F ? 1 : 2;
			yGrid = hitY < 0.25F ? 0 : hitY <= 0.75F ? 1 : 2;
		}
		if (xGrid != 1 && yGrid != 1) {
			facing = facing.getOpposite();
		} else if (xGrid == 1 && yGrid != 1) {
			switch (facing) {
			case DOWN:
			case UP:
				facing = yGrid == 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
				break;
			case NORTH:
			case SOUTH:
			case WEST:
			case EAST:
				facing = yGrid == 0 ? EnumFacing.DOWN : EnumFacing.UP;
				break;
			}
		} else if (xGrid != 1 && yGrid == 1) {
			switch (facing) {
			case DOWN:
			case UP:
			case NORTH:
			case SOUTH:
				facing = xGrid == 0 ? EnumFacing.WEST : EnumFacing.EAST;
				break;
			case WEST:
			case EAST:
				facing = xGrid == 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
				break;
			}
		}
		System.out.println(xGrid + "|" + yGrid + "|" + facing);
		CableConfig config = ((TECableGeneral) world.getTileEntity(pos)).getConfig(facing);
		System.out.println("SERVER:" + config.canEnergyTransmit() + "|" + config.getItemIOType());
		player.openGui(Zone.instance, GuiElementLoader.CABLE_2_DOWN + facing.getIndex(), world, pos.getX(), pos.getY(),
				pos.getZ());
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TECableGeneral();
	}
}
