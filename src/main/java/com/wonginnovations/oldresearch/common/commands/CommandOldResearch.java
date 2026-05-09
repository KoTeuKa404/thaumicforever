package com.wonginnovations.oldresearch.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wonginnovations.oldresearch.OldResearch;
import com.wonginnovations.oldresearch.common.items.ModItems;
import com.wonginnovations.oldresearch.common.lib.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.lib.research.ResearchNoteData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CommandOldResearch extends CommandBase {
    private static final int MAX_ASPECTS = 20;

    @Override
    public String getName() {
        return "oldresearch";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/oldresearch setdifficult <aspect_count> <player> OR /oldresearch setplayerdifficult <base_aspect_count> <player>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new WrongUsageException(getUsage(sender));

        String action = args[0];
        EntityPlayerMP player;
        int aspectCount = -1;
        if ("setdifficult".equalsIgnoreCase(action)) {
            if (args.length != 3) throw new WrongUsageException(getUsage(sender));
            aspectCount = Math.max(1, Math.min(MAX_ASPECTS, parseInt(args[1])));
            player = getPlayer(server, sender, args[2]);
        } else if ("setplayerdifficult".equalsIgnoreCase(action)) {
            if (args.length != 3) throw new WrongUsageException(getUsage(sender));
            aspectCount = Math.max(1, Math.min(MAX_ASPECTS, parseInt(args[1])));
            player = getPlayer(server, sender, args[2]);
            setPlayerDifficulty(player, aspectCount);
            sender.sendMessage(new TextComponentString("Set " + player.getName() + "'s oldresearch player difficulty to " + aspectCount + "."));
            return;
        } else {
            throw new WrongUsageException(getUsage(sender));
        }

        ItemStack note = getHeldResearchNote(player);
        if (note.isEmpty()) {
            throw new CommandException("Player must hold an oldresearch research note in main hand or off hand.");
        }

        ResearchNoteData data = OldResearchManager.getData(note);
        if (data == null) {
            throw new CommandException("Unable to read research note data.");
        }

        applyDifficulty(server, player, note, data, aspectCount);
        sender.sendMessage(new TextComponentString("Set " + player.getName() + "'s research note difficulty to " + aspectCount + " aspect(s)."));
    }

    private static void setPlayerDifficulty(EntityPlayerMP player, int difficulty) {
        int completedResearch = Math.max(0, (difficulty - 1) * 10);
        OldResearch.proxy.getPlayerKnowledge().setResearchCompleted(player.getGameProfile().getName(), completedResearch);
    }

    private static void applyDifficulty(MinecraftServer server, EntityPlayerMP player, ItemStack note, ResearchNoteData data, int aspectCount) {
        AspectList aspects = createRandomAspects(server, aspectCount);
        data.complete = false;
        data.aspects = aspects;
        data.hexEntries.clear();
        data.hexes.clear();
        data.generateHexes(player.world, player, aspects, aspectCount);
        OldResearchManager.updateData(note, data);
        note.setItemDamage(0);
        player.inventoryContainer.detectAndSendChanges();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "setdifficult", "setplayerdifficult");
        }
        if (args.length == 3 && ("setdifficult".equalsIgnoreCase(args[0]) || "setplayerdifficult".equalsIgnoreCase(args[0]))) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }

    private static ItemStack getHeldResearchNote(EntityPlayerMP player) {
        ItemStack main = player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() == ModItems.RESEARCHNOTE) {
            return main;
        }

        ItemStack off = player.getHeldItem(EnumHand.OFF_HAND);
        if (!off.isEmpty() && off.getItem() == ModItems.RESEARCHNOTE) {
            return off;
        }

        return ItemStack.EMPTY;
    }

    private static AspectList createRandomAspects(MinecraftServer server, int count) {
        List<Aspect> pool = new ArrayList<>();
        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspect != null) {
                pool.add(aspect);
            }
        }

        Collections.shuffle(pool, server.getEntityWorld().rand);
        AspectList list = new AspectList();
        for (int i = 0; i < Math.min(count, pool.size()); i++) {
            list.add(pool.get(i), 1);
        }
        return list;
    }
}
