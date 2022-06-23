package org.spoorn.spoornweaponattributes.forge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spoorn.spoornweaponattributes.client.SpoornWeaponAttributesClient;

public class SpoornWeaponAttributesClientForge {

    public static void init() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        SpoornWeaponAttributesClient.init();
    }
}
