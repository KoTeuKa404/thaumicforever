package com.koteuka404.thaumicforever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class ItemStructureSpawner extends Item {

    public ItemStructureSpawner() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("structure_spawner");
        this.setRegistryName("structure_spawner");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            MinecraftServer server = world.getMinecraftServer();
            if (server == null) {
                player.sendMessage(new TextComponentString("Failed to access server!"));
                return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
            }
            BlockPos spawnPos = player.getPosition().up();

            try {
                TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
                ResourceLocation templateName = new ResourceLocation("thaumicforever", "saved_structure");
                Template template = manager.getTemplate(server, templateName);

                if (template == null || template.getSize().equals(BlockPos.ORIGIN)) {
                    player.sendMessage(new TextComponentString("Structure not found or is empty!"));
                    return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
                }

                template.addBlocksToWorld(world, spawnPos, new PlacementSettings());
                player.sendMessage(new TextComponentString("Structure spawned successfully!"));
            } catch (Exception e) {
                player.sendMessage(new TextComponentString("Failed to spawn structure: " + e.getMessage()));
                e.printStackTrace();
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
