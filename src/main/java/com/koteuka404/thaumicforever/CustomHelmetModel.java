// package com.koteuka404.thaumicforever;

// import net.minecraft.client.model.ModelBase;
// import net.minecraft.client.model.ModelBox;
// import net.minecraft.client.model.ModelRenderer;
// import net.minecraft.entity.Entity;

// public class CustomHelmetModel extends ModelBase {
//     private final ModelRenderer bb_main;

//     public CustomHelmetModel() {
//         textureWidth  = 64;
//         textureHeight = 64;

//         bb_main = new ModelRenderer(this);
//         bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);

//         bb_main.cubeList.add(new ModelBox(bb_main,  0, 32, -6F, -10F, -8F, 14, 2, 2, 0F, false));
//         bb_main.cubeList.add(new ModelBox(bb_main,  0,  0, -8F, -10F, -8F,  2, 2,14, 0F, false));
//         bb_main.cubeList.add(new ModelBox(bb_main, 32,  0, -8F, -10F,  6F, 14, 2, 2, 0F, false));
//         bb_main.cubeList.add(new ModelBox(bb_main,  0, 16,  6F, -10F, -6F,  2, 2,14, 0F, false));
//     }

//     @Override
//     public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
//         bb_main.render(scale);
//     }
// }
