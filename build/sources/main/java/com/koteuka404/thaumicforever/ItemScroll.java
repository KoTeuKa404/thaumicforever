package com.koteuka404.thaumicforever;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ItemScroll extends Item {
    private final String type; // _p, _o, _c

    public ItemScroll(String type) {
        this.type = type;
        setUnlocalizedName("scroll" + type);
        setRegistryName("scroll" + type);
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getMetadata();
        return super.getUnlocalizedName() + "_" + metadata;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (int meta = 0; meta <= 3; meta++) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if ("_p".equals(type)) {
                transformToOpenScroll(stack, player);
            } else if ("_c".equals(type)) {
                unlockResearch(stack, player);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void transformToOpenScroll(ItemStack stack, EntityPlayer player) {
        int meta = stack.getMetadata();
        ItemStack newStack = new ItemStack(ModItems.SCROLL_O, 1, meta);

        if (!newStack.hasTagCompound()) {
            newStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = newStack.getTagCompound();
        String research = ResearchList.getRandomResearch(); 
        tag.setString("research", research);

        AspectList aspects = generateRandomAspects();
        tag.setTag("aspects", serializeAspectList(aspects));

        System.out.println("Generated research: " + research);
        System.out.println("Generated aspects:");
        for (Aspect aspect : aspects.getAspects()) {
            System.out.println(" - " + aspect.getName() + ": " + aspects.getAmount(aspect));
        }

        if (!player.addItemStackToInventory(newStack)) {
            player.dropItem(newStack, false);
        }

        stack.shrink(1);
    }

   private void unlockResearch(ItemStack stack, EntityPlayer player) {
    if (stack.hasTagCompound() && stack.getTagCompound().hasKey("research")) {
        String researchKey = stack.getTagCompound().getString("research");

        if (!player.world.isRemote && player instanceof EntityPlayerMP) {
            EntityPlayerMP serverPlayer = (EntityPlayerMP) player;

            ThaumcraftApi.internalMethods.completeResearch(serverPlayer, researchKey);

            System.out.println("Research unlocked with animation: " + researchKey);

            stack.shrink(1);
        }
    } else {
        System.out.println("Scroll does not contain a research key.");
    }
}


    

    private NBTTagCompound serializeAspectList(AspectList aspects) {
        NBTTagCompound compound = new NBTTagCompound();
        for (Aspect aspect : aspects.getAspects()) {
            compound.setInteger(aspect.getTag(), aspects.getAmount(aspect));
        }
        return compound;
    }

    private AspectList generateRandomAspects() {
        AspectList aspects = new AspectList();
        Aspect[] availableAspects = Aspect.aspects.values().toArray(new Aspect[0]);
    
        int fixedAmount = 7;
        int aspectCount = 7; 
    
        System.out.println("Generating random aspects with fixed amount of " + fixedAmount + ":");
        for (int i = 0; i < aspectCount; i++) {
            Aspect randomAspect = availableAspects[(int) (Math.random() * availableAspects.length)];
            if (!aspects.aspects.containsKey(randomAspect)) { 
                aspects.add(randomAspect, fixedAmount);
                System.out.println(" - Added aspect: " + randomAspect.getName() + " with amount: " + fixedAmount);
            } else {
                i--; 
            }
        }
    
        return aspects;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("research")) {
            String research = stack.getTagCompound().getString("research");
            if ("_c".equals(type)) {
                tooltip.add(TextFormatting.GOLD + research);
            } else {
                tooltip.add(TextFormatting.YELLOW + "Unknown Theory"); 
            }
        }
        // if (stack.hasTagCompound() && stack.getTagCompound().hasKey("aspects")) {
        //     NBTTagCompound tag = stack.getTagCompound().getCompoundTag("aspects");
        //     tooltip.add(TextFormatting.GREEN + "Required Aspects:");
        //     for (String key : tag.getKeySet()) {
        //         tooltip.add(" - " + key + ": " + tag.getInteger(key));
        //     }
        // }
    }
}
