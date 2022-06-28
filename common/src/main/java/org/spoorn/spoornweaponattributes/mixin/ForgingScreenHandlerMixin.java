package org.spoorn.spoornweaponattributes.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

/**
 * Note: If all this code starts running into problems, we can migrate to using inventoryTicks, like we did with rerolls
 * in AnvilScreenHandlerMixin - by removing the NBT_KEY and triggering rerolls/upgrades during the inventoryTick.
 */
@Mixin(ForgingScreenHandler.class)
public class ForgingScreenHandlerMixin {
    
    // This is to save the ItemStack during each call to transferSlot to be used by our 2 mixins.  Use a map keyed by
    // the player Id in case this someday needs to be thread safe
    private static ItemStack originalTransferSlotItemStack;
    
    @Redirect(method = "transferSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"))
    private ItemStack saveSlotItemStack(ItemStack instance) {
        ItemStack res = instance.copy();
        if ((Object)this instanceof AnvilScreenHandler) {
            originalTransferSlotItemStack = instance;
        }
        return res;
    }

    /**
     * This is a backup method on top of {@link AnvilScreenHandlerMixin}.  When the user Shift+Clicks on the output item
     * instead of a simple Left Click, slot.onTakeItem() will be called with an empty ItemStack which causes the code in
     * {@link AnvilScreenHandlerMixin} to not properly apply the attribute logic.  Instead, this transferSlot() is called here.
     */
    @Inject(method = "transferSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ForgingScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z", ordinal = 0))
    private void test(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if ((Object)this instanceof AnvilScreenHandler && originalTransferSlotItemStack != null) {
            ItemStack output = originalTransferSlotItemStack;
            // index == 2 when transferring from output to player inventory
            if (index == 2 && player instanceof ServerPlayerEntity && output.hasNbt()) {
                NbtCompound root = output.getNbt();
                SpoornWeaponAttributesUtil.rollOrUpgradeNbt(root);
            }

            originalTransferSlotItemStack = null;
        }
    }
}
