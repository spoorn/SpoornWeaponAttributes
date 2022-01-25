package org.spoorn.spoornweaponattributes.att;

import lombok.AllArgsConstructor;
import org.spoorn.spoornweaponattributes.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains generic information of the attributes.
 */
@AllArgsConstructor
public class Attribute {

    // WARNING: Changing the name in the static initialization will break existing Nbt data
    public static final String CRIT_NAME = "CRIT";
    public static final String FIRE_NAME = "FIRE";
    public static final String COLD_NAME = "COLD";
    public static final String LIGHTNING_NAME = "LIGHTNING";
    public static final String POISON_NAME = "POISON";

    public static Attribute CRIT;
    public static Attribute FIRE;
    public static Attribute COLD;
    public static Attribute LIGHTNING;
    public static Attribute POISON;

    public static Map<String, Attribute> VALUES = new HashMap<>();

    public final String name;
    public final double chance;

    public static void init() {
        CRIT = new Attribute(CRIT_NAME, ModConfig.get().critConfig.attributeChance);
        FIRE = new Attribute(FIRE_NAME, ModConfig.get().fireConfig.attributeChance);
        COLD = new Attribute(COLD_NAME, ModConfig.get().coldConfig.attributeChance);
        LIGHTNING = new Attribute(LIGHTNING_NAME, ModConfig.get().lightningConfig.attributeChance);
        POISON = new Attribute(POISON_NAME, ModConfig.get().poisonConfig.attributeChance);
        VALUES.put(CRIT.name, CRIT);
        VALUES.put(FIRE.name, FIRE);
        VALUES.put(COLD.name, COLD);
        VALUES.put(LIGHTNING.name, LIGHTNING);
        VALUES.put(POISON.name, POISON);
    }
}
