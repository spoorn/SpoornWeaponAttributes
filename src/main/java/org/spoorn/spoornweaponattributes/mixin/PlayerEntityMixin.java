package org.spoorn.spoornweaponattributes.mixin;

import static org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.spoornweaponattributes.att.Attribute;
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

import java.util.Map.Entry;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    /**
     * Fetch data from NBT and apply pre damage modifications.
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    public double modifyBaseDamage(PlayerEntity instance, EntityAttribute entityAttribute, Entity target) {
        float f = (float)instance.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        if ((instance instanceof ServerPlayerEntity) && (target instanceof LivingEntity)) {
            ItemStack mainItemStack = instance.getMainHandStack();
            Optional<NbtCompound> optNbt = SpoornWeaponAttributesUtil.getSWANbtIfPresent(mainItemStack);
            if (optNbt.isPresent()) {
                NbtCompound nbt = optNbt.get();
                for (Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
                    String name = entry.getKey();

                    if (nbt.contains(name)) {
                        NbtCompound subNbt = nbt.getCompound(name);
                        switch (name) {
                            case Attribute.FIRE_NAME:
                                f += handleFire(subNbt, instance, target);
                                break;
                            case Attribute.COLD_NAME:
                                f += handleCold(subNbt, instance, target);
                                break;
                            case Attribute.LIGHTNING_NAME:
                                f += handleLightning(subNbt, instance, target);
                                break;
                            case Attribute.POISON_NAME:
                                f += handlePoison(subNbt, instance, target);
                                break;
                            default:
                                // crit, lifesteal, and other attributes apply to final damage
                                // do nothing
                        }
                    }
                }
            }
        }

        return f;
    }

    /**
     * Fetch data from NBT and apply modifications to final damage.
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    public boolean modifyFinalDamage(Entity instance, DamageSource source, float amount) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if ((player instanceof ServerPlayerEntity) && (instance instanceof LivingEntity)) {
            ItemStack mainItemStack = player.getMainHandStack();
            Optional<NbtCompound> optNbt = SpoornWeaponAttributesUtil.getSWANbtIfPresent(mainItemStack);
            if (optNbt.isPresent()) {
                NbtCompound nbt = optNbt.get();
                if (nbt.contains(Attribute.CRIT_NAME)) {
                    NbtCompound subNbt = nbt.getCompound(Attribute.CRIT_NAME);
                    amount = handleCrit(amount, subNbt, player, instance);
                }

                if (nbt.contains(Attribute.LIFESTEAL_NAME)) {
                    NbtCompound subNbt = nbt.getCompound(Attribute.LIFESTEAL_NAME);
                    amount = handleLifesteal(amount, subNbt, player, instance);
                }
            }
        }
        return instance.damage(source, amount);
    }

    /**
     * We manually list all the handles here for optimal latency
     */

    private float handleFire(NbtCompound nbt, PlayerEntity player, Entity target) {
        int fireDuration = ModConfig.get().fireConfig.fireDuration;
        if (fireDuration > 0) {
            target.setOnFireFor(fireDuration);
        }
        if (nbt.contains(BONUS_DAMAGE)) {
            return nbt.getFloat(BONUS_DAMAGE);
        }
        return 0;
    }

    private float handleCold(NbtCompound nbt, PlayerEntity player, Entity target) {
        int slowDuration = ModConfig.get().coldConfig.slowDuration;
        if (slowDuration > 0) {
            LivingEntity livingEntity = (LivingEntity) target;
            livingEntity.applyStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * slowDuration, 2));
        }
        if (nbt.contains(BONUS_DAMAGE)) {
            return nbt.getFloat(BONUS_DAMAGE);
        }
        return 0;
    }

    private float handleLightning(NbtCompound nbt, PlayerEntity player, Entity target) {
        double lightningStrikeChance = ModConfig.get().lightningConfig.lightningStrikeChance;
        if (SpoornWeaponAttributesUtil.shouldEnable(lightningStrikeChance)) {
            World world = target.world;
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
            lightningEntity.teleport(target.getX(), target.getY(), target.getZ());
            world.spawnEntity(lightningEntity);
        }
        if (nbt.contains(BONUS_DAMAGE)) {
            return nbt.getFloat(BONUS_DAMAGE);
        }
        return 0;
    }

    private float handlePoison(NbtCompound nbt, PlayerEntity player, Entity target) {
        int poisonDuration = ModConfig.get().poisonConfig.poisonDuration;
        if (poisonDuration > 0) {
            LivingEntity livingEntity = (LivingEntity) target;
            livingEntity.applyStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 20 * poisonDuration, 2));
        }
        if (nbt.contains(BONUS_DAMAGE)) {
            return nbt.getFloat(BONUS_DAMAGE);
        }
        return 0;
    }

    private float handleCrit(float damage, NbtCompound nbt, PlayerEntity player, Entity target) {
        if (nbt.contains(CRIT_CHANCE)) {
            float critChance = nbt.getFloat(CRIT_CHANCE);
            if (SpoornWeaponAttributesUtil.shouldEnable(critChance)) {
                return (float) (damage * ModConfig.get().critConfig.critMultiplier);
            }
        }
        return damage;
    }

    private float handleLifesteal(float damage, NbtCompound nbt, PlayerEntity player, Entity target) {
        if (nbt.contains(LIFESTEAL)) {
            float lifesteal = nbt.getFloat(LIFESTEAL);
            player.heal(lifesteal * damage);
        }
        return damage;
    }
}
