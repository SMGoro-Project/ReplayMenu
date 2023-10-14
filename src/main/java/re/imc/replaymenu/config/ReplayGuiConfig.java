package re.imc.replaymenu.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import re.imc.replaymenu.data.model.ReplayIndex;

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
        string = string.replace("%id%", index.getReplayId());
        string = string.replace("%name%", index.getReplayName());
        string = string.replace("%type%", index.getReplayType());
        string = string.replace("%version%", String.valueOf(index.getVersion()));
        string = string.replace("%chunkLoc%", index.getChunkLoc());

        Timestamp stamp = new Timestamp(Long.parseLong(index.getTime()));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        string = string.replace("%time%", format.format(date));

        string = string.replace("%length%", ticksToTimeFormat(index.getLength()));
        string = string.replace("%importantLevel%", String.valueOf(index.getImportantLevel()));
        string = string.replace("%lastView%", index.getLastView());
        string = string.replace("%lastViewedBy%", index.getLastViewedBy());
        string = string.replace("%views%", String.valueOf(index.getViews()));
        string = string.replace("%storage%", index.getStorage().substring(9));

        return string;
    }
}
