package com.koteuka404.thaumicforever;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ScrollAspectHandler {

    /**
     * @param scrollItemStack ItemStack.
     * @return AspectList with aspect or null if not found.
     */
    public static AspectList getAspectsFromScroll(ItemStack scrollItemStack) {
        if (scrollItemStack == null || scrollItemStack.isEmpty()) {
            return null;
        }

        if (!scrollItemStack.hasTagCompound()) {
            return null;
        }

        NBTTagCompound tag = scrollItemStack.getTagCompound();
        if (tag != null && tag.hasKey("aspects")) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(tag.getCompoundTag("aspects"));

            for (Aspect aspect : aspects.getAspects()) {
            }

            return aspects;
        } else {
        }

        return null;
    }

    /**
     * @param scrollItemStack ItemStack .
     * @param aspects AspectList, what need save.
     */
    public static void saveAspectsToScroll(ItemStack scrollItemStack, AspectList aspects) {
        if (scrollItemStack == null || scrollItemStack.isEmpty() || aspects == null) {
            return;
        }

        if (!scrollItemStack.hasTagCompound()) {
            scrollItemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = scrollItemStack.getTagCompound();
        NBTTagCompound aspectCompound = new NBTTagCompound();
        aspects.writeToNBT(aspectCompound);

        tag.setTag("aspects", aspectCompound);

        for (Aspect aspect : aspects.getAspects()) {
        }
    }
}
