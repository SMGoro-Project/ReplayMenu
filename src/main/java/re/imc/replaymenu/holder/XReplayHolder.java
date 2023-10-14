package re.imc.replaymenu.holder;

import de.musterbukkit.replaysystem.main.ReplayAPI;
import lombok.Getter;
import org.bukkit.entity.Player;

public class XReplayHolder {

    public void playReplay(Player player, String id) {
        ReplayAPI.playReplayID(id, player);
    };

}
