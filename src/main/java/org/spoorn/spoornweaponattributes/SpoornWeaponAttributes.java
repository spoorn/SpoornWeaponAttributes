package org.spoorn.spoornweaponattributes;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import org.spoorn.spoornweaponattributes.config.ModConfig;

@Log4j2
public class SpoornWeaponAttributes implements ModInitializer {

    public static final String MODID = "SpoornWeaponAttributes";

    @Override
    public void onInitialize() {
        log.info("Hello from SpoornWeaponAttributes!");

        // Config
        ModConfig.init();

        // Attribute registry
        Attribute.init();
    }
}
