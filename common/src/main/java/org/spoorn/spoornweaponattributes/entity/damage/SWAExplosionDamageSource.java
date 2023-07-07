package org.spoorn.spoornweaponattributes.entity.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Used to identify if an explosion was from this mod.
 */
public class SWAExplosionDamageSource extends DamageSource {

    public SWAExplosionDamageSource(RegistryEntry<DamageType> type) {
        super(type);
    }
}
