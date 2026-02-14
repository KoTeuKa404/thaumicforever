package com.koteuka404.thaumicforever;

public enum Primal {
    IGNIS(0), TERRA(1), AER(2), AQUA(3), ORDO(4), PERDITIO(5);

    public final int id;

    Primal(int id) {
        this.id = id;
    }

    public static final int COUNT = 6;
}
