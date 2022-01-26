package org.spoorn.spoornweaponattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class LifestealConfig {

    @Comment("Chance for a weapon to have lifesteal [0 = never, 1 = always] [default = 0.02]")
    public double attributeChance = 0.02;

    @Comment("Minimum lifesteal percentage [default = 1]")
    public int minLifesteal = 1;

    @Comment("Maximum lifesteal percentage [default = 100]")
    public int maxLifesteal = 100;

    @Comment("True if damage should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minDamage and maxDamage [default = true]")
    public boolean useGaussian = true;

    @Comment("Average lifesteal [default = 10]")
    public int mean = 10;

    @Comment("Standard deviation for the distribution [default = 10]")
    public int standardDeviation = 10;
}
