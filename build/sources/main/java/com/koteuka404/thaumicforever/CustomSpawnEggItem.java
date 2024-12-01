package com.koteuka404.thaumicforever;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class CustomSpawnEggItem extends Item {
    private final ResourceLocation entityID;

    public CustomSpawnEggItem(String modID, String entityName) {
        this.entityID = new ResourceLocation(modID, entityName);
        this.setUnlocalizedName(entityName + "_spawn_egg");
        this.setRegistryName(entityName + "_spawn_egg");
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        RayTraceResult rayTrace = this.rayTrace(world, player, true);
        if (rayTrace == null || rayTrace.typeOfHit != Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (!world.isRemote) {
            Entity entity = EntityList.createEntityByIDFromName(entityID, world);
            if (entity != null) {
                entity.setPosition(rayTrace.hitVec.x, rayTrace.hitVec.y + 1.0, rayTrace.hitVec.z);
                world.spawnEntity(entity);

                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
}
