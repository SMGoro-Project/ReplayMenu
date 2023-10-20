package re.imc.replaymenu.config;

import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import re.imc.xreplayextendapi.data.model.ReplayIndex;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReplayGuiConfig {

    public static ItemStack createItemStackFromConfiguration(ConfigurationSection itemConfig, ReplayIndex index) {

        ItemStack item = ItemStackSerializer.deserializeItemStack(itemConfig);
        ItemStackSerializer.applyPlaceholdersToItemStack(item, replayPlaceholderUtil(index));

        return item;
    }

    public static String ticksToTimeFormat(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        int remainingSeconds = seconds % 60;
        int remainingMinutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, remainingMinutes, remainingSeconds);
    }
    public static PlaceholderUtil replayPlaceholderUtil(ReplayIndex index) {
        PlaceholderUtil placeholderUtil = new PlaceholderUtil();

        placeholderUtil.add("%id%", index.replayId());
        placeholderUtil.add("%name%", index.replayName());
        placeholderUtil.add("%type%", index.replayType());
        placeholderUtil.add("%version%", String.valueOf(index.version()));
        placeholderUtil.add("%chunkLoc%", index.chunkLoc());

        Timestamp stamp = new Timestamp(Long.parseLong(index.time()));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        placeholderUtil.add("%time%", format.format(date));

        placeholderUtil.add("%length%", ticksToTimeFormat(index.length()));
        placeholderUtil.add("%importantLevel%", String.valueOf(index.importantLevel()));
        placeholderUtil.add("%lastView%", index.lastView());
        placeholderUtil.add("%lastViewedBy%", index.lastViewedBy());
        placeholderUtil.add("%views%", String.valueOf(index.views()));
        placeholderUtil.add("%storage%", index.storage().substring(9));

        return placeholderUtil;
    }
}
