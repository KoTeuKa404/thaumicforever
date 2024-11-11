package com.koteuka404.thaumicforever;

import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.casters.FocusMod;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.Trajectory;

public class FocusModBoostPower extends FocusMod {

    // Максимальна кількість разів, коли можна додати модифікатор
    private static final int MAX_USES = 2;

    @Override
    public String getResearch() {
        return "FOCUSSPLIT"; // Дослідження для доступу до цього фокуса
    }

    @Override
    public String getKey() {
        return "thaumicforever.BOOSTPOWER"; // Унікальний ключ для цього модифікатора
    }

    @Override
    public int getComplexity() {
        return 10; // Вартість у 10 одиниць складності
    }

    @Override
    public float getPowerMultiplier() {
        return 2.0f; // Підвищення сили заклинання в 2 рази
    }

    @Override
    public EnumSupplyType[] mustBeSupplied() {
        // Цей модифікатор потребує цілей (TARGET) для подальшого застосування заклинань
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public EnumSupplyType[] willSupply() {
        // Модифікатор постачає тип цілей (TARGET) для подальших заклинань
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public RayTraceResult[] supplyTargets() {
        // Повертаємо цілі з батьківського фокуса (одна ціль)
        return getParent().supplyTargets(); // Лише одна ціль
    }

    @Override
    public Trajectory[] supplyTrajectories() {
        // Повертаємо траєкторії з батьківського фокуса, якщо потрібно (для одного заклинання)
        return getParent().supplyTrajectories(); // Лише одна траєкторія
    }

    @Override
    public boolean execute() {
        // Метод execute необхідний для реалізації модифікатора
        return true;
    }

    public boolean canApply(FocusPackage focusPackage) {
        // Лічильник використання цього модифікатора
        int uses = 0;

        // Спробуємо звернутися до поля nodes, якщо таке існує
        if (focusPackage.nodes != null) {
            for (IFocusElement element : focusPackage.nodes) { // Якщо focusPackage має поле nodes
                if (element instanceof FocusModBoostPower) {
                    uses++;
                }
            }
        }

        // Дозволяємо додати модифікатор, якщо його ще не більше двох разів
        return uses < MAX_USES;
    }
}
