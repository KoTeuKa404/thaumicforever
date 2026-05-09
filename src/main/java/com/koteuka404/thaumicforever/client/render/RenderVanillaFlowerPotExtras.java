package com.koteuka404.thaumicforever.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.koteuka404.thaumicforever.compat.FlowerPotCompat;
import com.koteuka404.thaumicforever.config.ModConfig;

@SideOnly(Side.CLIENT)
public class RenderVanillaFlowerPotExtras extends TileEntitySpecialRenderer<TileEntityFlowerPot> {
    // Vanilla flower_pot_cross.json places the plant inside this exact box:
    // x/z 2.6..13.4 and y 4..16.
    private static final double POT_PLANT_XZ_OFFSET = 2.6D / 16.0D;
    private static final double POT_PLANT_Y_OFFSET = 4.0D / 16.0D;
    private static final float POT_PLANT_XZ_SCALE = 10.8F / 16.0F;
    private static final float POT_PLANT_Y_SCALE = 12.0F / 16.0F;

    @Override
    public void render(TileEntityFlowerPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Item item = getPotItem(te);
        if (!(item instanceof ItemBlock)) {
            return;
        }

        Block block = ((ItemBlock) item).getBlock();
        int meta = getPotMeta(te);
        if (!ModConfig.isAllowedVanillaPotPlant(block, meta)) {
            return;
        }
        if (FlowerPotCompat.isRegisteredVanillaPotPlant(block, meta)) {
            // Registered plants use vanilla flower-pot rendering and placement.
            return;
        }
        if (isVanillaPotRenderedByDefault(block, meta)) {
            // Let vanilla render its own supported flowers/saplings.
            return;
        }
        ResourceLocation blockId = block.getRegistryName();
        if (blockId == null) {
            return;
        }
        IBlockState state;
        try {
            state = block.getStateFromMeta(meta);
        } catch (Throwable ignored) {
            state = block.getDefaultState();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + POT_PLANT_XZ_OFFSET, y + POT_PLANT_Y_OFFSET, z + POT_PLANT_XZ_OFFSET);
        GlStateManager.scale(POT_PLANT_XZ_SCALE, POT_PLANT_Y_SCALE, POT_PLANT_XZ_SCALE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (te.getWorld() != null) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(state, te.getPos(), te.getWorld(), buffer);
            buffer.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
        } else {
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, 1.0F);
        }

        GlStateManager.popMatrix();
    }

    private static Item getPotItem(TileEntityFlowerPot te) {
        try {
            // 1.12-style getter
            return te.getFlowerPotItem();
        } catch (Throwable ignored) {
        }

        NBTTagCompound nbt = te.writeToNBT(new NBTTagCompound());
        String id = nbt.getString("Item");
        return id.isEmpty() ? null : Item.getByNameOrId(id);
    }

    private static int getPotMeta(TileEntityFlowerPot te) {
        try {
            // 1.12-style getter
            return te.getFlowerPotData();
        } catch (Throwable ignored) {
        }

        NBTTagCompound nbt = te.writeToNBT(new NBTTagCompound());
        return nbt.getInteger("Data");
    }

    private static boolean isVanillaPotRenderedByDefault(Block block, int meta) {
        if (block == net.minecraft.init.Blocks.YELLOW_FLOWER) return true;
        if (block == net.minecraft.init.Blocks.RED_FLOWER) return true;
        if (block == net.minecraft.init.Blocks.CACTUS) return true;
        if (block == net.minecraft.init.Blocks.BROWN_MUSHROOM) return true;
        if (block == net.minecraft.init.Blocks.RED_MUSHROOM) return true;
        if (block == net.minecraft.init.Blocks.SAPLING) return true;
        if (block == net.minecraft.init.Blocks.DEADBUSH) return true;
        return block == net.minecraft.init.Blocks.TALLGRASS && meta == BlockTallGrass.EnumType.FERN.getMeta();
    }
}
