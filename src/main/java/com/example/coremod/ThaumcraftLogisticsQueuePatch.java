package com.example.coremod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.network.PacketCancelLogisticsRequest;
import com.koteuka404.thaumicforever.ThaumicForever;

public final class ThaumcraftLogisticsQueuePatch {

    private static final List<QueueEntry> QUEUE = new ArrayList<QueueEntry>();
    private static final long ENTRY_LIFETIME_MS = 600000L;

    private ThaumcraftLogisticsQueuePatch() {}

    private static final class QueueEntry {
        ItemStack stack;
        long expireAt;
        int lastInventoryCount;
    }

    public static void onAction(Object gui, GuiButton button) {
        if (gui == null || button == null || button.id != 7) {
            return;
        }

        try {
            int selectedSlot = getIntField(gui, "selectedSlot", -1);
            if (selectedSlot < 0) {
                return;
            }

            Container container = (Container) getFieldAny(gui, "field_147002_h", "inventorySlots");
            if (container == null || selectedSlot >= container.inventorySlots.size()) {
                return;
            }

            Slot slot = container.getSlot(selectedSlot);
            if (slot == null || !slot.getHasStack()) {
                return;
            }

            int count = Math.max(1, getIntField(gui, "stackSize", 1));
            ItemStack stack = slot.getStack().copy();
            stack.setCount(count);
            add(stack);
        } catch (Throwable ignored) {
        }
    }

    public static void render(Object gui, int mouseX, int mouseY) {
        if (gui == null) {
            return;
        }

        try {
            syncInventoryDeltas();
            cleanup();
            if (QUEUE.isEmpty()) {
                return;
            }

            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;
            RenderItem renderItem = mc.getRenderItem();
            int guiLeft = getIntFieldAny(gui, 0, "field_147003_i", "guiLeft");
            int guiTop = getIntFieldAny(gui, 0, "field_147009_r", "guiTop");
            int x = Math.max(6, guiLeft - 132);
            int y = Math.max(18, guiTop + 18);
            int rows = Math.min(QUEUE.size(), 9);

            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawRect(x - 4, y - 15, x + 128, y + rows * 20 + 3, 0x66000000);
            font.drawString("Queue", x, y - 12, 0xC8D8FF);
            RenderHelper.enableGUIStandardItemLighting();
            for (int i = 0; i < rows; i++) {
                QueueEntry entry = QUEUE.get(i);
                int iy = y + i * 20;
                if (isInside(mouseX, mouseY, x - 2, iy - 1, 124, 19)) {
                    Gui.drawRect(x - 3, iy - 2, x + 126, iy + 18, 0x33FFFFFF);
                }
                renderItem.renderItemAndEffectIntoGUI(entry.stack, x, iy);
                renderItem.renderItemOverlayIntoGUI(font, entry.stack, x, iy, null);
                font.drawString("x" + entry.stack.getCount(), x + 20, iy + 1, 0xFFFFFF);
                font.drawString(shortName(entry.stack), x + 20, iy + 10, 0xC8D8FF);
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        } catch (Throwable ignored) {
        }
    }

    public static boolean onMouseClicked(Object gui, int mouseX, int mouseY, int mouseButton) {
        if (gui == null || mouseButton != 0) {
            return false;
        }

        try {
            syncInventoryDeltas();
            cleanup();
            if (QUEUE.isEmpty()) {
                return false;
            }

            int guiLeft = getIntFieldAny(gui, 0, "field_147003_i", "guiLeft");
            int guiTop = getIntFieldAny(gui, 0, "field_147009_r", "guiTop");
            int x = Math.max(6, guiLeft - 132);
            int y = Math.max(18, guiTop + 18);
            int rows = Math.min(QUEUE.size(), 9);
            for (int i = 0; i < rows; i++) {
                int iy = y + i * 20;
                if (!isInside(mouseX, mouseY, x - 2, iy - 1, 124, 19)) {
                    continue;
                }

                QueueEntry entry = QUEUE.remove(i);
                if (entry != null && entry.stack != null && !entry.stack.isEmpty() && ThaumicForever.network != null) {
                    ThaumicForever.network.sendToServer(new PacketCancelLogisticsRequest(entry.stack.copy()));
                }
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static void add(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        syncInventoryDeltas();
        cleanup();
        long expireAt = System.currentTimeMillis() + ENTRY_LIFETIME_MS;
        int currentInventoryCount = countInPlayerInventory(stack);
        for (QueueEntry entry : QUEUE) {
            if (sameItem(entry.stack, stack)) {
                entry.stack.grow(stack.getCount());
                entry.expireAt = expireAt;
                entry.lastInventoryCount = currentInventoryCount;
                return;
            }
        }

        QueueEntry entry = new QueueEntry();
        entry.stack = stack.copy();
        entry.expireAt = expireAt;
        entry.lastInventoryCount = currentInventoryCount;
        QUEUE.add(entry);
    }

    private static void syncInventoryDeltas() {
        if (QUEUE.isEmpty()) {
            return;
        }

        Iterator<QueueEntry> it = QUEUE.iterator();
        while (it.hasNext()) {
            QueueEntry entry = it.next();
            if (entry == null || entry.stack == null || entry.stack.isEmpty()) {
                it.remove();
                continue;
            }

            int current = countInPlayerInventory(entry.stack);
            int delivered = current - entry.lastInventoryCount;
            if (delivered > 0) {
                entry.stack.shrink(delivered);
                if (entry.stack.isEmpty()) {
                    it.remove();
                    continue;
                }
            }
            entry.lastInventoryCount = current;
        }
    }

    private static void cleanup() {
        long now = System.currentTimeMillis();
        Iterator<QueueEntry> it = QUEUE.iterator();
        while (it.hasNext()) {
            QueueEntry entry = it.next();
            if (entry == null || entry.stack == null || entry.stack.isEmpty() || entry.expireAt < now) {
                it.remove();
            }
        }
    }

    private static boolean sameItem(ItemStack first, ItemStack second) {
        return first != null
            && second != null
            && !first.isEmpty()
            && !second.isEmpty()
            && ItemStack.areItemsEqual(first, second)
            && ItemStack.areItemStackTagsEqual(first, second);
    }

    private static int countInPlayerInventory(ItemStack template) {
        if (template == null || template.isEmpty()) {
            return 0;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc == null ? null : mc.player;
        if (player == null || player.inventory == null) {
            return 0;
        }

        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (sameItem(stack, template)) {
                count += stack.getCount();
            }
        }
        for (ItemStack stack : player.inventory.offHandInventory) {
            if (sameItem(stack, template)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static String shortName(ItemStack stack) {
        String name = stack.getDisplayName();
        if (name == null) {
            return "";
        }
        return name.length() > 18 ? name.substring(0, 18) + "." : name;
    }

    private static boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static Object getField(Object obj, String name) throws Exception {
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(obj);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static Object getFieldAny(Object obj, String... names) throws Exception {
        NoSuchFieldException last = null;
        for (String name : names) {
            try {
                return getField(obj, name);
            } catch (NoSuchFieldException e) {
                last = e;
            }
        }
        throw last != null ? last : new NoSuchFieldException();
    }

    private static int getIntField(Object obj, String name, int def) {
        try {
            Object value = getField(obj, name);
            return value instanceof Integer ? (Integer) value : def;
        } catch (Throwable ignored) {
            return def;
        }
    }

    private static int getIntFieldAny(Object obj, int def, String... names) {
        for (String name : names) {
            try {
                Object value = getField(obj, name);
                if (value instanceof Integer) {
                    return (Integer) value;
                }
            } catch (Throwable ignored) {
            }
        }
        return def;
    }
}
