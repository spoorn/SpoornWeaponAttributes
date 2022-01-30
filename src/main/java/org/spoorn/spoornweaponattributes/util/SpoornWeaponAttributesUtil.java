package org.spoorn.spoornweaponattributes.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import org.spoorn.spoornweaponattributes.entity.damage.SWAExplosionDamageSource;

import java.util.Optional;
import java.util.Random;

/**
 * All generic utilities.
 */
public final class SpoornWeaponAttributesUtil {

    public static final String NBT_KEY = "spoornWeaponAttributes";
    public static final String BONUS_DAMAGE = "bonusDmg";
    public static final String CRIT_CHANCE = "critChance";
    public static final String LIFESTEAL = "lifesteal";
    public static final String EXPLOSION_CHANCE = "explosionChance";
    private static final String EXPLOSION_DAMAGE_SOURCE_ID = "swa.explosion";
    public static final SWAExplosionDamageSource SWA_EXPLOSION_DAMAGE_SOURCE = new SWAExplosionDamageSource(EXPLOSION_DAMAGE_SOURCE_ID);
    public static final Random RANDOM = new Random();

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
}
