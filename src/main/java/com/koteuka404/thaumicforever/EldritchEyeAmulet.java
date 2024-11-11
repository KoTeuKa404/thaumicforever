package com.koteuka404.thaumicforever;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

public class EldritchEyeAmulet extends Item implements IBauble {

    public EldritchEyeAmulet() {
        super();
        this.setMaxStackSize(1);
        this.setUnlocalizedName("eldritch_eye_amulet");
        this.setRegistryName("eldritch_eye_amulet");
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer && player.world.isRemote) {
            World world = player.world;
            float x = (float)(player.posX + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2f);
            float z = (float)(player.posZ + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, (float)(player.posY + 0.22 * player.height), z, player);
        }
    }
}
