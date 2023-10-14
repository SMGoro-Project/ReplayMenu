package re.imc.replaymenu.menu;

import me.lucko.helper.Helper;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PaginatedGuiBuilder;
import me.lucko.helper.messaging.bungee.BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import re.imc.replaymenu.ReplayMenu;
import re.imc.replaymenu.config.ReplayGuiConfig;
import re.imc.replaymenu.data.MySQLDatabase;
import re.imc.replaymenu.data.model.ReplayHistory;
import re.imc.replaymenu.data.model.ReplayIndex;
import re.imc.replaymenu.data.model.ReplayWaitForPlay;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReplayGui {


    //TODO Favourite Replays
    public static void open(Player player, ConfigurationSection section) {

        CompletableFuture.supplyAsync(() -> {
            try {
                 ReplayHistory history = MySQLDatabase.getReplayHistoryDao().queryForId(ReplayMenu.getInstance().isUseName() ? player.getName() : player.getUniqueId().toString());
                 return new ArrayList<>(MySQLDatabase.getReplayIndexDao().queryBuilder().where().in("REPLAYID", Arrays.asList(history.replays().split(","))).query());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }).thenAccept(list -> {

            list.sort(Comparator.comparingLong(index-> Long.parseLong("-" + index.getTime())));

            PaginatedGuiBuilder builder = PaginatedGuiBuilder.create()
                    .title(section.getString("title"));

            builder.previousPageSlot(49);
            builder.nextPageSlot(51);
            builder.nextPageItem((pageInfo) -> ItemStackBuilder.of(Material.ARROW).name(section.getString("next-page")).build());
            builder.previousPageItem((pageInfo) -> ItemStackBuilder.of(Material.ARROW).name(section.getString("previous-page")).build());

            builder.build(player, paginatedGui -> {
                List<Item> items = new ArrayList<>();

                for (ReplayIndex index : list) {
                    items.add(ItemStackBuilder.of(ReplayGuiConfig.createItemStackFromConfiguration(section.getConfigurationSection("replay-item"), index)
                    ).build(() -> {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                playReplay(player, index);
                            }
                        }.runTask(ReplayMenu.getInstance());

                    }));
                }
                return items;
            }).open();

        });
    }

    public static void playReplay(Player player, ReplayIndex index) {
        if (ReplayMenu.getInstance().isReplayServer()) {
            if (ReplayMenu.getInstance().isUseCommand()) {
                Bukkit.dispatchCommand(player, ReplayMenu.getInstance().getConfig().getString("play-command").replace("%id%", index.getReplayId()));
            } else {
                ReplayMenu.getInstance().getXReplayHolder().playReplay(player, index.getReplayId());
            }
        }
        CompletableFuture.runAsync(() -> {
            try {
                MySQLDatabase.getReplayWaitForPlayDao().createOrUpdate(new ReplayWaitForPlay(player.getUniqueId().toString(), index.getReplayId()));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync((v,t) -> {
            if (t != null) {
                t.printStackTrace();
                return;
            }
            Helper.service(BungeeCord.class).ifPresent(bc -> bc.connect(player, ReplayMenu.getInstance().getConfig().getString("replay-server")));
        });
    }
}
