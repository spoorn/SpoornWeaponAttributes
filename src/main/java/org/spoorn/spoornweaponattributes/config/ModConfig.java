package org.spoorn.spoornweaponattributes.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import org.spoorn.spoornweaponattributes.SpoornWeaponAttributes;
import org.spoorn.spoornweaponattributes.config.attribute.*;

@Config(name = SpoornWeaponAttributes.MODID)
public class ModConfig implements ConfigData {

    @Comment("Critical Hit attribute config")
    public CritConfig critConfig = new CritConfig();

    @Comment("Fire attribute config")
    public FireConfig fireConfig = new FireConfig();

    @Comment("Cold attribute config")
    public ColdConfig coldConfig = new ColdConfig();

    @Comment("Lightning attribute config")
    public LightningConfig lightningConfig = new LightningConfig();

    @Comment("Poison attribute config")
    public PoisonConfig poisonConfig = new PoisonConfig();

    @Comment("Lifesteal attribute config")
    public LifestealConfig lifestealConfig = new LifestealConfig();

    @Comment("Explosive attribute config")
    public ExplosiveConfig explosiveConfig = new ExplosiveConfig();

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        Expressions.init();
    }

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
