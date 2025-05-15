package com.example.coremod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.example.coremod"})
public class CoreModPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            "com.example.coremod.IUEventHandlerTransformer"




            

        };
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        System.out.println("[CoreModPlugin] data injected");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
