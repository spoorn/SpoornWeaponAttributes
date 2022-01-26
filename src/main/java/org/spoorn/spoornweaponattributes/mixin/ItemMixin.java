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
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.config.attribute.*;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

import java.util.Map.Entry;

@Mixin(Item.class)
public class ItemMixin {

    /**
     * Generates the NBT data for our mod on an item.
     */
    @Inject(method = "inventoryTick", at = @At(value = "HEAD"))
    public void addCustomNbt(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        // TODO: compat with Spoorn Loot
        if (SpoornWeaponAttributesUtil.shouldTryGenAttr(stack)) {
            NbtCompound root = stack.getOrCreateTag();
            if (!root.contains(SpoornWeaponAttributesUtil.NBT_KEY) && !root.contains("spoornConfig")) {
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
    }

    private void handleCold(NbtCompound nbt) {
        ColdConfig config = ModConfig.get().coldConfig;
        float bonusDamage = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config. maxDamage);
        nbt.putFloat(BONUS_DAMAGE, bonusDamage);
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
    }

    private void handleLifesteal(NbtCompound nbt) {
        LifestealConfig config = ModConfig.get().lifestealConfig;
        float lifesteal = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minLifesteal, config. maxLifesteal);
        nbt.putFloat(LIFESTEAL, lifesteal);
    }
}
