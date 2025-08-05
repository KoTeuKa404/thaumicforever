// // CustomHelmetRenderHandler.java
// package com.koteuka404.thaumicforever;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.GlStateManager;
// import net.minecraft.client.renderer.entity.RenderPlayer;
// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.inventory.EntityEquipmentSlot;
// import net.minecraft.item.ItemStack;
// import net.minecraft.util.ResourceLocation;
// import net.minecraftforge.client.event.RenderPlayerEvent;
// import net.minecraftforge.common.MinecraftForge;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// public class CustomHelmetRenderHandler {
//     private final Map<UUID,ItemStack> backup = new HashMap<>();

//     public CustomHelmetRenderHandler() {
//         MinecraftForge.EVENT_BUS.register(this);
//     }

//     @SubscribeEvent
//     public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
//         EntityPlayer p = event.getEntityPlayer();
//         ItemStack helm = p.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
//         if (!helm.isEmpty() && helm.getItem() instanceof ItemCustomHelmet) {
//             backup.put(p.getUniqueID(), helm.copy());
//             p.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
//         }
//         RenderPlayer renderer = event.getRenderer();
//         renderer.getMainModel().bipedHead.showModel     = false;
//         renderer.getMainModel().bipedHeadwear.showModel = false;
//     }

//     @SubscribeEvent
//     public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
//         EntityPlayer p = event.getEntityPlayer();
//         ItemStack saved = backup.remove(p.getUniqueID());
//         if (saved != null) {
//             p.setItemStackToSlot(EntityEquipmentSlot.HEAD, saved);
//             GlStateManager.pushMatrix();
//             event.getRenderer().getMainModel().bipedHead.postRender(0.0625F);
//             GlStateManager.translate(0F, -0.25F, 0F);

//             Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(ItemCustomHelmet.NIMBUS_TEXTURE));

//             new CustomHelmetModel().render(p, 0,0,0,0,0,0.0625F);

//             GlStateManager.popMatrix();
//         }
//     }
// }
