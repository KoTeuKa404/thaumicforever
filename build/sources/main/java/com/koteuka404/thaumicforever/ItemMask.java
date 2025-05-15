// package com.koteuka404.thaumicforever;

// import java.util.List;

// import javax.annotation.Nullable;

// import net.minecraft.client.model.ModelBiped;
// import net.minecraft.client.model.ModelRenderer;
// import net.minecraft.client.util.ITooltipFlag;
// import net.minecraft.entity.Entity;
// import net.minecraft.entity.EntityLivingBase;
// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.inventory.EntityEquipmentSlot;
// import net.minecraft.item.ItemArmor;
// import net.minecraft.item.ItemStack;
// import net.minecraft.util.text.TextComponentTranslation;
// import net.minecraft.util.text.TextFormatting;
// import net.minecraft.world.World;
// import net.minecraftforge.common.util.EnumHelper;
// import net.minecraftforge.event.entity.living.LivingHurtEvent;
// import net.minecraftforge.fml.common.Mod;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import net.minecraftforge.fml.relauncher.Side;
// import net.minecraftforge.fml.relauncher.SideOnly;

// @Mod.EventBusSubscriber
// public class ItemMask extends ItemArmor {

//     private static final ArmorMaterial MAGIC_ARMOR_MATERIAL = EnumHelper.addArmorMaterial(
//             "mask", "thaumicforever:mask", 33, new int[]{3, 6, 8, 3}, 25,
//             net.minecraft.util.SoundEvent.REGISTRY.getObjectById(1), 2.0F);

//     public ItemMask() {
//         super(MAGIC_ARMOR_MATERIAL, 0, EntityEquipmentSlot.HEAD);
//         setRegistryName("mask");
//         setUnlocalizedName("mask");
//     }
//     @SideOnly(Side.CLIENT)
//     @Override
//     public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//         tooltip.add(TextFormatting.AQUA + new TextComponentTranslation("item.mask.tooltip").getFormattedText());
//     }

//     @SubscribeEvent
//     public static void onLivingHurt(LivingHurtEvent event) {
//         if (event.getEntityLiving() instanceof EntityPlayer) {
//             EntityPlayer player = (EntityPlayer) event.getEntityLiving();
//             ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

//             if (helmet != null && helmet.getItem() instanceof ItemMask) {
//                 if (event.getSource().isMagicDamage()) {
//                     float reducedDamage = event.getAmount() * 0.5F; // Reduce magic damage by 50%
//                     event.setAmount(reducedDamage);
//                 }
//             }
//         }
//     }

//     @Override
//     @SideOnly(Side.CLIENT)
//     public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
//         if (armorSlot == EntityEquipmentSlot.HEAD) {
//             return new ModelBiped() {
//                 // Define the mask model directly here
//                 private final ModelRenderer mask;

//                 {
//                     this.textureWidth = 64;
//                     this.textureHeight = 64;

//                     this.mask = new ModelRenderer(this);
//                     this.mask.setRotationPoint(0.0F, 0.0F, 0.0F);
//                     this.mask.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
//                     this.bipedHead.addChild(this.mask);
//                 }

//                 @Override
//                 public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//                     this.mask.render(scale);
//                 }
//             };
//         }

//         return defaultModel;
//     }

// }
