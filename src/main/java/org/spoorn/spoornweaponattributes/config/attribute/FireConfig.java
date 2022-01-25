package org.spoorn.spoornweaponattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class FireConfig {

    @Comment("Chance for a weapon to have fire damage [0 = never, 1 = always] [default = 0.05]")
    public double attributeChance = 0.05;

    @Comment("Duration in seconds to set target on fire for [0 = don't set on fire] [default = 5]")
    public int fireDuration = 5;

    @Comment("Minimum bonus damage [default = 1.0]")
    public float minDamage = 1;

    @Comment("Maximum bonus damage [default = 30.0]")
    public float maxDamage = 30;

    @Comment("True if damage should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minDamage and maxDamage [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~15% chance of getting above 5 damage
    @Comment("Average damage [default = 1]")
    public int mean = 1;

    @Comment("Standard deviation for the distribution [default = 4]")
    public int standardDeviation = 4;
}
