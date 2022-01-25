package org.spoorn.spoornweaponattributes.mixin;

import net.fabricmc.fabric.api.event.registry.ItemConstructedCallback;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.spoornweaponattributes.Attribute;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

import java.util.Map;
import java.util.Map.Entry;

@Mixin(Item.class)
public class ItemMixin {

    private static final String CHANCE = "chance";

    @Inject(method = "inventoryTick", at = @At(value = "HEAD"))
    public void addCustomNbt(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (SpoornWeaponAttributesUtil.shouldTryGenAttr(stack)) {
            NbtCompound root = stack.getOrCreateTag();
            if (!root.contains(SpoornWeaponAttributesUtil.NBT_KEY)) {
                NbtCompound nbt = SpoornWeaponAttributesUtil.createAttributesSubNbt(root);
                System.out.println("Initial Nbt: " + nbt);

                for (Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
                    String name = entry.getKey();
                    Attribute att = entry.getValue();
                    if (att.chance > 0 && SpoornWeaponAttributesUtil.RANDOM.nextFloat() < 1.0f/att.chance) {
                        NbtCompound newNbt = new NbtCompound();

                        if (att.addsDirectDamage) {
                            newNbt.putFloat(SpoornWeaponAttributesUtil.BONUS_DAMAGE, SpoornWeaponAttributesUtil.getRandomInRange(1.0f, 10.0f));
                        }

                        nbt.put(name, newNbt);
                    }
                }

                System.out.println("Updated Nbt: " + nbt);
            }
        }
    }
}
