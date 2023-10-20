package re.imc.replaymenu;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import re.imc.replaymenu.command.ReplayGuiCommand;
import re.imc.replaymenu.holder.XReplayHolder;
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
    private XReplayHolder xReplayHolder;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        useName = getConfig().getBoolean("use-name-instead-of-uuid", false);
        isReplayServer = getConfig().getBoolean("is-replay-server", false);
        useCommand = getConfig().getBoolean("use-command-play");

        if (Bukkit.getPluginManager().getPlugin("helper") != null) {
            Bukkit.getPluginCommand("replaygui").setExecutor(new ReplayGuiCommand());
        }

        SpigotPlugin.getInstance().setReplayAction((player, id) -> {
            if (isUseCommand()) {
                Bukkit.dispatchCommand(player, ReplayMenu.getInstance().getConfig().getString("play-command").replace("%id%", id));
            } else {
                ReplayMenu.getInstance().getXReplayHolder().playReplay(player, id);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
