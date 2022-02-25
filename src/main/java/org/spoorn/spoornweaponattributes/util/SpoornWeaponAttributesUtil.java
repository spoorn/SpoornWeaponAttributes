package org.spoorn.spoornweaponattributes.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import org.spoorn.spoornweaponattributes.att.Attribute;
import org.spoorn.spoornweaponattributes.config.Expressions;
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.config.attribute.*;
import org.spoorn.spoornweaponattributes.entity.damage.SWAExplosionDamageSource;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * All generic utilities.
 */
public final class SpoornWeaponAttributesUtil {

    public static final String NBT_KEY = "swa3";
    public static final String REROLL_NBT_KEY = "swa3_reroll";
    public static final String BONUS_DAMAGE = "bonusDmg";
    public static final String DURATION = "dur";
    public static final String SLOW_DURATION = "slowDur";
    public static final String FREEZE_DURATION = "freezeDur";
    public static final String CRIT_CHANCE = "critChance";
    public static final String LIFESTEAL = "lifesteal";
    public static final String EXPLOSION_CHANCE = "explosionChance";
    private static final String EXPLOSION_DAMAGE_SOURCE_ID = "swa.explosion";
    public static final SWAExplosionDamageSource SWA_EXPLOSION_DAMAGE_SOURCE = new SWAExplosionDamageSource(EXPLOSION_DAMAGE_SOURCE_ID);
    public static final Random RANDOM = new Random();
    
    public static final String SPOORN_LOOT_NBT_KEY = "spoornConfig";

    public static boolean shouldTryGenAttr(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }

    public static NbtCompound createAttributesSubNbt(NbtCompound root) {
        NbtCompound res = new NbtCompound();
        root.put(NBT_KEY, res);
        return res;
    }

    public static Optional<NbtCompound> getSWANbtIfPresent(ItemStack stack) {
        if (stack.hasNbt()) {
            NbtCompound root = stack.getNbt();

            if (root != null && root.contains(SpoornWeaponAttributesUtil.NBT_KEY)) {
                return Optional.of(root.getCompound(SpoornWeaponAttributesUtil.NBT_KEY));
            }
        }
        return Optional.empty();
    }
    
    public static boolean hasSWANbt(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(NBT_KEY);
    }
    
    public static boolean isLapisLazuli(ItemStack stack) {
        return stack.getItem().equals(Items.LAPIS_LAZULI);
    }

    /**
     * Assumes chance is between 0.0 and 1.0.
     */
    public static boolean shouldEnable(float chance) {
        return (chance > 0) && (RANDOM.nextFloat() < chance);
    }

    public static boolean shouldEnable(double chance) {
        return (chance > 0) && (RANDOM.nextDouble() < chance);
    }

    public static float getRandomInRange(float min, float max) {
        return RANDOM.nextFloat() * (max - min) + min;
    }

    public static int getRandomInRange(int min, int max) {
        return Math.round(RANDOM.nextFloat() * (max - min) + min);
    }

    public static float drawRandom(boolean useGaussian, float mean, double sd, float min, float max) {
        if (useGaussian) {
            return (float) getNextGaussian(mean, sd, min, max);
        } else {
            return getRandomInRange(min, max);
        }
    }

    // Assumes parameters are correct
    public static double getNextGaussian(float mean, double sd, float min, float max) {
        double nextGaussian = RANDOM.nextGaussian() * sd + mean;
        if (nextGaussian < min) {
            nextGaussian = min;
        } else if (nextGaussian > max) {
            nextGaussian = max;
        }
        return nextGaussian;
    }
    
    
    // Apply attributes
    public static void rollAttributes(NbtCompound root) {
        if (!root.contains(SpoornWeaponAttributesUtil.NBT_KEY) && !root.contains(SPOORN_LOOT_NBT_KEY)) {
            NbtCompound nbt = SpoornWeaponAttributesUtil.createAttributesSubNbt(root);
            //System.out.println("Initial Nbt: " + nbt);

            for (Map.Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
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

    /**
     * We manually list all the handles here for optimal latency
     */

    private static void handleCrit(NbtCompound nbt) {
        CritConfig config = ModConfig.get().critConfig;
        float critChance = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minCritChance, config.maxCritChance);
        if (config.useGaussian) {
            critChance /= 100;
        }
        nbt.putFloat(CRIT_CHANCE, critChance);
    }

    private static void handleFire(NbtCompound nbt) {
        FireConfig config = ModConfig.get().fireConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
        float duration = (float) Expressions.fireDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
        nbt.putFloat(DURATION, duration);
    }

    private static void handleCold(NbtCompound nbt) {
        ColdConfig config = ModConfig.get().coldConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
        float slowDuration = (float) Expressions.slowDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
        nbt.putFloat(SLOW_DURATION, slowDuration);
        float freezeDuration = (float) Expressions.freezeDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
        nbt.putFloat(FREEZE_DURATION, freezeDuration);
    }

    private static void handleLightning(NbtCompound nbt) {
        LightningConfig config = ModConfig.get().lightningConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
    }

    private static void handlePoison(NbtCompound nbt) {
        PoisonConfig config = ModConfig.get().poisonConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
        float duration = (float) Expressions.poisonDuration.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
        nbt.putFloat(DURATION, duration);
    }

    private static void handleLifesteal(NbtCompound nbt) {
        LifestealConfig config = ModConfig.get().lifestealConfig;
        float lifesteal = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minLifesteal, config. maxLifesteal);
        nbt.putFloat(LIFESTEAL, lifesteal);
    }

    private static void handleExplosive(NbtCompound nbt) {
        ExplosiveConfig config = ModConfig.get().explosiveConfig;
        nbt.putFloat(EXPLOSION_CHANCE, (float) config.explosionChance);
    }
}
