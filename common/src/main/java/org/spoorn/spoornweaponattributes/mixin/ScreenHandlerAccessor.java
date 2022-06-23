package org.spoorn.spoornweaponattributes.mixin;

import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {
    
    @Invoker("sendContentUpdates")
    void trySendContentUpdates();
}
