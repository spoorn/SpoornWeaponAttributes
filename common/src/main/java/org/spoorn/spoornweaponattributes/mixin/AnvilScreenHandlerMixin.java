package org.spoorn.spoornweaponattributes.mixin;

import static org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Shadow @Final private Property levelCost;

    @Shadow private int repairItemUsage;

    /**
     *
     * @param player
     * @param output ItemStack on the cursor.  Note: This will be "air" if user Shift+Clicks the output item!
     * @param ci
     */
    @Inject(method = "onTakeOutput", at = @At(value = "HEAD"))
    private void reroll(PlayerEntity player, ItemStack output, CallbackInfo ci) {
        ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
        Inventory inputInventory = accessor.getInput();
        ItemStack input1 = inputInventory.getStack(0);
        ItemStack input2 = inputInventory.getStack(1);

        // Apply on output item
        if (player instanceof ServerPlayerEntity) {
            if (output.hasNbt()) {
                NbtCompound root = output.getNbt();
                SpoornWeaponAttributesUtil.rollOrUpgradeNbt(root);
            }
        }

        // Put items in the correct order so vanilla code can subtract the stack count and remove item correctly
        // First slot should be the weapon, 2nd slot should be the upgrade item
        // This prevents the wrong order from deleting the entire stack of the upgrade item
        ItemStack weapon;
        if ((weapon = canUpgrade(input1, input2)) != null || (weapon = canReroll(input1, input2)) != null) {
            ItemStack temp = weapon == input1 ? input2 : input1;
            // Swap if in wrong order
            inputInventory.setStack(0, weapon);
            inputInventory.setStack(1, temp);
        }
    }

    @Inject(method = "updateResult", at = @At(value = "RETURN"))
    private void addRerolls(CallbackInfo ci) {
        ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
        Inventory inputInventory = accessor.getInput();
        ItemStack input1 = inputInventory.getStack(0);
        ItemStack input2 = inputInventory.getStack(1);

        ItemStack swaStack = canReroll(input1, input2);
        if (swaStack != null) {
            ItemStack output = swaStack.copy();
            NbtCompound root = output.getNbt();
            // This will cause a reroll no matter what.  We could do the same thing with Upgrading in the future if it's simpler than the mixin in ForgingScreenHandlerMixin
            root.remove(NBT_KEY);
            root.putBoolean(REROLL_NBT_KEY, true);

            this.levelCost.set(ModConfig.get().rerollLevelCost);
            this.repairItemUsage = 1;
            accessor.getOutput().setStack(0, output);
            ((ScreenHandlerAccessor) this).trySendContentUpdates();
        } else {
            swaStack = canUpgrade(input1, input2);
            if (swaStack != null) {
                ItemStack output = swaStack.copy();
                NbtCompound root = output.getNbt();

                root.putBoolean(UPGRADE_NBT_KEY, true);

                this.levelCost.set(ModConfig.get().upgradeLevelCost);
                this.repairItemUsage = 1;
                accessor.getOutput().setStack(0, output);
                ((ScreenHandlerAccessor) this).trySendContentUpdates();
            }
        }
    }

    @Inject(method = "canTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void allowNoLevelCost(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
        Inventory inputInventory = accessor.getInput();
        ItemStack input1 = inputInventory.getStack(0);
        ItemStack input2 = inputInventory.getStack(1);

        if ((canReroll(input1, input2) != null && ModConfig.get().rerollLevelCost <= 0)
                || (canUpgrade(input1, input2) != null && ModConfig.get().upgradeLevelCost <= 0)) {
            cir.setReturnValue(player.getAbilities().creativeMode || player.experienceLevel >= this.levelCost.get());
            cir.cancel();
        }
    }

    private ItemStack canReroll(ItemStack stack1, ItemStack stack2) {
        if (SpoornWeaponAttributesUtil.hasSWANbt(stack1) && SpoornWeaponAttributesUtil.isRerollItem(stack2)) {
            return stack1;
        } else if (SpoornWeaponAttributesUtil.hasSWANbt(stack2) && SpoornWeaponAttributesUtil.isRerollItem(stack1)) {
            return stack2;
        }
        return null;
    }

    private ItemStack canUpgrade(ItemStack stack1, ItemStack stack2) {
        if (SpoornWeaponAttributesUtil.shouldTryGenAttr(stack1) && SpoornWeaponAttributesUtil.isUpgradeItem(stack2)) {
            return stack1;
        } else if (SpoornWeaponAttributesUtil.shouldTryGenAttr(stack2) && SpoornWeaponAttributesUtil.isUpgradeItem(stack1)) {
            return stack2;
        }
        return null;
    }
}
