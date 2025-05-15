package com.koteuka404.thaumicforever;

import javax.annotation.Nullable;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class WizardVillager extends EntityVillager {

    public WizardVillager(World worldIn) {
        super(worldIn);
        this.setProfession(WizardVillagerProfession.WIZARD_PROFESSION);
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.setProfession(WizardVillagerProfession.WIZARD_PROFESSION);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!this.world.isRemote) {
        }
        return super.processInteract(player, hand);
    }

    @Override
    public ResourceLocation getLootTable() {
        return new ResourceLocation("thaumicforever", "entities/wizard");
    }

    @Override
    public void setProfession(VillagerProfession profession) {
        if (profession != WizardVillagerProfession.WIZARD_PROFESSION) {
            super.setProfession(WizardVillagerProfession.WIZARD_PROFESSION);
        } else {
            super.setProfession(profession);
        }
    }
}
