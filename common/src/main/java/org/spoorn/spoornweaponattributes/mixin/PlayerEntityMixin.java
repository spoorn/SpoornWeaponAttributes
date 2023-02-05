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
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.spoornweaponattributes.att.Attribute;
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.config.attribute.ExplosiveConfig;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

import java.util.Map.Entry;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    /**
     * Fetch data from NBT and apply pre damage modifications.
     * 
     * ordinal = 0 is required for Forge, else this ends up also redirecting some knockback velocity for some reason.
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", ordinal = 0))
    public double modifyBaseDamage(PlayerEntity instance, EntityAttribute entityAttribute, Entity target) {
        float f = (float) instance.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        try {
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
        } catch (Exception e) {
            System.err.println("[SpoornWeaponAttributes] Applying base attribute effects failed: " + e);
        }

        return f;
    }

    /**
     * Fetch data from NBT and apply modifications to final damage.
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    public boolean modifyFinalDamage(Entity instance, DamageSource source, float amount) {
        try {
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

                    if (nbt.contains(Attribute.EXPLOSIVE_NAME)) {
                        NbtCompound subNbt = nbt.getCompound(Attribute.EXPLOSIVE_NAME);
                        amount = handleExplosive(amount, subNbt, player, instance);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SpoornWeaponAttributes] Applying final attribute effects failed: " + e);
        }
        return instance.damage(source, amount);
    }


    /**
     * We manually list all the handles here for optimal latency
     */

    private float handleFire(NbtCompound nbt, PlayerEntity player, Entity target) {
        int fireDurationTicks = 0;
        if (nbt.contains(DURATION)) {
            fireDurationTicks = (int) (nbt.getFloat(DURATION) * 20);
        }
        if (fireDurationTicks > 0 && target.getFireTicks() < fireDurationTicks) {
            target.setFireTicks(fireDurationTicks);
        }
        if (nbt.contains(BONUS_DAMAGE)) {
            return nbt.getFloat(BONUS_DAMAGE);
        }
        return 0;
    }

    private float handleCold(NbtCompound nbt, PlayerEntity player, Entity target) {
        LivingEntity livingEntity = (LivingEntity) target;

        int freezeDurationTicks = 0;
        if (nbt.contains(FREEZE_DURATION)) {
            // *40 because freeze duration decreases by 2 per tick
            freezeDurationTicks = (int) (nbt.getFloat(FREEZE_DURATION) * 40);
        }
        if (freezeDurationTicks > 0 && livingEntity.getFrozenTicks() < freezeDurationTicks) {
            livingEntity.setFrozenTicks(freezeDurationTicks);
        }
        
        int slowDurationTicks = 0;
        if (nbt.contains(SLOW_DURATION)) {
            slowDurationTicks = (int) (nbt.getFloat(SLOW_DURATION)) * 20;
        }
        if (slowDurationTicks > 0) {
            StatusEffectInstance existingSlowEffect = livingEntity.getStatusEffect(StatusEffects.SLOWNESS);
            if (existingSlowEffect == null || existingSlowEffect.getDuration() < slowDurationTicks) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slowDurationTicks, 2));
            }
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
        int poisonDurationTicks = 0;
        if (nbt.contains(DURATION)) {
            poisonDurationTicks = (int) (nbt.getFloat(DURATION) * 20);
        }
        
        if (poisonDurationTicks > 0) {
            LivingEntity livingEntity = (LivingEntity) target;
            StatusEffectInstance existingEffect = livingEntity.getStatusEffect(StatusEffects.POISON);
            if (existingEffect == null || existingEffect.getDuration() < poisonDurationTicks) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, poisonDurationTicks, 2));
            }
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
            player.heal(lifesteal * damage / 100);
        }
        return damage;
    }

    private float handleExplosive(float damage, NbtCompound nbt, PlayerEntity player, Entity target) {
        if (nbt.contains(EXPLOSION_CHANCE)) {
            float explosionChance = nbt.getFloat(EXPLOSION_CHANCE);
            ExplosiveConfig config = ModConfig.get().explosiveConfig;
            if (SpoornWeaponAttributesUtil.shouldEnable(explosionChance) && !target.world.isClient()) {
                target.world.createExplosion(target, SWA_EXPLOSION_DAMAGE_SOURCE, null, target.getX(), target.getY(), target.getZ(),
                        (float) config.explosionPower, config.causeFires, config.breakBlocks ? World.ExplosionSourceType.TNT : World.ExplosionSourceType.NONE);
            }
        }
        return damage;
    }
}
