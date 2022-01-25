package org.spoorn.spoornweaponattributes.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import org.spoorn.spoornweaponattributes.SpoornWeaponAttributes;

@Config(name = SpoornWeaponAttributes.MODID)
public class ModConfig implements ConfigData {

    @Comment("Chance for a weapon to have critical hit property [1/value, 0 = never, 1 = always] [default = 10]")
    public int critChanceChance = 10;

    @Comment("Chance for a weapon to have fire damage [1/value, 0 = never, 1 = always] [default = 10]")
    public int fireDamageChance = 5;

    @Comment("Chance for a weapon to have cold damage [1/value, 0 = never, 1 = always] [default = 10]")
    public int coldDamageChance = 5;

    @Comment("Chance for a weapon to have lightning damage [1/value, 0 = never, 1 = always] [default = 10]")
    public int lightningDamageChance = 5;

    @Comment("Chance for a weapon to have poison damage [1/value, 0 = never, 1 = always] [default = 10]")
    public int poisonDamageChance = 5;

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
    }

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
