package org.spoorn.spoornweaponattributes.config;

import lombok.extern.log4j.Log4j2;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@Log4j2
public class Expressions {
    
    public static final String DAMAGE_VAR = "damage";
    public static Expression fireDuration;
    public static Expression slowDuration;
    public static Expression freezeDuration;
    public static Expression poisonDuration;
    
    public static void init() {
        ModConfig modConfig = ModConfig.get();
        
        try {
            fireDuration = new ExpressionBuilder(modConfig.fireConfig.fireDuration).variable(DAMAGE_VAR).build();
        } catch (Exception e) {
            log.error("fireDuration expression is invalid", e);
            throw new IllegalArgumentException("fireDuration expression is invalid", e);
        }

        try {
            slowDuration = new ExpressionBuilder(modConfig.coldConfig.slowDuration).variable(DAMAGE_VAR).build();
        } catch (Exception e) {
            log.error("slowDuration expression is invalid", e);
            throw new IllegalArgumentException("slowDuration expression is invalid", e);
        }

        try {
            freezeDuration = new ExpressionBuilder(modConfig.coldConfig.freezeDuration).variable(DAMAGE_VAR).build();
        } catch (Exception e) {
            log.error("freezeDuration expression is invalid", e);
            throw new IllegalArgumentException("freezeDuration expression is invalid", e);
        }

        try {
            poisonDuration = new ExpressionBuilder(modConfig.poisonConfig.poisonDuration).variable(DAMAGE_VAR).build();
        } catch (Exception e) {
            log.error("poisonDuration expression is invalid", e);
            throw new IllegalArgumentException("poisonDuration expression is invalid", e);
        }
    }
}
