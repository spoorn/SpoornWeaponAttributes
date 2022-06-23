package org.spoorn.spoornweaponattributes.util;

import lombok.extern.log4j.Log4j2;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.spoornweaponattributes.att.Attribute;
import org.spoorn.spoornweaponattributes.att.Roller;
import org.spoorn.spoornweaponattributes.config.Expressions;
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.entity.damage.SWAExplosionDamageSource;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * All generic utilities.
 */
@Log4j2
public final class SpoornWeaponAttributesUtil {

    public static final String NBT_KEY = "swa3";
    public static final String REROLL_NBT_KEY = "swa3_reroll";
    public static final String UPGRADE_NBT_KEY = "swa3_upgrade";
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

    public static NbtCompound createAttributesSubNbtReturnRoot(NbtCompound root) {
        NbtCompound res = new NbtCompound();
        root.put(NBT_KEY, res);
        return root;
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

    public static boolean isRerollItem(ItemStack stack) {
        String rerollItem = ModConfig.get().rerollItem;
        Optional<Item> item = Registry.ITEM.getOrEmpty(new Identifier(rerollItem));
        if (item.isEmpty()) {
            throw new RuntimeException("Reroll item " + rerollItem + " was not found in the registry!");
        }
        return stack.getItem().equals(item.get());
    }

    public static boolean isUpgradeItem(ItemStack stack) {
        String upgradeItem = ModConfig.get().upgradeItem;
        Optional<Item> item = Registry.ITEM.getOrEmpty(new Identifier(upgradeItem));
        if (item.isEmpty()) {
            throw new RuntimeException("Upgrade item " + upgradeItem + " was not found in the registry!");
        }
        return stack.getItem().equals(item.get());
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

    public static void rollOrUpgradeNbt(NbtCompound root) {
        if (root.getBoolean(UPGRADE_NBT_KEY)) {
            SpoornWeaponAttributesUtil.upgradeAttributes(root);
            root.remove(UPGRADE_NBT_KEY);
        } else if (root.getBoolean(REROLL_NBT_KEY)) {
            SpoornWeaponAttributesUtil.rollAttributes(root);
            root.remove(REROLL_NBT_KEY);
        }
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
                    float bonusDamage;
                    switch (name) {
                        case Attribute.CRIT_NAME:
                            newNbt.putFloat(CRIT_CHANCE, Roller.rollCrit());
                            break;
                        case Attribute.FIRE_NAME:
                            bonusDamage = Roller.rollFire();
                            newNbt.putFloat(BONUS_DAMAGE, bonusDamage);
                            newNbt.putFloat(DURATION, Roller.rollDamageDuration(Expressions.fireDuration, bonusDamage));
                            break;
                        case Attribute.COLD_NAME:
                            bonusDamage = Roller.rollCold();
                            newNbt.putFloat(BONUS_DAMAGE, bonusDamage);
                            newNbt.putFloat(SLOW_DURATION, Roller.rollDamageDuration(Expressions.slowDuration, bonusDamage));
                            newNbt.putFloat(FREEZE_DURATION, Roller.rollDamageDuration(Expressions.freezeDuration, bonusDamage));
                            break;
                        case Attribute.LIGHTNING_NAME:
                            newNbt.putFloat(BONUS_DAMAGE, Roller.rollLightning());
                            break;
                        case Attribute.POISON_NAME:
                            bonusDamage = Roller.rollPoison();
                            newNbt.putFloat(BONUS_DAMAGE, bonusDamage);
                            newNbt.putFloat(DURATION, Roller.rollDamageDuration(Expressions.poisonDuration, bonusDamage));
                            break;
                        case Attribute.LIFESTEAL_NAME:
                            newNbt.putFloat(LIFESTEAL, Roller.rollLifesteal());
                            break;
                        case Attribute.EXPLOSIVE_NAME:
                            newNbt.putFloat(EXPLOSION_CHANCE, Roller.rollExplosive());
                            break;
                        default:
                            // do nothing
                            log.error("Unknown SpoornWeaponAttribute: {}", name);
                    }
                    nbt.put(name, newNbt);
                }
            }
            //System.out.println("Updated Nbt: " + nbt);
        }
    }

    // Upgrade stats if applicable
    public static void upgradeAttributes(NbtCompound root) {
        if (!root.contains(SPOORN_LOOT_NBT_KEY)) {
            if (!root.contains(SpoornWeaponAttributesUtil.NBT_KEY)) {
                SpoornWeaponAttributesUtil.createAttributesSubNbtReturnRoot(root);
            }
            NbtCompound nbt = root.getCompound(SpoornWeaponAttributesUtil.NBT_KEY);

            for (Map.Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
                String name = entry.getKey();
                Attribute att = entry.getValue();

                if (SpoornWeaponAttributesUtil.shouldEnable(att.chance)) {
                    NbtCompound newNbt = nbt.contains(name) ? nbt.getCompound(name) : new NbtCompound();

                    float bonusDamage;
                    switch (name) {
                        case Attribute.CRIT_NAME:
                            checkFloatUpgradeThenAdd(newNbt, CRIT_CHANCE, Roller.rollCrit());
                            break;
                        case Attribute.FIRE_NAME:
                            bonusDamage = Roller.rollFire();
                            checkFloatUpgradeThenAdd(newNbt, BONUS_DAMAGE, bonusDamage);
                            checkFloatUpgradeThenAdd(newNbt, DURATION, Roller.rollDamageDuration(Expressions.fireDuration, bonusDamage));
                            break;
                        case Attribute.COLD_NAME:
                            bonusDamage = Roller.rollCold();
                            checkFloatUpgradeThenAdd(newNbt, BONUS_DAMAGE, bonusDamage);
                            checkFloatUpgradeThenAdd(newNbt, SLOW_DURATION, Roller.rollDamageDuration(Expressions.slowDuration, bonusDamage));
                            checkFloatUpgradeThenAdd(newNbt, FREEZE_DURATION, Roller.rollDamageDuration(Expressions.freezeDuration, bonusDamage));
                            break;
                        case Attribute.LIGHTNING_NAME:
                            checkFloatUpgradeThenAdd(newNbt, BONUS_DAMAGE, Roller.rollLightning());
                            break;
                        case Attribute.POISON_NAME:
                            bonusDamage = Roller.rollPoison();
                            checkFloatUpgradeThenAdd(newNbt, BONUS_DAMAGE, bonusDamage);
                            checkFloatUpgradeThenAdd(newNbt, DURATION, Roller.rollDamageDuration(Expressions.poisonDuration, bonusDamage));
                            break;
                        case Attribute.LIFESTEAL_NAME:
                            checkFloatUpgradeThenAdd(newNbt, LIFESTEAL, Roller.rollLifesteal());
                            break;
                        case Attribute.EXPLOSIVE_NAME:
                            checkFloatUpgradeThenAdd(newNbt, EXPLOSION_CHANCE, Roller.rollExplosive());
                            break;
                        default:
                            // do nothing
                            log.error("Unknown SpoornWeaponAttribute: {}", name);
                    }
                    nbt.put(name, newNbt);
                }
            }
            //System.out.println("Updated Nbt: " + nbt);
        }
    }

    private static void checkFloatUpgradeThenAdd(NbtCompound nbt, String attribute, float newValue) {
        if (!nbt.contains(attribute) || nbt.getFloat(attribute) < newValue) {
            nbt.putFloat(attribute, newValue);
        }
    }
}
