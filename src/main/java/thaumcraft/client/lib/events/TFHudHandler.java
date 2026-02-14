package thaumcraft.client.lib.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.koteuka404.thaumicforever.wand.api.item.wand.IWand;

public class TFHudHandler extends HudHandler {

    public static void install() {
        HudHandler old = RenderEventHandler.hudHandler;
        TFHudHandler replacement = new TFHudHandler();
        if (old != null) {
            replacement.knowledgeGainTrackers = old.knowledgeGainTrackers;
            replacement.kgFade = old.kgFade;
            replacement.nextsync = old.nextsync;
        }
        RenderEventHandler.hudHandler = replacement;
    }

    @Override
    void renderCastingWandHud(Minecraft mc, float pt, EntityPlayer player, long time, ItemStack stack, int shift) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof IWand) {
            // Custom wand HUD renders elsewhere; skip default vis capsule.
            return;
        }
        super.renderCastingWandHud(mc, pt, player, time, stack, shift);
    }
}
