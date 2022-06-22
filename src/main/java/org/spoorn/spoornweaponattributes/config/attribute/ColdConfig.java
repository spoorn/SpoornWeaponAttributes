package org.spoorn.spoornweaponattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class ColdConfig {

    @Comment("Chance for a weapon to have cold damage [0 = never, 1 = always] [default = 0.05]")
    public double attributeChance = 0.05;

    @Comment("Expression for duration in seconds to slow target [0 = don't slow] [default = (damage+7)/2]")
    public String slowDuration = "(damage+7)/2";
    
    @Comment("Expression for duration in seconds to add frozen effect to target (deals damage over time) [0 = don't freeze] [default = (damage+7)/2]")
    public String freezeDuration = "(damage+7)/2";
    
    @Comment("Minimum bonus damage [default = 1.0]")
    public float minDamage = 1;

    @Comment("Maximum bonus damage [default = 30.0]")
    public float maxDamage = 30;

    @Comment("True if damage should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minDamage and maxDamage [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~5.48% chance of getting above 5 damage
    // Use https://onlinestatbook.com/2/calculators/normal_dist.html
    @Comment("Average damage [default = 1]")
    public int mean = 1;

    @Comment("Standard deviation for the distribution [default = 2.5]")
    public double standardDeviation = 2.5;
}
