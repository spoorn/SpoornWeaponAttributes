package org.spoorn.spoornweaponattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class ExplosiveConfig {

    @Comment("Chance for a weapon to be Explosive [0 = never, 1 = always] [default = 0.02]\n" +
            "The explosion by default does not hurt other players.")
    public double attributeChance = 0.02;

    @Comment("Chance for each attack to cause an explosion [0 = never, 1 = always] [default = 1.0]")
    public double explosionChance = 1.0;

    @Comment("Power of explosion, affects the radius of explosion [default = 2.0]")
    public double explosionPower = 2.0;

    @Comment("True if explosions should cause fires, else false [default = false]")
    public boolean causeFires = false;

    @Comment("True if explosions should break blocks [default = false]")
    public boolean breakBlocks = false;
}
