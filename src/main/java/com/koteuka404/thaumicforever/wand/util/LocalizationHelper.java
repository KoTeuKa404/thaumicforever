package com.koteuka404.thaumicforever.wand.util;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.aspects.Aspect;

public class LocalizationHelper {

    public static String localize(String name) {
        return new TextComponentTranslation(name, new Object[0]).getFormattedText();
    }

    public static TextFormatting getTextColorFromAspect(Aspect a) {
        if (a == null) return TextFormatting.RESET;
        String color = a.getChatcolor();
        if (color == null) return TextFormatting.RESET;
        switch (color) {
            case "0":
                return TextFormatting.BLACK;
            case "1":
                return TextFormatting.DARK_BLUE;
            case "2":
                return TextFormatting.DARK_GREEN;
            case "3":
                return TextFormatting.DARK_AQUA;
            case "4":
                return TextFormatting.DARK_RED;
            case "5":
                return TextFormatting.DARK_PURPLE;
            case "6":
                return TextFormatting.GOLD;
            case "7":
                return TextFormatting.GRAY;
            case "8":
                return TextFormatting.DARK_GRAY;
            case "9":
                return TextFormatting.BLUE;
            case "a":
                return TextFormatting.GREEN;
            case "b":
                return TextFormatting.AQUA;
            case "c":
                return TextFormatting.RED;
            case "d":
                return TextFormatting.LIGHT_PURPLE;
            case "e":
                return TextFormatting.YELLOW;
            case "f":
                return TextFormatting.WHITE;
        }

        return TextFormatting.RESET;
    }

}
