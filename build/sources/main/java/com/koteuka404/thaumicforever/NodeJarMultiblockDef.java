package com.koteuka404.thaumicforever;

import net.minecraft.init.Blocks;
import thaumcraft.api.crafting.Part;

public class NodeJarMultiblockDef {

    public static final Part GLASS = new Part(Blocks.GLASS, 0);
    public static final Part SLAB = new Part(Blocks.WOODEN_SLAB, 0); 
    public static final Part NODE = null;

    public static final Part[][][] SHAPE = new Part[][][] {
        {
            { SLAB, SLAB, SLAB },
            { SLAB, SLAB, SLAB },
            { SLAB, SLAB, SLAB }
        },
        {
            { GLASS, GLASS, GLASS },
            { GLASS, GLASS, GLASS },
            { GLASS, GLASS, GLASS }
        },
        {
            { GLASS, GLASS, GLASS },
            { GLASS, NODE,  GLASS },
            { GLASS, GLASS, GLASS }
        },
        {
            { GLASS, GLASS, GLASS },
            { GLASS, GLASS, GLASS },
            { GLASS, GLASS, GLASS }
        }
        
    };

   
    
}
