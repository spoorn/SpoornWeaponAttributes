package org.spoorn.spoornweaponattributes.mixin;

import static org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.spoornweaponattributes.att.Attribute;
import org.spoorn.spoornweaponattributes.config.Expressions;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

import java.util.Map.Entry;

@Mixin(Item.class)
public class ItemMixin {

    // For compatibility
    private static final String OLD_NBT_KEY = "spoornWeaponAttributes";

    /**
     * Generates the NBT data for our mod on an item.
     */
    @Inject(method = "inventoryTick", at = @At(value = "HEAD"))
    public void addCustomNbt(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!world.isClient() && SpoornWeaponAttributesUtil.shouldTryGenAttr(stack)) {
            NbtCompound root = stack.getOrCreateTag();
            
            if (root.contains(OLD_NBT_KEY)) {
                handleMigration(root);
            }
            
            SpoornWeaponAttributesUtil.rollAttributes(root);
        }
    }
    
    // Handles migration from old system to new 3.x+ system with durations as expressions for backwards compatibility
    private void handleMigration(NbtCompound root) {
        NbtCompound oldNbt = root.getCompound(OLD_NBT_KEY);
        NbtCompound newNbt = SpoornWeaponAttributesUtil.createAttributesSubNbt(root);

        for (Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
            String name = entry.getKey();
            
            if (oldNbt.contains(name)) {
                NbtCompound oldInner = oldNbt.getCompound(name).copy();

                float bonusDamage;
                float duration;
                switch (name) {
                    case Attribute.FIRE_NAME:
                        bonusDamage = oldInner.getFloat(BONUS_DAMAGE);
                        duration = (float) Expressions.fireDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
                        oldInner.putFloat(DURATION, duration);
                        break;
                    case Attribute.COLD_NAME:
                        bonusDamage = oldInner.getFloat(BONUS_DAMAGE);
                        float slowDuration = (float) Expressions.slowDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
                        oldInner.putFloat(SLOW_DURATION, slowDuration);
                        break;
                    case Attribute.POISON_NAME:
                        bonusDamage = oldInner.getFloat(BONUS_DAMAGE);
                        duration = (float) Expressions.poisonDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
                        oldInner.putFloat(DURATION, duration);
                        break;
                    default:
                        // do nothing
                }

                newNbt.put(name, oldInner);
            }
        }
    }
}
