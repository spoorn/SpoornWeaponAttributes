package org.spoorn.spoornweaponattributes.fabric;

import net.fabricmc.api.ModInitializer;
import org.spoorn.spoornweaponattributes.SpoornWeaponAttributes;

public class SpoornWeaponAttributesFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        SpoornWeaponAttributes.init();
    }
}
