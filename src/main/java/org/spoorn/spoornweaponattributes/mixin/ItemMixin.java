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
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.config.attribute.*;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

import java.util.Map.Entry;

@Mixin(Item.class)
public class ItemMixin {

    // For compatibility
    private static final String SPOORN_LOOT_NBT_KEY = "spoornConfig";
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
            
            if (!root.contains(SpoornWeaponAttributesUtil.NBT_KEY) && !root.contains(SPOORN_LOOT_NBT_KEY)) {
                NbtCompound nbt = SpoornWeaponAttributesUtil.createAttributesSubNbt(root);
                //System.out.println("Initial Nbt: " + nbt);

                for (Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
                    String name = entry.getKey();
                    Attribute att = entry.getValue();

                    if (SpoornWeaponAttributesUtil.shouldEnable(att.chance)) {
                        NbtCompound newNbt = new NbtCompound();
                        switch (name) {
                            case Attribute.CRIT_NAME:
                                handleCrit(newNbt);
                                break;
                            case Attribute.FIRE_NAME:
                                handleFire(newNbt);
                                break;
                            case Attribute.COLD_NAME:
                                handleCold(newNbt);
                                break;
                            case Attribute.LIGHTNING_NAME:
                                handleLightning(newNbt);
                                break;
                            case Attribute.POISON_NAME:
                                handlePoison(newNbt);
                                break;
                            case Attribute.LIFESTEAL_NAME:
                                handleLifesteal(newNbt);
                                break;
                            case Attribute.EXPLOSIVE_NAME:
                                handleExplosive(newNbt);
                                break;
                            default:
                                // do nothing
                        }
                        nbt.put(name, newNbt);
                    }
                }

                //System.out.println("Updated Nbt: " + nbt);
            }
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

    /**
     * We manually list all the handles here for optimal latency
     */

    private void handleCrit(NbtCompound nbt) {
        CritConfig config = ModConfig.get().critConfig;
        float critChance = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minCritChance, config.maxCritChance);
        if (config.useGaussian) {
            critChance /= 100;
        }
        nbt.putFloat(CRIT_CHANCE, critChance);
    }

    private void handleFire(NbtCompound nbt) {
        FireConfig config = ModConfig.get().fireConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
        float duration = (float) Expressions.fireDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
        nbt.putFloat(DURATION, duration);
    }

    private void handleCold(NbtCompound nbt) {
        ColdConfig config = ModConfig.get().coldConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
        float slowDuration = (float) Expressions.slowDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
        nbt.putFloat(SLOW_DURATION, slowDuration);
    }

    private void handleLightning(NbtCompound nbt) {
        LightningConfig config = ModConfig.get().lightningConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
    }

    private void handlePoison(NbtCompound nbt) {
        PoisonConfig config = ModConfig.get().poisonConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
        float duration = (float) Expressions.poisonDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
        nbt.putFloat(DURATION, duration);
    }

    private void handleLifesteal(NbtCompound nbt) {
        LifestealConfig config = ModConfig.get().lifestealConfig;
        float lifesteal = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minLifesteal, config. maxLifesteal);
        nbt.putFloat(LIFESTEAL, lifesteal);
    }

    private void handleExplosive(NbtCompound nbt) {
        ExplosiveConfig config = ModConfig.get().explosiveConfig;
        nbt.putFloat(EXPLOSION_CHANCE, (float) config.explosionChance);
    }
}
