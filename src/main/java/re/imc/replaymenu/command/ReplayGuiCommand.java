package re.imc.replaymenu.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import re.imc.replaymenu.ReplayMenu;
import re.imc.replaymenu.menu.ReplayGui;

public class ReplayGuiCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender);
            new ReplayGui(player, ReplayMenu.getInstance().isUseName() ? player.getName() : player.getUniqueId().toString()).open();
        }
        return false;
    }
}
