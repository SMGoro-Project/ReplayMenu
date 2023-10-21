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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {

            Player player = ((Player) commandSender);
            if (args.length > 0) {
                String id = args[0];
                if (id.equalsIgnoreCase("all") && commandSender.hasPermission("replay.gui.all")) {
                    new ReplayGui(player, null).open();
                    return true;
                } else if (commandSender.hasPermission("replay.gui.other")) {
                    new ReplayGui(player, id).open();
                    return true;
                }
            }
            new ReplayGui(player, ReplayMenu.getInstance().isUseName() ? player.getName() : player.getUniqueId().toString()).open();
        }
        return true;
    }
}
