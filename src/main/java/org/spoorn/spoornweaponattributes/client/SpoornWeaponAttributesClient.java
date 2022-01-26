package org.spoorn.spoornweaponattributes.client;

import static org.spoorn.spoornweaponattributes.util.SpoornWeaponAttributesUtil.*;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
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
public class SpoornWeaponAttributesClient implements ClientModInitializer {

    private static final Style FIRE_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16732754));
    private static final Style COLD_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(4890623));
    private static final Style CRIT_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(9851135));
    private static final Style LIGHTNING_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(15990666));
    private static final Style POISON_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(32537));
    private static final Style LIFESTESAL_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(7864320));
    private static final MutableText FIRE_TOOLTIP = new TranslatableText("swa.tooltip.firedamage");
    private static final MutableText COLD_TOOLTIP = new TranslatableText("swa.tooltip.colddamage");
    private static final MutableText CRIT_TOOLTIP = new TranslatableText("swa.tooltip.critchance");
    private static final MutableText LIGHTNING_TOOLTIP = new TranslatableText("swa.tooltip.lightningdamage");
    private static final MutableText POISON_TOOLTIP = new TranslatableText("swa.tooltip.poisondamage");
    private static final MutableText LIFESTEAL_TOOLTIP = new TranslatableText("swa.tooltip.lifesteal");
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#", SYMBOLS);
    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#", SYMBOLS);

    private static final String LIFESTEAL_NO_TOOLTIP = "0";

    @Override
    public void onInitializeClient() {
        log.info("Hello client from SpoornWeaponAttributes!");

        // Tooltip modifications
        registerTooltipCallback();
    }

    private void registerTooltipCallback() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            Optional<NbtCompound> optNbt = SpoornWeaponAttributesUtil.getSWANbtIfPresent(stack);
            if (optNbt.isPresent()) {
                NbtCompound nbt = optNbt.get();
                List<Text> adds = new ArrayList<>();
                adds.add(new LiteralText(""));

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
                            default:
                                // do nothing
                        }
                    }
                }

                if (adds.size() > 1) {
                    // Add after the item name
                    lines.addAll(1, adds);
                }
            }
        });
    }

    private void handleCrit(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(CRIT_CHANCE)) {
            float critChance = nbt.getFloat(CRIT_CHANCE);
            MutableText text = new LiteralText(Integer.toString(Math.round(critChance * 100))).append(CRIT_TOOLTIP).setStyle(CRIT_STYLE);
            tooltips.add(text);
        }
    }

    private void handleFire(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = new LiteralText("+" + DECIMAL_FORMAT.format(bonusDamage)).append(FIRE_TOOLTIP).setStyle(FIRE_STYLE);
            tooltips.add(text);
        }
    }

    private void handleCold(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = new LiteralText("+" + DECIMAL_FORMAT.format(bonusDamage)).append(COLD_TOOLTIP).setStyle(COLD_STYLE);
            tooltips.add(text);
        }
    }

    private void handleLightning(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = new LiteralText("+" + DECIMAL_FORMAT.format(bonusDamage)).append(LIGHTNING_TOOLTIP).setStyle(LIGHTNING_STYLE);
            tooltips.add(text);
        }
    }

    private void handlePoison(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_DAMAGE)) {
            float bonusDamage = nbt.getFloat(BONUS_DAMAGE);
            MutableText text = new LiteralText("+" + DECIMAL_FORMAT.format(bonusDamage)).append(POISON_TOOLTIP).setStyle(POISON_STYLE);
            tooltips.add(text);
        }
    }

    private void handleLifesteal(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(LIFESTEAL)) {
            float lifesteal = nbt.getFloat(LIFESTEAL);
            String lifestealStr = INTEGER_FORMAT.format(lifesteal);
            if (!LIFESTEAL_NO_TOOLTIP.equals(lifestealStr)) {
                MutableText text = new LiteralText(lifestealStr).append(LIFESTEAL_TOOLTIP).setStyle(LIFESTESAL_STYLE);
                tooltips.add(text);
            }
        }
    }
}
