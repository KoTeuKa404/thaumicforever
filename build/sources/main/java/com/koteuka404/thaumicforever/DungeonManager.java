package com.koteuka404.thaumicforever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DungeonManager {
    private static final DungeonManager INSTANCE = new DungeonManager();
    private final List<DungeonBounds> dungeonBounds = new ArrayList<>();

    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    public void addDungeon(DungeonBounds bounds) {
        dungeonBounds.add(bounds);
    }

    public List<DungeonBounds> getAllDungeons() {
        return Collections.unmodifiableList(dungeonBounds);
    }

    public void clear() {
        dungeonBounds.clear();
    }
}
