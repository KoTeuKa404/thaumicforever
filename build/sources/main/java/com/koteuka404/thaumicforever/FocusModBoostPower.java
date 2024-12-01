package com.koteuka404.thaumicforever;

import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.casters.FocusMod;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.Trajectory;

public class FocusModBoostPower extends FocusMod {

    private static final int MAX_USES = 2;

    @Override
    public String getResearch() {
        return "FOCUSSPLIT";
    }

    @Override
    public String getKey() {
        return "thaumicforever.BOOSTPOWER";
    }

    @Override
    public int getComplexity() {
        return 10;
    }

    @Override
    public float getPowerMultiplier() {
        return 2.0f; 
    }

    @Override
    public EnumSupplyType[] mustBeSupplied() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public RayTraceResult[] supplyTargets() {
        return getParent().supplyTargets();
    }

    @Override
    public Trajectory[] supplyTrajectories() {
        return getParent().supplyTrajectories(); 
    }

    @Override
    public boolean execute() {
        return true;
    }

    public boolean canApply(FocusPackage focusPackage) {
        int uses = 0;

        if (focusPackage.nodes != null) {
            for (IFocusElement element : focusPackage.nodes) { 
                if (element instanceof FocusModBoostPower) {
                    uses++;
                }
            }
        }

        return uses < MAX_USES;
    }
}
