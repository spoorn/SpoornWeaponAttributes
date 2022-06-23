package org.spoorn.spoornweaponattributes.fabric.client;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spoorn.spoornweaponattributes.client.SpoornWeaponAttributesClient;

@Log4j2
@Environment(EnvType.CLIENT)
public class SpoornWeaponAttributesClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SpoornWeaponAttributesClient.init();
    }
}
