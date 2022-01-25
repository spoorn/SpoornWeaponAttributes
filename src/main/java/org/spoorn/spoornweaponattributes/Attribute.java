package org.spoorn.spoornweaponattributes;

import lombok.AllArgsConstructor;
import org.spoorn.spoornweaponattributes.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class Attribute {

    public static Attribute CRIT;
    public static Attribute FIRE;
    public static Attribute COLD;
    public static Attribute LIGHTNING;
    public static Attribute POISON;

    public static Map<String, Attribute> VALUES = new HashMap<>();

    // WARNING: Changing the name in the static initialization will break existing Nbt data
    public final String name;
    public final int chance;
    public final boolean addsDirectDamage;

    public static void init() {
        CRIT = new Attribute("CRIT", ModConfig.get().critChanceChance, false);
        FIRE = new Attribute("FIRE", ModConfig.get().fireDamageChance, true);
        COLD = new Attribute("COLD", ModConfig.get().coldDamageChance, true);
        LIGHTNING = new Attribute("LIGHTNING", ModConfig.get().lightningDamageChance, true);
        POISON = new Attribute("POISON", ModConfig.get().poisonDamageChance, true);
        VALUES.put(CRIT.name, CRIT);
        VALUES.put(FIRE.name, FIRE);
        VALUES.put(COLD.name, COLD);
        VALUES.put(LIGHTNING.name, LIGHTNING);
        VALUES.put(POISON.name, POISON);
    }
}
