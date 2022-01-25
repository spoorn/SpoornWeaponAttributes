package org.spoorn.spoornweaponattributes.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;

import java.util.Random;

/**
 * All generic utilities.
 */
public final class SpoornWeaponAttributesUtil {

    public static final String NBT_KEY = "spoornweaponattributes";
    public static final String BONUS_DAMAGE = "bonusDmg";
    public static final Random RANDOM = new Random();

    public static boolean shouldTryGenAttr(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }

    public static NbtCompound createAttributesSubNbt(NbtCompound root) {
        NbtCompound res = new NbtCompound();
        root.put(NBT_KEY, res);
        return res;
    }

    public static float getRandomInRange(float min, float max) {
        return RANDOM.nextFloat() * (max - min) + min;
    }
}
