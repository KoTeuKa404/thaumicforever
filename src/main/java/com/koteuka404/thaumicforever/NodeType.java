package com.koteuka404.thaumicforever;

public abstract class NodeType {
    int id;
    public static NodeType[] nodeTypes = new NodeType[6];

    public NodeType(int id) {
        this.id = id;
    }

    abstract void performTickEvent(EntityAuraNode var1);

    abstract void performPeriodicEvent(EntityAuraNode var1);

    abstract int calculateStrength(EntityAuraNode var1);

    static {
        NodeType.nodeTypes[0] = new NTNormal(0);
        NodeType.nodeTypes[1] = new NTDark(1);
        NodeType.nodeTypes[2] = new NTHungry(2);
        NodeType.nodeTypes[3] = new NTPure(3);
        NodeType.nodeTypes[4] = new NTTaint(4);
        NodeType.nodeTypes[5] = new NTUnstable(5);
        // NodeType.nodeTypes[5] = new NTEnergy(7);
        // NodeType.nodeTypes[6] = new NTPrimal(6);
    }
}
