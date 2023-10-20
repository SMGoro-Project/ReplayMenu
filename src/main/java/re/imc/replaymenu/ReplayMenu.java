package re.imc.replaymenu;

import com.google.common.cache.Cache;
import lombok.Getter;
import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.inventory.configurable.ConfigurableGuiCache;
import mc.obliviate.inventory.configurable.GuiConfigurationTable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import re.imc.replaymenu.command.ReplayGuiCommand;
import re.imc.xreplayextendapi.spigot.SpigotPlugin;

public final class ReplayMenu extends JavaPlugin {


    @Getter
    private static ReplayMenu instance;

    @Getter
    private boolean useName;
    @Getter
    private boolean isReplayServer;
    @Getter
    private boolean useCommand;
    @Getter
    private InventoryAPI inventoryAPI;
    @Getter
    private int maxPlayerReplayAmount = 50;
    @Getter
    private int maxAllReplayAmount = 100;

    @Override
    public void onEnable() {
        inventoryAPI = new InventoryAPI(this);
        ConfigurableGuiCache.resetCaches(); //not obligatory but recommended at configuration reload methods.
        GuiConfigurationTable.setDefaultConfigurationTable(new GuiConfigurationTable(getConfig().getConfigurationSection("gui")));

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        inventoryAPI.init();
        saveDefaultConfig();
        instance = this;
        useName = getConfig().getBoolean("use-name-instead-of-uuid", false);
        isReplayServer = getConfig().getBoolean("is-replay-server", false);
        useCommand = getConfig().getBoolean("use-command-play");
        maxPlayerReplayAmount = getConfig().getInt("max-replay-amount.player");
        maxAllReplayAmount = getConfig().getInt("max-replay-amount.all");
        if (Bukkit.getPluginManager().getPlugin("helper") != null) {
            Bukkit.getPluginCommand("replaygui").setExecutor(new ReplayGuiCommand());
        }

        SpigotPlugin.getInstance().setReplayAction((player, id) -> {
            if (isUseCommand()) {
                Bukkit.dispatchCommand(player, ReplayMenu.getInstance().getConfig().getString("play-command").replace("%id%", id));
            } else {
                SpigotPlugin.getInstance().getXReplayHolder().playReplay(player, id);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
