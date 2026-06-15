package com.koteuka404.thaumicforever.item;

import java.util.List;
import java.util.Locale;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.koteuka404.thaumicforever.ThaumicForever;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;

@Mod.EventBusSubscriber(modid = ThaumicForever.MODID)
public class ItemVisRing extends Item implements IBauble {

    public static final ItemVisRing INSTANCE = new ItemVisRing();

    private static final String NBT_STORED_VIS = "StoredVis";
    private static final float MAX_STORED_VIS = 35.0f;
    private static final float TRANSFER_PER_SECOND = 0.5f;
    private static final float HIGH_AURA_RATIO = 0.85f;
    private static final float LOW_AURA_RATIO = 0.35f;
    private static final float EPS = 0.0001f;

    public ItemVisRing() {
        setUnlocalizedName("ring_vis");
        setRegistryName("ring_vis");
        setCreativeTab(ThaumicForever.CREATIVE_TAB);
        setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase wearer) {
        if (wearer == null || wearer.world == null || wearer.world.isRemote) {
            return;
        }
        if (wearer.world.getTotalWorldTime() % 20L != 0L) {
            return;
        }

        tryBalanceAura(stack, wearer.world, wearer.getPosition());
    }

    private static void tryBalanceAura(ItemStack stack, World world, net.minecraft.util.math.BlockPos pos) {
        if (stack == null || stack.isEmpty() || world == null || pos == null) {
            return;
        }

        float base = Math.max(0.0f, (float) AuraHelper.getAuraBase(world, pos));
        if (base <= EPS) {
            return;
        }

        float vis = AuraHelper.getVis(world, pos);
        float stored = getStoredVis(stack);

        float highMark = base * HIGH_AURA_RATIO;
        if (vis > highMark + EPS && stored < MAX_STORED_VIS - EPS) {
            float take = Math.min(TRANSFER_PER_SECOND, vis - highMark);
            take = Math.min(take, MAX_STORED_VIS - stored);
            if (take <= EPS) {
                return;
            }

            float drained = AuraHelper.drainVis(world, pos, take, false);
            if (drained > EPS) {
                setStoredVis(stack, stored + drained);
            }
            return;
        }

        float lowMark = base * LOW_AURA_RATIO;
        if (vis < lowMark - EPS && stored > EPS) {
            float give = Math.min(TRANSFER_PER_SECOND, lowMark - vis);
            give = Math.min(give, stored);
            if (give <= EPS) {
                return;
            }

            AuraHelper.addVis(world, pos, give);
            setStoredVis(stack, stored - give);
        }
    }

    public static float getStoredVis(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return 0.0f;
        }
        return clamp(stack.getTagCompound().getFloat(NBT_STORED_VIS), 0.0f, MAX_STORED_VIS);
    }

    private static void setStoredVis(ItemStack stack, float amount) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setFloat(NBT_STORED_VIS, clamp(amount, 0.0f, MAX_STORED_VIS));
    }

    private static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.AQUA + "Stored vis: " + formatVis(getStoredVis(stack)) + " / " + formatVis(MAX_STORED_VIS));
        tooltip.add(TextFormatting.GRAY + "Stores excess aura and releases it when the chunk is low.");
    }

    private static String formatVis(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getStoredVis(stack) > EPS;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0d - (getStoredVis(stack) / MAX_STORED_VIS);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0x66ccff;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Vis Ring";
    }

    @Override public void onEquipped(ItemStack stack, EntityLivingBase player) { }
    @Override public void onUnequipped(ItemStack stack, EntityLivingBase player) { }
    @Override public boolean canEquip(ItemStack stack, EntityLivingBase player) { return true; }
    @Override public boolean canUnequip(ItemStack stack, EntityLivingBase player) { return true; }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
            INSTANCE,
            0,
            new ModelResourceLocation(INSTANCE.getRegistryName(), "inventory")
        );
    }
}
