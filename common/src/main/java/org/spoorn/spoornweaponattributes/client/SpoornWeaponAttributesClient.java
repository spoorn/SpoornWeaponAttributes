package org.spoorn.spoornweaponattributes.client;

import static org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil.*;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spoorn.spoornweaponattributes.att.Attribute;
import org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Log4j2
@Environment(EnvType.CLIENT)
public class SpoornWeaponAttributesClient {

    private static final Style FIRE_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16732754));
    private static final Style COLD_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(4890623));
    private static final Style CRIT_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(9851135));
    private static final Style LIGHTNING_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(15990666));
    private static final Style POISON_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(32537));
    private static final Style LIFESTESAL_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(7864320));
    private static final Style EXPLOSIVE_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16711680));
    private static final MutableText FIRE_TOOLTIP = Text.translatable("swa.tooltip.firedamage");
    private static final MutableText COLD_TOOLTIP = Text.translatable("swa.tooltip.colddamage");
    private static final MutableText CRIT_TOOLTIP = Text.translatable("swa.tooltip.critchance");
    private static final MutableText LIGHTNING_TOOLTIP = Text.translatable("swa.tooltip.lightningdamage");
    private static final MutableText POISON_TOOLTIP = Text.translatable("swa.tooltip.poisondamage");
    private static final MutableText LIFESTEAL_TOOLTIP = Text.translatable("swa.tooltip.lifesteal");
    private static final MutableText EXPLOSIVE_TOOLTIP = Text.translatable("swa.tooltip.explosive");
    private static final MutableText EXPLOSIVE_PREPEND_TOOLTIP = Text.translatable("swa.tooltip.explosiveprepend").formatted(Formatting.ITALIC, Formatting.DARK_GRAY);
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#", SYMBOLS);
    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#", SYMBOLS);

    private static final String LIFESTEAL_NO_TOOLTIP = "0";

    public static void init() {
        log.info("Hello client from SpoornWeaponAttributes!");
    }
    
    // Mimic Fabric API's ItemTooltipCallback with our own Mixin to support both Fabric and Forge without introducing
    // Fabric API as a dependency, yet still be easily converted back
    @FunctionalInterface
    public interface ItemTooltipCallback {
        void getTooltip(ItemStack stack, TooltipContext context, List<Text> lines);
    }

    public static ItemTooltipCallback registerTooltipCallback() {
//        ItemTooltipCallback.EVENT.register(
        return (ItemStack stack, TooltipContext context, List<Text> lines) -> {
            Optional<NbtCompound> optNbt = SpoornWeaponAttributesUtil.getSWANbtIfPresent(stack);

            List<Text> adds = null;

            // Rerolling
            if (stack.hasNbt() && optNbt.isEmpty()) {
                NbtCompound root = stack.getNbt();
                if (root.getBoolean(REROLL_NBT_KEY)) {
                    adds = new ArrayList<>();
                    adds.add(Text.literal(""));
                    adds.add(Text.literal("???").formatted(Formatting.AQUA));
                }
            } else if (optNbt.isPresent()) {
                NbtCompound nbt = optNbt.get();
                adds = new ArrayList<>();
                adds.add(Text.literal(""));

                for (String name : Attribute.TOOLTIPS) {
                    if (nbt.contains(name)) {
                        NbtCompound subNbt = nbt.getCompound(name);
                        switch (name) {
                            case Attribute.CRIT_NAME:
                                handleCrit(adds, subNbt);
                                break;
                            case Attribute.FIRE_NAME:
                                handleFire(adds, subNbt);
                                break;
                            case Attribute.COLD_NAME:
                                handleCold(adds, subNbt);
                                break;
                            case Attribute.LIGHTNING_NAME:
                                handleLightning(adds, subNbt);
                                break;
                            case Attribute.POISON_NAME:
                                handlePoison(adds, subNbt);
                                break;
                            case Attribute.LIFESTEAL_NAME:
                                handleLifesteal(adds, subNbt);
                                break;
                            case Attribute.EXPLOSIVE_NAME:
                                handleExplosive(adds, subNbt);
                                break;
                            default:
                                // do nothing
                        }
                    }
                }
            }

            // Upgrades
            if (stack.hasNbt()) {
                NbtCompound root = stack.getNbt();
                if (root.getBoolean(UPGRADE_NBT_KEY)) {
                    if (adds == null) {
                        adds = new ArrayList<>();
                    }
                    adds.add(Text.literal(""));
                    adds.add(Text.literal("+++").formatted(Formatting.RED));
                }
            }

            if (adds != null && adds.size() > 1) {
                // Add after the item name
                lines.addAll(1, adds);
            }
        };
    }

    private static void handleCrit(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(CRIT_CHANCE)) {
            float critChance = nbt.getFloat(CRIT_CHANCE);
            MutableText text = Text.literal(Integer.toString(Math.round(critChance * 100))).append(CRIT_TOOLTIP).setStyle(CRIT_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleFire(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = Text.literal("+" + DECIMAL_FORMAT.format(bonusDamage)).append(FIRE_TOOLTIP).setStyle(FIRE_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleCold(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = Text.literal("+" + DECIMAL_FORMAT.format(bonusDamage)).append(COLD_TOOLTIP).setStyle(COLD_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleLightning(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = Text.literal("+" + DECIMAL_FORMAT.format(bonusDamage)).append(LIGHTNING_TOOLTIP).setStyle(LIGHTNING_STYLE);
            tooltips.add(text);
        }
    }

    private static void handlePoison(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = Text.literal("+" + DECIMAL_FORMAT.format(bonusDamage)).append(POISON_TOOLTIP).setStyle(POISON_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleLifesteal(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(LIFESTEAL)) {
            float lifesteal = nbt.getFloat(LIFESTEAL);
            String lifestealStr = INTEGER_FORMAT.format(lifesteal);
            if (!LIFESTEAL_NO_TOOLTIP.equals(lifestealStr)) {
                MutableText text = Text.literal(lifestealStr).append(LIFESTEAL_TOOLTIP).setStyle(LIFESTESAL_STYLE);
                tooltips.add(text);
            }
        }
    }

    private static void handleExplosive(List<Text> tooltips, NbtCompound nbt) {
        MutableText text = EXPLOSIVE_TOOLTIP.setStyle(EXPLOSIVE_STYLE);
        tooltips.add(text);
        tooltips.add(0, EXPLOSIVE_PREPEND_TOOLTIP);
    }
}
