package org.spoorn.spoornweaponattributes;

import lombok.extern.log4j.Log4j2;
import org.spoorn.spoornweaponattributes.att.Attribute;
import org.spoorn.spoornweaponattributes.config.ModConfig;

@Log4j2
public class SpoornWeaponAttributes {

    public static final String MODID = "spoornweaponattributes";
    
    public static void init() {
        log.info("Hello from SpoornWeaponAttributes!");

        // Config
        ModConfig.init();

        // Attribute registry
        Attribute.init();
    }
}
