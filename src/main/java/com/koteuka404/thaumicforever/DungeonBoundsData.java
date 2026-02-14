package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class DungeonBoundsData extends WorldSavedData {
    public static final String DATA_NAME = "tf_maze_bounds";

    private final List<BoundingBox> boxes = new ArrayList<>();

    public DungeonBoundsData() { super(DATA_NAME); }
    public DungeonBoundsData(String name) { super(name); }

    public static DungeonBoundsData get(World world) {
        if (world == null) return null;
        MapStorage storage = world.getMapStorage();
        if (storage == null) return null;
        DungeonBoundsData data = (DungeonBoundsData) storage.getOrLoadData(DungeonBoundsData.class, DATA_NAME);
        if (data == null) {
            data = new DungeonBoundsData();
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    public List<BoundingBox> getBoxes() {
        return new ArrayList<>(boxes);
    }

    public void addBox(BoundingBox b) {
        if (!boxes.contains(b)) {
            boxes.add(b);
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        boxes.clear();
        NBTTagList list = nbt.getTagList("boxes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            BoundingBox b = new BoundingBox(
                new net.minecraft.util.math.BlockPos(c.getInteger("sx"), c.getInteger("sy"), c.getInteger("sz")),
                new net.minecraft.util.math.BlockPos(c.getInteger("ex"), c.getInteger("ey"), c.getInteger("ez"))
            );
            boxes.add(b);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (BoundingBox b : boxes) {
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("sx", b.getMinX());
            c.setInteger("sy", b.getMinY());
            c.setInteger("sz", b.getMinZ());
            c.setInteger("ex", b.getMaxX());
            c.setInteger("ey", b.getMaxY());
            c.setInteger("ez", b.getMaxZ());
            list.appendTag(c);
        }
        nbt.setTag("boxes", list);
        return nbt;
    }
    
}
