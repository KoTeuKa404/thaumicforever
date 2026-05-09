package com.koteuka404.thaumicforever.wand.item;

import com.koteuka404.thaumicforever.entity.AuraNodeEntity;
import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.wand.api.ThaumicWandsAPI;
import com.koteuka404.thaumicforever.wand.api.item.wand.IScepter;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandCap;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWandRod;
import com.koteuka404.thaumicforever.wand.util.LocalizationHelper;
import com.koteuka404.thaumicforever.wand.util.WandHelper;
import com.koteuka404.thaumicforever.wand.wand.TW_Wands;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.casters.CasterTriggerRegistry;
import thaumcraft.api.casters.IInteractWithCaster;

import java.util.List;

public class ItemScepter extends ItemBase implements IScepter {

    public ItemScepter(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == this.getCreativeTab()) {
            for (ItemStack wand : new ItemStack[]{WandHelper.getScepterWithParts("silverwood", "thaumium")}) {
                items.add(wand.copy());
                ItemStack charged = wand.copy();
                WandHelper.fillPrimalCharge(charged, null);
                items.add(charged);
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        if (stack != null && stack.getItem() instanceof ItemScepter)
            if (entity instanceof EntityPlayer && itemSlot < 9)
                if (getRod(stack).hasUpdate())
                    getRod(stack).getUpdate().onUpdate(stack, (EntityPlayer) entity);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = I18n.translateToLocal("item.wand.name");
        name = name.replace("%CAP", LocalizationHelper.localize("item.wand." + getCap(stack).getTag() + ".cap"));
        name = name.replace("%ROD", LocalizationHelper.localize("item.wand." + getRod(stack).getTag() + ".rod"));
        name = name.replace("%OBJ", LocalizationHelper.localize("item.wand.scepter.obj"));
        return name;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState bs = world.getBlockState(pos);
        if (bs.getBlock() instanceof IInteractWithCaster && ((IInteractWithCaster) bs.getBlock()).onCasterRightClick(world, player.getHeldItem(hand), player, pos, side, hand)) {
            return EnumActionResult.PASS;
        } else {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof IInteractWithCaster && ((IInteractWithCaster) tile).onCasterRightClick(world, player.getHeldItem(hand), player, pos, side, hand)) {
                return EnumActionResult.PASS;
            } else if (CasterTriggerRegistry.hasTrigger(bs)) {
                return CasterTriggerRegistry.performTrigger(world, player.getHeldItem(hand), player, pos, side, bs) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
            } else {
                return EnumActionResult.PASS;
            }
        }
    }

    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 72000;
    }

    public EnumAction getItemUseAction(ItemStack stack1) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack scepter = player.getHeldItem(hand);
        if (!WandHelper.isWandFullyCharged(scepter, player)) {
            double reach = WandHelper.getNodeReach(player);
            boolean hasNode;
            if (world.isRemote) {
                hasNode = WandHelper.findNodeAlongLook(player, reach) != null
                        || WandHelper.findAuraNodeEntityAlongLook(player, reach) != null;
            } else {
                hasNode = WandHelper.findNodeAlongLook(player, reach) != null
                        || WandHelper.findAuraNodeEntityAlongLook(player, reach) != null
                        || WandHelper.findNodeInCone(player, reach, 0.35D) != null
                        || WandHelper.findAuraNodeEntityInCone(player, reach, 0.35D) != null;
            }
            if (hasNode) {
                player.setActiveHand(hand);
                return new ActionResult<>(EnumActionResult.SUCCESS, scepter);
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
        if (!(living instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) living;
        if (player.world.isRemote) {
            return;
        }
        if (player.ticksExisted % 5 != 0) return;

        EntityAuraNode node = WandHelper.findNodeAlongLook(player, WandHelper.getNodeReach(player));
        AuraNodeEntity auraNode = null;
        if (node == null) {
            auraNode = WandHelper.findAuraNodeEntityAlongLook(player, WandHelper.getNodeReach(player));
            if (auraNode == null) {
                node = WandHelper.findNodeInCone(player, WandHelper.getNodeReach(player), 0.35D);
                if (node == null) {
                    auraNode = WandHelper.findAuraNodeEntityInCone(player, WandHelper.getNodeReach(player), 0.35D);
                    if (auraNode == null) return;
                }
            }
        }

        EnumHand activeHand = player.getActiveHand();
        ItemStack held = activeHand == null ? stack : player.getHeldItem(activeHand);
        if (held.isEmpty() || held.getItem() != this) {
            held = stack;
        }
        if (WandHelper.isWandFullyCharged(held, player)) {
            return;
        }

        if (node != null) {
            WandHelper.chargeWandFromNode(held, node, player);
        } else {
            WandHelper.chargeWandFromAuraNodeEntity(held, auraNode, player);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) return true;
        return oldStack.getItem() != newStack.getItem();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        AspectList primalCharge = WandHelper.getPrimalCharge(stack);
        int maxPerAspect = getMaxCharge(stack, null);
        tooltip.add(TextFormatting.YELLOW + "Vis Capacity: " + maxPerAspect);
        Aspect[] ordered = new Aspect[]{Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY};
        StringBuilder chargeLine = new StringBuilder();
        for (int i = 0; i < ordered.length; i++) {
            Aspect primal = ordered[i];
            int amount = primalCharge.getAmount(primal);
            chargeLine.append(LocalizationHelper.getTextColorFromAspect(primal)).append(amount);
            if (i < ordered.length - 1) {
                chargeLine.append(TextFormatting.GRAY).append(" | ");
            }
        }
        tooltip.add(chargeLine.toString());

        if (stack.hasTagCompound()) {
            String text = "";

            tooltip.add(TextFormatting.DARK_PURPLE + LocalizationHelper.localize("tc.vis.cost") + " " + (int) (WandHelper.getTotalDiscount(stack, null) * 100F) + "%");
            if (getCap(stack).getAspectDiscount().size() > 0) {
                tooltip.add(TextFormatting.DARK_AQUA + LocalizationHelper.localize("tw.crystal.discount"));
                for (Aspect a : getCap(stack).getAspectDiscount().getAspects())
                    tooltip.add(LocalizationHelper.getTextColorFromAspect(a) + a.getName() + ": " + getCap(stack).getAspectDiscount().getAmount(a));
            }
        }
    }

    @Override
    public IWandCap getCap(ItemStack stack) {
        if (stack.hasTagCompound()) {
            String s = stack.getTagCompound().getString("cap");
            IWandCap cap = ThaumicWandsAPI.getWandCap(s);
            return cap != null ? cap : TW_Wands.capIron;
        }
        return TW_Wands.capIron;
    }

    @Override
    public IWandRod getRod(ItemStack stack) {
        if (stack.hasTagCompound()) {
            String s = stack.getTagCompound().getString("rod");
            IWandRod rod = ThaumicWandsAPI.getWandRod(s);
            return rod != null ? rod : TW_Wands.rodWood;
        }
        return TW_Wands.rodWood;
    }

    public static ItemStack fromParts(IWandRod rod, IWandCap cap) {
        ItemStack is = new ItemStack(TW_Items.itemScepter);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("cap", cap.getTag());
        nbt.setString("rod", rod.getTag());
        is.setTagCompound(nbt);
        return is;
    }

    @Override
    public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
        return getRod(stack).getCapacity() * 2;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack arg0, EntityLivingBase arg1) {
        return EnumChargeDisplay.NORMAL;
    }
}
