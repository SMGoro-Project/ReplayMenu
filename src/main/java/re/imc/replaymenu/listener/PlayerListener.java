package re.imc.replaymenu.listener;

import de.musterbukkit.replaysystem.main.ReplayAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import re.imc.replaymenu.ReplayMenu;
import re.imc.replaymenu.data.MySQLDatabase;
import re.imc.replaymenu.data.model.ReplayWaitForPlay;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CompletableFuture.supplyAsync(() -> {
            try {
                ReplayWaitForPlay data = MySQLDatabase.getReplayWaitForPlayDao().queryForId(String.valueOf(event.getPlayer().getUniqueId()));

                if (data != null) {
                    MySQLDatabase.getReplayWaitForPlayDao().delete(data);
                }
                return data;
            } catch (SQLException ignored) {

            }
            return null;
        }).thenAccept(data -> {
            if (data == null) {
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ReplayMenu.getInstance().isUseCommand()) {
                        Bukkit.dispatchCommand(event.getPlayer(), ReplayMenu.getInstance().getConfig().getString("play-command").replace("%id%", data.getReplay()));
                    } else {
                        ReplayMenu.getInstance().getXReplayHolder().playReplay(event.getPlayer(), data.getReplay());
                    }
                }
            }.runTask(ReplayMenu.getInstance());

        });

    }
}
