package com.pinball3d.zone.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBullet extends Entity {
	public EntityLivingBase shootingEntity;
	private int ticksAlive;
	private int ticksInAir;
	public double accelerationX;
	public double accelerationY;
	public double accelerationZ;

	public EntityBullet(World worldIn) {
		super(worldIn);
		this.setSize(1.0F, 1.0F);
	}

	public EntityBullet(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
		super(worldIn);
		this.setSize(1.0F, 1.0F);
		this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
		this.setPosition(x, y, z);
		double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
		this.accelerationX = accelX / d0 * 0.1D;
		this.accelerationY = accelY / d0 * 0.1D;
		this.accelerationZ = accelZ / d0 * 0.1D;
	}

	public EntityBullet(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
		super(worldIn);
		this.shootingEntity = shooter;
		this.setSize(1.0F, 1.0F);
		this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		accelX = accelX + this.rand.nextGaussian() * 0.4D;
		accelY = accelY + this.rand.nextGaussian() * 0.4D;
		accelZ = accelZ + this.rand.nextGaussian() * 0.4D;
		double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
		this.accelerationX = accelX / d0 * 0.1D;
		this.accelerationY = accelY / d0 * 0.1D;
		this.accelerationZ = accelZ / d0 * 0.1D;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	@Override
	public void onUpdate() {
		if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead)
				&& this.world.isBlockLoaded(new BlockPos(this))) {
			super.onUpdate();

			++this.ticksInAir;
			RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, this.ticksInAir >= 25,
					this.shootingEntity);

			if (raytraceresult != null && raytraceresult.entityHit != null
					&& !raytraceresult.entityHit.equals(this.shootingEntity)
					&& !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
				this.onImpact(raytraceresult);
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
			float f = this.getMotionFactor();

			if (this.isInWater()) {
				for (int i = 0; i < 4; ++i) {
					float f1 = 0.25F;
					this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D,
							this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX,
							this.motionY, this.motionZ);
				}

				f = 0.8F;
			}

			this.motionX += this.accelerationX;
			this.motionY += this.accelerationY;
			this.motionZ += this.accelerationZ;
			this.motionX *= f;
			this.motionY *= f;
			this.motionZ *= f;
			this.setPosition(this.posX, this.posY, this.posZ);
		} else {
			this.setDead();
		}
	}

	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
			if (result.entityHit != null) {
				result.entityHit.attackEntityFrom(causeBulletDamage(this, this.shootingEntity), 5.0F);
			}
		}
		this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX - this.motionX * 0.25D,
				this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY,
				this.motionZ);
		this.setDead();
	}

	protected float getMotionFactor() {
		return 0.95F;
	}

	public static DamageSource causeBulletDamage(EntityBullet arrow, @Nullable Entity indirectEntityIn) {
		return (new EntityDamageSourceIndirect("bullet", arrow, indirectEntityIn)).setProjectile();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setTag("direction", this.newDoubleNBTList(new double[] { this.motionX, this.motionY, this.motionZ }));
		compound.setTag("power",
				this.newDoubleNBTList(new double[] { this.accelerationX, this.accelerationY, this.accelerationZ }));
		compound.setInteger("life", this.ticksAlive);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("power", 9)) {
			NBTTagList nbttaglist = compound.getTagList("power", 6);

			if (nbttaglist.tagCount() == 3) {
				this.accelerationX = nbttaglist.getDoubleAt(0);
				this.accelerationY = nbttaglist.getDoubleAt(1);
				this.accelerationZ = nbttaglist.getDoubleAt(2);
			}
		}

		this.ticksAlive = compound.getInteger("life");

		if (compound.hasKey("direction", 9) && compound.getTagList("direction", 6).tagCount() == 3) {
			NBTTagList nbttaglist1 = compound.getTagList("direction", 6);
			this.motionX = nbttaglist1.getDoubleAt(0);
			this.motionY = nbttaglist1.getDoubleAt(1);
			this.motionZ = nbttaglist1.getDoubleAt(2);
		} else {
			this.setDead();
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	@Override
	public float getBrightness() {
		return 1.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		return 15728880;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderInPass(int pass) {
		return false;
	}
}
