package org.spoorn.spoornweaponattributes.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornweaponattributes.client.SpoornWeaponAttributesClient;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /**
     * Copy of Fabric API's ItemStackMixin for ItemTooltipCallback
     */
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void getTooltip(PlayerEntity entity, TooltipContext tooltipContext, CallbackInfoReturnable<List<Text>> info) {
        SpoornWeaponAttributesClient.registerTooltipCallback().getTooltip((ItemStack) (Object) this, tooltipContext, info.getReturnValue());
    }
}
