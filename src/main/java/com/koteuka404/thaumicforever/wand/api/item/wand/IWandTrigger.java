package com.koteuka404.thaumicforever.wand.api.item.wand;

import thaumcraft.api.crafting.IDustTrigger;

public interface IWandTrigger extends IDustTrigger {

    public default int getCost() {
        return 0;
    }

}
