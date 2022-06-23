package org.spoorn.spoornweaponattributes.forge;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.spoorn.spoornweaponattributes.SpoornWeaponAttributes;
import org.spoorn.spoornweaponattributes.forge.client.SpoornWeaponAttributesClientForge;

@Mod(SpoornWeaponAttributes.MODID)
public class SpoornWeaponAttributesForge {

    public SpoornWeaponAttributesForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ClimbLaddersFast.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        SpoornWeaponAttributes.init();

        // Client
        SpoornWeaponAttributesClientForge.init();
    }
}
