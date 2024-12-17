package com.koteuka404.thaumicforever;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class PlayerStructureManager {
    private static final Map<UUID, BlockPos> playerStructures = new HashMap<>();
    private static final String STRUCTURE_NAME = "thaumicforever:void";

    public static BlockPos getOrCreateStructureForPlayer(EntityPlayerMP player, World world) {
        UUID playerID = player.getUniqueID();

        // Перевірка, чи вже є структура для гравця
        if (playerStructures.containsKey(playerID)) {
            return playerStructures.get(playerID);
        } else {
            // Генеруємо унікальну позицію структури
            BlockPos structurePosition = generateUniqueStructurePosition(playerID, world);
            generateStructure(world, structurePosition);
            playerStructures.put(playerID, structurePosition);
            return structurePosition.add(15, 8, 15); // Зсув для точки спавну
        }
    }

    private static BlockPos generateUniqueStructurePosition(UUID playerID, World world) {
        // Створюємо унікальні координати на основі UUID гравця
        int x = (int) (playerID.hashCode() * 12345L % 100000L) - 50000; // Діапазон (-50000, 50000)
        int z = (int) (playerID.hashCode() * 67890L % 100000L) - 50000;
        int y = 64; // Висота спавну

        // Перевірка на колізію
        BlockPos position = new BlockPos(x, y, z);
        while (!world.isAirBlock(position) || world.isBlockLoaded(position)) {
            x += 16; // Зміщення на чанки для уникнення перетинів
            z += 16;
            position = new BlockPos(x, y, z);
        }

        System.out.println("Генеруємо унікальну структуру для гравця на координатах: " + position);
        return position;
    }

    private static void generateStructure(World world, BlockPos position) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.getTemplate(world.getMinecraftServer(), new ResourceLocation(STRUCTURE_NAME));

        if (template != null) {
            template.addBlocksToWorld(world, position, new net.minecraft.world.gen.structure.template.PlacementSettings());
            System.out.println("Структура згенерована на позиції: " + position);
        } else {
            System.err.println("Шаблон не знайдено: " + STRUCTURE_NAME);
        }
    }
}
