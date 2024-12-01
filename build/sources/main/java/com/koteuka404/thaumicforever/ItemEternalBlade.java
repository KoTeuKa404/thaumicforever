package com.koteuka404.thaumicforever;

import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEternalBlade extends ItemSword {

    private static final float ATTACK_DAMAGE = 23.0F;
    private static final float ATTACK_SPEED = -2.34F; 

    private final Multimap<String, AttributeModifier> attributeModifiers;

    public ItemEternalBlade() {
        super(ToolMaterial.DIAMOND);
        setRegistryName("eternal_blade");
        setUnlocalizedName("eternal_blade");
        setMaxStackSize(1);

        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ATTACK_DAMAGE, 0));
        builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0));
        this.attributeModifiers = builder.build();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            int radius = 3 + world.rand.nextInt(3); 
            AxisAlignedBB area = new AxisAlignedBB(
                    player.posX - radius, player.posY - 1, player.posZ - radius,
                    player.posX + radius, player.posY + 1, player.posZ + radius
            );

            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area, entity -> entity != player);
            for (EntityLivingBase entity : entities) {
                entity.setFire(5); 
            }

            spawnFireParticles(world, player, radius);

            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, player.getSoundCategory(), 1.0F, 1.0F);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @SideOnly(Side.CLIENT)
    private void spawnFireParticles(World world, EntityPlayer player, int radius) {
        for (int r = 1; r <= radius; r++) {
            final double currentRadius = r; // Поточний радіус
            for (int angle = 0; angle < 360; angle += 10) {
                double radian = Math.toRadians(angle);
                double x = player.posX + currentRadius * Math.cos(radian);
                double z = player.posZ + currentRadius * Math.sin(radian);
                double y = player.posY + 0.5;

                // Відкласти спаун частинок
                world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0.05, 0);
            }
        }
    }




    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.DARK_RED + "Eternal Blade";
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return slot == EntityEquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot, stack);
    }
    
}
