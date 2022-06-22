package org.spoorn.spoornweaponattributes.mixin;

import static org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Shadow @Final private Property levelCost;

    @Shadow private int repairItemUsage;

    @Inject(method = "onTakeOutput", at = @At(value = "TAIL"))
    private void reroll(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (stack.hasNbt()) {
            NbtCompound root = stack.getNbt();
            if (root.getBoolean(REROLL_NBT_KEY)) {
                SpoornWeaponAttributesUtil.rollAttributes(root);
                root.remove(REROLL_NBT_KEY);
            } else if (root.getBoolean(UPGRADE_NBT_KEY)) {
                SpoornWeaponAttributesUtil.upgradeAttributes(root);
                root.remove(UPGRADE_NBT_KEY);
            }
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
            root.remove(NBT_KEY);
            root.putBoolean(REROLL_NBT_KEY, true);

            this.levelCost.set(1);
            this.repairItemUsage = 1;
            accessor.getOutput().setStack(0, output);
            ((ScreenHandlerAccessor) this).trySendContentUpdates();
        } else {
            swaStack = canUpgrade(input1, input2);
            if (swaStack != null) {
                ItemStack output = swaStack.copy();
                NbtCompound root = output.getNbt();

                root.putBoolean(UPGRADE_NBT_KEY, true);

                this.levelCost.set(1);
                this.repairItemUsage = 1;
                accessor.getOutput().setStack(0, output);
                ((ScreenHandlerAccessor) this).trySendContentUpdates();
            }
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
