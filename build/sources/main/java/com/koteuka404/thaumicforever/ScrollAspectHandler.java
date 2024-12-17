package com.koteuka404.thaumicforever;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ScrollAspectHandler {

    /**
     * Метод для отримання аспектів зі скролу.
     * @param scrollItemStack ItemStack скролу.
     * @return AspectList з аспектами, або null, якщо аспекти не знайдено.
     */
    public static AspectList getAspectsFromScroll(ItemStack scrollItemStack) {
        if (scrollItemStack == null || scrollItemStack.isEmpty()) {
            System.out.println("Скрол порожній.");
            return null;
        }

        if (!scrollItemStack.hasTagCompound()) {
            System.out.println("Скрол не має тегів.");
            return null;
        }

        NBTTagCompound tag = scrollItemStack.getTagCompound();
        if (tag != null && tag.hasKey("aspects")) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(tag.getCompoundTag("aspects"));

            // Логування отриманих аспектів
            System.out.println("Отримані аспекти зі скролу:");
            for (Aspect aspect : aspects.getAspects()) {
                System.out.println(" - " + aspect.getName() + ": " + aspects.getAmount(aspect));
            }

            return aspects;
        } else {
            System.out.println("Скрол не містить аспектів.");
        }

        return null;
    }

    /**
     * Метод для збереження оновлених аспектів у скрол.
     * @param scrollItemStack ItemStack скролу.
     * @param aspects AspectList, який потрібно зберегти.
     */
    public static void saveAspectsToScroll(ItemStack scrollItemStack, AspectList aspects) {
        if (scrollItemStack == null || scrollItemStack.isEmpty() || aspects == null) {
            System.out.println("Неможливо зберегти аспекти, оскільки скрол або аспекти порожні.");
            return;
        }

        if (!scrollItemStack.hasTagCompound()) {
            scrollItemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = scrollItemStack.getTagCompound();
        NBTTagCompound aspectCompound = new NBTTagCompound();
        aspects.writeToNBT(aspectCompound);

        tag.setTag("aspects", aspectCompound);

        System.out.println("Аспекти успішно збережено у скрол:");
        for (Aspect aspect : aspects.getAspects()) {
            System.out.println(" - " + aspect.getName() + ": " + aspects.getAmount(aspect));
        }
    }
}
