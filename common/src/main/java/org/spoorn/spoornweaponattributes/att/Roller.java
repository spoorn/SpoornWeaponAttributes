package org.spoorn.spoornweaponattributes.att;

import net.objecthunter.exp4j.Expression;
import org.spoorn.spoornweaponattributes.config.Expressions;
import org.spoorn.spoornweaponattributes.config.ModConfig;
import org.spoorn.spoornweaponattributes.config.attribute.*;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

public class Roller {
    
    public static float rollCrit() {
        CritConfig config = ModConfig.get().critConfig;
        float critChance = SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minCritChance, config.maxCritChance);
        if (config.useGaussian) {
            critChance /= 100;
        }
        return critChance;
    }
    
    public static float rollDamageDuration(Expression expression, float bonusDamage) {
        return (float) expression.setVariable(Expressions.DAMAGE_VAR, bonusDamage).evaluate();
    }
    
    public static float rollFire() {
        FireConfig config = ModConfig.get().fireConfig;
        return SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config.maxDamage);
    }
    
    public static float rollCold() {
        ColdConfig config = ModConfig.get().coldConfig;
        return SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config.maxDamage);
    }
    
    public static float rollLightning() {
        LightningConfig config = ModConfig.get().lightningConfig;
        return SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config.maxDamage);
    }
    
    public static float rollPoison() {
        PoisonConfig config = ModConfig.get().poisonConfig;
        return SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDamage, config.maxDamage);
    }
    
    public static float rollLifesteal() {
        LifestealConfig config = ModConfig.get().lifestealConfig;
        return SpoornWeaponAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minLifesteal, config.maxLifesteal);
    }
    
    public static float rollExplosive() {
        ExplosiveConfig config = ModConfig.get().explosiveConfig;
        return (float) config.explosionChance;
    }
}
