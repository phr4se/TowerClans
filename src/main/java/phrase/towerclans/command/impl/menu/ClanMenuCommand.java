package phrase.towerclans.command.impl.menu;

import org.bukkit.entity.Player;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.gui.MenuType;
import phrase.towerclans.util.Utils;

public class ClanMenuCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        modifiedPlayer.showMenu(MenuType.MENU_CLAN_MAIN);
        Utils.sendMessage(player, Config.getCommandMessages().openClanMenu());
        return true;
    }
}
