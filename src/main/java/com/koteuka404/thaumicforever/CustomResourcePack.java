package com.koteuka404.thaumicforever;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.util.ResourceLocation;

public class CustomResourcePack extends AbstractResourcePack {

    public CustomResourcePack(File resourcePackFile) {
        super(resourcePackFile);
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException {
        if (name.equals("assets/tg/models/block/gemcutter.json")) {
            return new FileInputStream(new File("src/main/resources/assets/tg/models/block/gemcutter.json"));
        }
        return null;
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        return location.getResourceDomain().equals("tg") && location.getResourcePath().equals("models/block/gemcutter.json");
    }

    @Override
    public Set<String> getResourceDomains() {
        return Collections.singleton("tg");
    }

    @Override
    protected boolean hasResourceName(String name) {
        return name.equals("assets/tg/models/block/gemcutter.json");
    }
}
