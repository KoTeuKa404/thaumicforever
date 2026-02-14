package com.koteuka404.thaumicforever.wand.util;

import com.koteuka404.thaumicforever.wand.api.item.wand.IStaffCore;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandCap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class StaffWrapper {

    IStaffCore core;
    IWandCap cap;

    public StaffWrapper() {
        this(null, null);
    }

    public StaffWrapper(IStaffCore core, IWandCap cap) {
        this.core = core;
        this.cap = cap;
    }


    public boolean canCraft(EntityPlayer player) {
        return isValidStaff() && ThaumcraftCapabilities.knowsResearch(player, "STAFF@1");
    }

    public boolean isValidStaff() {
        return core != null && cap != null && !(cap.getTag() == "iron");
    }

    //TODO
    public int getVisCost() {
        return isValidStaff() ? 1 * cap.getCraftCost() : 0;
    }

    public AspectList getCrystals() {
        AspectList aspects = new AspectList();

        if (isValidStaff()) {
            //TODO
            int cost = Math.max(1, cap.getCraftCost());
            for (Aspect a : Aspect.getPrimalAspects())
                aspects.add(a, cost);
        }

        return aspects;

    }

    public ItemStack makeStaff() {
        return isValidStaff() ? WandHelper.getStaffWithParts(core.getTag(), cap.getTag()) : ItemStack.EMPTY;
    }

    public StaffWrapper copy() {
        return new StaffWrapper(core, cap);
    }

    public IWandCap getCap() {
        return cap;
    }

    public IStaffCore getRod() {
        return core;
    }

    public StaffWrapper setCap(IWandCap cap) {
        this.cap = cap;
        return this;
    }

    public StaffWrapper setCore(IStaffCore core) {
        this.core = core;
        return this;
    }

    @Override
    public String toString() {
        if (core != null && cap != null)
            return "Rod: " + core.getTag() + " | Cap: " + cap.getTag();
        return "";
    }

}
