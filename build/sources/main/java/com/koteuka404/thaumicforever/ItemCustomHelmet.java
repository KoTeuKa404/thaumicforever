// // ItemCustomHelmet.java
// package com.koteuka404.thaumicforever;

// import net.minecraft.client.model.ModelBiped;
// import net.minecraft.entity.EntityLivingBase;
// import net.minecraft.inventory.EntityEquipmentSlot;
// import net.minecraft.item.ItemArmor;
// import net.minecraft.item.ItemStack;
// import net.minecraftforge.fml.relauncher.Side;
// import net.minecraftforge.fml.relauncher.SideOnly;

// public class ItemCustomHelmet extends ItemArmor {
//     public static final String NIMBUS_TEXTURE = "thaumicforever:textures/blocks/arcane_stone_1.png";

//     public ItemCustomHelmet(ArmorMaterial material) {
//         super(material, 0, EntityEquipmentSlot.HEAD);
//     }

//     @Override
//     @SideOnly(Side.CLIENT)
//     public ModelBiped getArmorModel(EntityLivingBase player, ItemStack stack,EntityEquipmentSlot slot, ModelBiped _default) {
//         if (slot == EntityEquipmentSlot.HEAD && stack.getItem() instanceof ItemCustomHelmet) {
//             ModelBiped empty = new ModelBiped(1.0F);
//             empty.bipedHead.showModel     = false;
//             empty.bipedHeadwear.showModel = false;
//             empty.isChild      = _default.isChild;
//             empty.isRiding     = _default.isRiding;
//             empty.isSneak      = _default.isSneak;
//             empty.rightArmPose = _default.rightArmPose;
//             empty.leftArmPose  = _default.leftArmPose;
//             return empty;
//         }
//         return null;
//     }
// }
