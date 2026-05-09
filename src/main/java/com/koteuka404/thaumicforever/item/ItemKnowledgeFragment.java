package com.koteuka404.thaumicforever.item;

import com.wonginnovations.oldresearch.OldResearch;
import com.wonginnovations.oldresearch.common.lib.network.PacketAspectPool;
import com.wonginnovations.oldresearch.common.lib.network.PacketHandler;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ItemKnowledgeFragment extends Item {

    private static final String NBT_ASPECTS = "Aspects";

    public ItemKnowledgeFragment() {
        setUnlocalizedName("knowledge_fragment");
        setRegistryName("knowledge_fragment");
        setMaxStackSize(64);
    }

    public static ItemStack create(AspectList aspects) {
        ItemStack stack = new ItemStack(com.koteuka404.thaumicforever.registry.ModItems.KNOWLEDGE_FRAGMENT);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound aspectTag = new NBTTagCompound();
        if (aspects != null) {
            for (Aspect aspect : aspects.getAspects()) {
                if (aspect != null && aspects.getAmount(aspect) > 0) {
                    aspectTag.setInteger(aspect.getTag(), aspects.getAmount(aspect));
                }
            }
        }
        tag.setTag(NBT_ASPECTS, aspectTag);
        stack.setTagCompound(tag);
        return stack;
    }

    public static AspectList getStoredAspects(ItemStack stack) {
        AspectList aspects = new AspectList();
        if (stack.isEmpty() || !stack.hasTagCompound()) return aspects;
        NBTTagCompound tag = stack.getTagCompound().getCompoundTag(NBT_ASPECTS);
        for (String key : tag.getKeySet()) {
            Aspect aspect = Aspect.getAspect(key);
            int amount = tag.getInteger(key);
            if (aspect != null && amount > 0) {
                aspects.add(aspect, amount);
            }
        }
        return aspects;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        AspectList aspects = getStoredAspects(stack);
        if (aspects == null || aspects.size() <= 0) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (!world.isRemote) {
            String name = player.getGameProfile().getName();
            for (Aspect aspect : aspects.getAspects()) {
                int amount = aspects.getAmount(aspect);
                if (aspect == null || amount <= 0) continue;
                OldResearch.proxy.playerKnowledge.addAspectPool(name, aspect, amount);
                if (player instanceof EntityPlayerMP) {
                    int total = OldResearch.proxy.playerKnowledge.getAspectPoolFor(name, aspect);
                    PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), amount, total), (EntityPlayerMP) player);
                }
            }
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        AspectList aspects = getStoredAspects(stack);
        if (aspects == null || aspects.size() <= 0) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("tooltip.knowledge_fragment.empty"));
            return;
        }
        tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("tooltip.knowledge_fragment.aspects"));
        for (Aspect aspect : aspects.getAspectsSortedByName()) {
            tooltip.add(TextFormatting.GRAY + aspect.getName() + TextFormatting.WHITE + " x" + aspects.getAmount(aspect));
        }
    }
}
