// package com.koteuka404.thaumicforever;

// import java.util.Random;

// import net.minecraft.entity.Entity;
// import net.minecraft.entity.MoverType;
// import net.minecraft.nbt.NBTTagCompound;
// import net.minecraft.world.World;
// import thaumcraft.client.fx.FXDispatcher;

// public class EntityAirCurrent extends Entity {

//     private static final Random random = new Random();
//     private int lifetime = 200; 

//     public EntityAirCurrent(World world) {
//         super(world);
//         this.setSize(0.1f, 0.1f); 
//     }

//     public EntityAirCurrent(World world, double x, double y, double z) {
//         this(world);
//         this.setPosition(x, y, z);
//         this.motionX = (random.nextDouble() - 0.5) * 0.2;
//         this.motionY = (random.nextDouble() * 0.05);
//         this.motionZ = (random.nextDouble() - 0.5) * 0.2;
//     }

//     @Override
//     public void onUpdate() {
//         super.onUpdate();
    
//         if (lifetime-- <= 0) {
//             this.setDead(); 
//             return;
//         }
    
//         this.motionY *= 0.98; 
//         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    
//         for (int i = 0; i < 0.2; i++) { 
//             FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
//             pp.grav = -0.02f;
//             pp.age = 40 + world.rand.nextInt(20);
//             pp.alpha = new float[]{0.6f, 0.0f};
//             pp.grid = 32;
//             pp.partStart = 337;
//             pp.partInc = 1;
//             pp.partNum = 5;
//             pp.slowDown = 0.9;
//             pp.rot = (float) world.rand.nextGaussian() / 2.0f;
//             float s = (float) (1.5 + world.rand.nextGaussian() * 0.3);
//             pp.scale = new float[]{s, s * 1.2f};
    
//             FXDispatcher.INSTANCE.drawGenericParticles(
//                 posX + (random.nextDouble() - 0.5) * 1.5,
//                 posY + (random.nextDouble() * 1.5),
//                 posZ + (random.nextDouble() - 0.5) * 1.5,
//                 (random.nextDouble() - 0.5) * 0.1,
//                 0.05 + (random.nextDouble() * 0.05),
//                 (random.nextDouble() - 0.5) * 0.1,
//                 pp
//             );
//         }
//     }
    

//     @Override
//     protected void entityInit() {}

//     @Override
//     protected void readEntityFromNBT(NBTTagCompound compound) {
//         this.lifetime = compound.getInteger("lifetime");
//     }

//     @Override
//     protected void writeEntityToNBT(NBTTagCompound compound) {
//         compound.setInteger("lifetime", this.lifetime);
//     }
// }
