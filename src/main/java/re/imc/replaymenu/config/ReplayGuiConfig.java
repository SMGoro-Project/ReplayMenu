package re.imc.replaymenu.config;

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
        String type = itemConfig.getString("type");
        String displayName = ChatColor.translateAlternateColorCodes('&', replacePlaceholders(itemConfig.getString("item-name"), index));

        ItemStack itemStack = new ItemStack(Material.matchMaterial(type));
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(displayName);

        if (itemConfig.contains("lore")) {
            List<String> lore = itemConfig.getStringList("lore");
            List<String> translatedLore = new ArrayList<>();
            for (String line : lore) {
                translatedLore.add(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(line, index)));
            }
            itemMeta.setLore(translatedLore);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static String ticksToTimeFormat(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        int remainingSeconds = seconds % 60;
        int remainingMinutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, remainingMinutes, remainingSeconds);
    }
    public static String replacePlaceholders(String string, ReplayIndex index) {
        string = string.replace("%id%", index.replayId());
        string = string.replace("%name%", index.replayName());
        string = string.replace("%type%", index.replayType());
        string = string.replace("%version%", String.valueOf(index.version()));
        string = string.replace("%chunkLoc%", index.chunkLoc());

        Timestamp stamp = new Timestamp(Long.parseLong(index.time()));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        string = string.replace("%time%", format.format(date));

        string = string.replace("%length%", ticksToTimeFormat(index.length()));
        string = string.replace("%importantLevel%", String.valueOf(index.importantLevel()));
        string = string.replace("%lastView%", index.lastView());
        string = string.replace("%lastViewedBy%", index.lastViewedBy());
        string = string.replace("%views%", String.valueOf(index.views()));
        string = string.replace("%storage%", index.storage().substring(9));

        return string;
    }
}
