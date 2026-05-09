package com.koteuka404.thaumicforever.node;

import com.koteuka404.thaumicforever.entity.EntityAuraNode;
import com.koteuka404.thaumicforever.node.type.NTDark;
import com.koteuka404.thaumicforever.node.type.NTHungry;
import com.koteuka404.thaumicforever.node.type.NTNormal;
import com.koteuka404.thaumicforever.node.type.NTPure;
import com.koteuka404.thaumicforever.node.type.NTTaint;
import com.koteuka404.thaumicforever.node.type.NTUnstable;


public abstract class NodeType {
    public int id;
    public static NodeType[] nodeTypes = new NodeType[6];

    public NodeType(int id) {
        this.id = id;
    }

    public abstract void performTickEvent(EntityAuraNode var1);

    public abstract void performPeriodicEvent(EntityAuraNode var1);

    public abstract int calculateStrength(EntityAuraNode var1);

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
