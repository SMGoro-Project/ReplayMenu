package re.imc.replaymenu.menu;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;
import re.imc.replaymenu.ReplayMenu;
import re.imc.replaymenu.config.ReplayGuiConfig;
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.data.ReplayDataManager;
import re.imc.xreplayextendapi.data.model.ReplayHistory;
import re.imc.xreplayextendapi.data.model.ReplayIndex;
import re.imc.xreplayextendapi.data.model.ReplayWaitForPlay;
import re.imc.xreplayextendapi.spigot.SpigotPlugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReplayGui extends ConfigurableGui {
    private final PaginationManager gamesPagination = new PaginationManager(this);

    private final PaginationManager replaysPagination = new PaginationManager(this);

    private String currentGameType = "";

    private final String userId;
    public ReplayGui(Player player, String replayUserId) {
        super(player, "replay-gui");
        this.userId = replayUserId;
    }


    @Override
    public void onOpen(InventoryOpenEvent event) {
        gamesPagination.registerPageSlotsBetween(1, 7);
        replaysPagination.registerPageSlotsBetween(18, getSize() - 10);

        ConfigurationSection gameTypes = ReplayMenu.getInstance().getConfig().getConfigurationSection("gui.game-types");
        for (String key : gameTypes.getKeys(false)) {
            ConfigurationSection section = gameTypes.getConfigurationSection(key);
            gamesPagination.addItem(new Icon(ItemStackSerializer.deserializeItemStack(section.getConfigurationSection("item"))).onClick(click -> {
                currentGameType = section.getString("server-name");
                System.out.println(currentGameType);
                addItem(13, click.getCurrentItem());
                updateNewReplays();
            }));
        }

        putDysfunctionalIcons("games-previous-page",
                "games-next-page",
                "replays-previous-page",
                "replays-next-page");


        updateButtons();
        calculateAndUpdateReplaysPagination();
    }

    private void updateButtons() {

        PlaceholderUtil pagePlaceholder = new PlaceholderUtil();
        pagePlaceholder.add("%page%", String.valueOf(gamesPagination.getCurrentPage() + 1));
        pagePlaceholder.add("%maxPage%", String.valueOf(gamesPagination.getLastPage() + 1));

        if (gamesPagination.getCurrentPage() != 0) {
            addConfigIcon("games-previous-page", pagePlaceholder).onClick(e -> {
                gamesPagination.goPreviousPage();
                updateButtons();
            });
        } else {
            addConfigIcon("games-previous-page-end", pagePlaceholder);
        }
        pagePlaceholder = new PlaceholderUtil();
        pagePlaceholder.add("%page%", String.valueOf(replaysPagination.getCurrentPage() + 1));
        pagePlaceholder.add("%maxPage%", String.valueOf(replaysPagination.getLastPage() + 1));

        if (!gamesPagination.isLastPage()) {
            System.out.println("last page" + replaysPagination.getLastPage());
            addConfigIcon("games-next-page", pagePlaceholder).onClick(e -> {
                gamesPagination.goNextPage();
                updateButtons();
            });
        } else {
            addConfigIcon("games-next-page-end", pagePlaceholder);
        }

        if (replaysPagination.getCurrentPage() != 0) {
            addConfigIcon("replays-previous-page", pagePlaceholder).onClick(e -> {
                replaysPagination.goPreviousPage();
                calculateAndUpdateReplaysPagination();
                updateButtons();
            });
        } else {
            addConfigIcon("replays-previous-page-end", pagePlaceholder);
        }
        if (!replaysPagination.isLastPage() ) {
            addConfigIcon("replays-next-page", pagePlaceholder).onClick(e -> {
                replaysPagination.goNextPage();
                calculateAndUpdateReplaysPagination();
                updateButtons();
            });
        } else {
            addConfigIcon("replays-next-page-end", pagePlaceholder);
        }

        gamesPagination.update();
    }

    private void calculateAndUpdateReplaysPagination() {
        replaysPagination.update();
    }


    private void updateNewReplays() {
            CompletableFuture.supplyAsync(() -> {
                try {
                    ReplayDataManager manager = XReplayExtendAPI.getInstance().getReplayDataManager();
                    if (userId == null) {
                        System.out.println("ALL");
                        return manager.getReplayIndexDao().queryBuilder().orderBy("TIME", false).limit((long) ReplayMenu.getInstance().getMaxAllReplayAmount()).where().eq("REPLAYNAME", currentGameType).query();
                    }
                    ReplayHistory history = manager.getReplayHistoryDao().queryForId(userId);
                    System.out.println(history.uuid());
                    System.out.println(Arrays.toString(history.replays()));
                    return manager.getReplayIndexDao().queryBuilder().orderBy("TIME", false).limit((long) ReplayMenu.getInstance().getMaxPlayerReplayAmount()).where().eq("REPLAYNAME", currentGameType).and().in("REPLAYID", Arrays.asList(history.replays())).query();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }).whenCompleteAsync((list, e) -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        replaysPagination.getItems().clear();
                        for (ReplayIndex index : list) {
                            replaysPagination.addItem(new Icon(getConfigItem("replay", ReplayGuiConfig.replayPlaceholderUtil(index))).onClick(click -> {
                                playReplay(player, index);
                            }));
                        }

                        replaysPagination.goFirstPage();
                        replaysPagination.update();
                        updateButtons();
                    }
                }.runTask(ReplayMenu.getInstance());
            });

            gamesPagination.update();

    }


    public static void playReplay(Player player, ReplayIndex index) {
        if (ReplayMenu.getInstance().isReplayServer()) {
            SpigotPlugin.getInstance().getReplayAction().accept(player, index.replayId());
            return;
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
                return;
            }
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(ReplayMenu.getInstance().getConfig().getString("replay-server"));
            player.sendPluginMessage(ReplayMenu.getInstance(), "BungeeCord", out.toByteArray());
        });
    }
}
