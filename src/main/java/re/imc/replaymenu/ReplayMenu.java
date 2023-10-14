package re.imc.replaymenu;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import re.imc.replaymenu.command.ReplayGuiCommand;
import re.imc.replaymenu.data.MySQLDatabase;
import re.imc.replaymenu.holder.XReplayHolder;
import re.imc.replaymenu.listener.PlayerListener;

import java.sql.SQLException;

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
    private XReplayHolder xReplayHolder;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        useName = getConfig().getBoolean("use-name-instead-of-uuid", false);
        isReplayServer = getConfig().getBoolean("is-replay-server", false);
        useCommand = getConfig().getBoolean("use-command-play");

        try {
            MySQLDatabase.load(getConfig().getConfigurationSection("database"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (isReplayServer) {
            xReplayHolder = new XReplayHolder();
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        }
        if (Bukkit.getPluginManager().getPlugin("helper") != null) {
            Bukkit.getPluginCommand("replaygui").setExecutor(new ReplayGuiCommand());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
