// package com.koteuka404.thaumicforever.client.render;

// import com.koteuka404.thaumicforever.item.ItemKatana;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.entity.AbstractClientPlayer;
// import net.minecraft.client.renderer.GlStateManager;
// import net.minecraft.client.renderer.entity.Render;
// import net.minecraft.client.renderer.entity.RenderPlayer;
// import net.minecraft.util.EnumHand;
// import net.minecraft.util.EnumHandSide;
// import net.minecraft.util.math.MathHelper;
// import net.minecraftforge.client.event.RenderSpecificHandEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// public class KatanaHandRenderHandler {
//     @SubscribeEvent
//     public void renderKatanaHand(RenderSpecificHandEvent event) {
//         Minecraft minecraft = Minecraft.getMinecraft();
//         if (minecraft.player == null
//             || minecraft.player.isInvisible()
//             || event.getItemStack().isEmpty()
//             || !(event.getItemStack().getItem() instanceof ItemKatana)) {
//             return;
//         }

//         EnumHandSide side = event.getHand() == EnumHand.MAIN_HAND
//             ? minecraft.player.getPrimaryHand()
//             : minecraft.player.getPrimaryHand().opposite();

//         Render<?> render = minecraft.getRenderManager().getEntityRenderObject(minecraft.player);
//         if (!(render instanceof RenderPlayer)) {
//             return;
//         }

//         GlStateManager.pushMatrix();
//         renderArmFirstPerson(
//             (RenderPlayer) render,
//             minecraft.player,
//             side,
//             event.getEquipProgress(),
//             event.getSwingProgress()
//         );
//         GlStateManager.popMatrix();
//     }

//     private static void renderArmFirstPerson(
//         RenderPlayer renderer,
//         AbstractClientPlayer player,
//         EnumHandSide side,
//         float equipProgress,
//         float swingProgress
//     ) {
//         boolean rightHand = side == EnumHandSide.RIGHT;
//         float direction = rightHand ? 1.0F : -1.0F;
//         float swingRoot = MathHelper.sqrt(swingProgress);
//         float swingX = -0.3F * MathHelper.sin(swingRoot * (float) Math.PI);
//         float swingY = 0.4F * MathHelper.sin(swingRoot * ((float) Math.PI * 2.0F));
//         float swingZ = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);

//         GlStateManager.translate(
//             direction * (swingX + 0.64000005F),
//             swingY - 0.6F + equipProgress * -0.6F,
//             swingZ - 0.71999997F
//         );
//         GlStateManager.rotate(direction * 45.0F, 0.0F, 1.0F, 0.0F);

//         float swingSin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
//         float swingRootSin = MathHelper.sin(swingRoot * (float) Math.PI);
//         GlStateManager.rotate(direction * swingRootSin * 70.0F, 0.0F, 1.0F, 0.0F);
//         GlStateManager.rotate(direction * swingSin * -20.0F, 0.0F, 0.0F, 1.0F);
//         GlStateManager.translate(direction * -1.0F, 3.6F, 3.5F);
//         GlStateManager.rotate(direction * 120.0F, 0.0F, 0.0F, 1.0F);
//         GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
//         GlStateManager.rotate(direction * -135.0F, 0.0F, 1.0F, 0.0F);
//         GlStateManager.translate(direction * 5.6F, 0.0F, 0.0F);

//         GlStateManager.disableCull();
//         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//         Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
//         if (rightHand) {
//             renderer.renderRightArm(player);
//         } else {
//             renderer.renderLeftArm(player);
//         }
//         GlStateManager.enableCull();
//     }
// }
