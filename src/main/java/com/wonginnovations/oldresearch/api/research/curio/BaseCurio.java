package com.wonginnovations.oldresearch.api.research.curio;

import com.wonginnovations.oldresearch.OldResearch;
import com.wonginnovations.oldresearch.common.lib.research.ScanManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

public class BaseCurio {

    private String name;
    private String category;
    private AspectList aspects = new AspectList();
    private final int[] warp = new int[]{0,0,0};
    private ResourceLocation texture;

    public BaseCurio() {}

    public BaseCurio(String name) {
        this.name = name;
        this.texture = new ResourceLocation("oldresearch", "curio_" + name);
    }

    public BaseCurio(String name, ResourceLocation texture) {
        this.name = name;
        this.texture = texture;
    }

    public BaseCurio setName(String name) {
        this.name = name;
        return this;
    }

    public BaseCurio setCategory(String category) {
        this.category = category;
        return this;
    }

    public BaseCurio aspect(Aspect aspect) {
        return this.aspect(aspect, 1);
    }

    public BaseCurio aspect(Aspect aspect, int amount) {
        this.aspects.add(aspect, amount);
        return this;
    }

    public BaseCurio setAspects(AspectList aspects) {
        this.aspects = aspects;
        return this;
    }

    public BaseCurio setWarp(IPlayerWarp.EnumWarpType type, int i) {
        warp[type.ordinal()] = i;
        return this;
    }

    public BaseCurio setTexture(String texture) {
        this.texture = new ResourceLocation(texture);
        return this;
    }

    public BaseCurio setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public boolean onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        if (!worldIn.isRemote) {
            ResearchCategory researchCategory = this.category != null
                    ? ResearchCategories.getResearchCategory(this.category)
                    : null;
            AspectList aspectList = (researchCategory != null)
                    ? researchCategory.formula
                    : aspects;
            for (Aspect aspect : aspectList.getAspects()) {
                if (OldResearch.proxy.playerKnowledge.hasDiscoveredParentAspects(player.getGameProfile().getName(), aspect)) {
                    ScanManager.checkAndSyncAspectKnowledge(player, aspect, (int) Math.floor(aspectList.getAmount(aspect) * (player.getRNG().nextFloat() / 2.0F)));
                }
            }
            if (researchCategory != null) {
                int obsProg = IPlayerKnowledge.EnumKnowledgeType.OBSERVATION.getProgression();
                int theoryProg = IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression();
                ThaumcraftApi.internalMethods.addKnowledge(
                        player,
                        IPlayerKnowledge.EnumKnowledgeType.OBSERVATION,
                        researchCategory,
                        MathHelper.getInt(player.getRNG(), obsProg / 2, obsProg)
                );
                ThaumcraftApi.internalMethods.addKnowledge(
                        player,
                        IPlayerKnowledge.EnumKnowledgeType.THEORY,
                        researchCategory,
                        MathHelper.getInt(player.getRNG(), theoryProg / 3, theoryProg / 2)
                );
            }
            int i = 0;
            for (int n : warp) {
                if (n != 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, n, IPlayerWarp.EnumWarpType.values()[i]);
                }
                ++i;
            }
        }
        return true;
    }

}
