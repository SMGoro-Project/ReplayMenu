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
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.data.ReplayDataManager;
import re.imc.xreplayextendapi.data.model.ReplayHistory;
import re.imc.xreplayextendapi.data.model.ReplayIndex;
import re.imc.xreplayextendapi.data.model.ReplayWaitForPlay;

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
                ReplayDataManager manager = XReplayExtendAPI.getInstance().getReplayDataManager();
                ReplayHistory history = manager.getReplayHistoryDao().queryForId(ReplayMenu.getInstance().isUseName() ? player.getName() : player.getUniqueId().toString());
                return new ArrayList<>(manager.getReplayIndexDao().queryBuilder().where().in("REPLAYID", Arrays.asList(history.replays())).query());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }).thenAccept(list -> {

            list.sort(Comparator.comparingLong(index-> Long.parseLong("-" + index.time())));

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
                Bukkit.dispatchCommand(player, ReplayMenu.getInstance().getConfig().getString("play-command").replace("%id%", index.replayId()));
            } else {
                ReplayMenu.getInstance().getXReplayHolder().playReplay(player, index.replayId());
            }
        }
        CompletableFuture.runAsync(() -> {
            try {
                ReplayDataManager manager = XReplayExtendAPI.getInstance().getReplayDataManager();

                manager.getReplayWaitForPlayDao().createOrUpdate(new ReplayWaitForPlay(player.getUniqueId().toString(), index.replayId()));

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
