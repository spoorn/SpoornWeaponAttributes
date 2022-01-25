package org.spoorn.spoornweaponattributes.config.attribute;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class CritConfig {

    @Comment("Chance for a weapon to have critical hit property [0 = never, 1 = always] [default = 0.1]")
    public double attributeChance = 0.1;

    @Comment("Minimum crit chance [min = 0, max = 100] [default = 20]")
    @ConfigEntry.BoundedDiscrete(min = 0L, max = 100L)
    public int minCritChance = 20;

    @Comment("Maximum crit chance [min = 0, max = 100] [default = 100]")
    @ConfigEntry.BoundedDiscrete(min = 0L, max = 100L)
    public int maxCritChance = 100;

    @Comment("True if crit chance should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minCritChance and maxCritChance [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~31% chance of getting above 50% crit chance
    @Comment("Average crit chance [min = 0, max = 100] [default = 25]")
    @ConfigEntry.BoundedDiscrete(min = 0L, max = 100L)
    public int mean = 25;

    @Comment("Standard deviation for the distribution [default = 50]")
    public int standardDeviation = 50;
}
