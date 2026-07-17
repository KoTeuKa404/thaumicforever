package com.koteuka404.thaumicforever.interaction;

import com.koteuka404.thaumicforever.registry.ModBlocks;

import thaumcraft.api.crafting.Part;

public final class GreatResearchTableMultiblockDef {
    public static final Part RESEARCH_TABLE = new Part(com.wonginnovations.oldresearch.common.blocks.ModBlocks.RESEARCHTABLE, 0);
    public static final Part GREATWOOD_TABLE = new Part(ModBlocks.GREATWOOD_TABLE, 0);

    public static final Part[][][] SHAPE = new Part[][][] {
        {
            { RESEARCH_TABLE, GREATWOOD_TABLE }
        }
    };

    private GreatResearchTableMultiblockDef() {}
}
