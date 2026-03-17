package ruiseki.okbackpack.compat;

import java.util.function.Supplier;

import net.minecraft.launchwrapper.Launch;

import cpw.mods.fml.common.Loader;

public enum Mods {

    // spotless:off
    Baubles("Baubles"),
    BaublesExpanded("Baubles|Expanded"),
    NotEnoughItems("NotEnoughItems"),
    EtFuturum("etfuturum"),
    TConstruct("TConstruct"),
    ;
    // spotless:on

    public final String modid;
    private final Supplier<Boolean> supplier;
    private Boolean loaded;

    public static final boolean DEV_ENV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    Mods(String modid) {
        this.modid = modid;
        this.supplier = null;
    }

    Mods(Supplier<Boolean> supplier) {
        this.supplier = supplier;
        this.modid = null;
    }

    public boolean isLoaded() {
        if (loaded == null) {
            if (supplier != null) {
                loaded = supplier.get();
            } else if (modid != null) {
                loaded = Loader.isModLoaded(modid);
            } else loaded = false;
        }
        return loaded;
    }
}
