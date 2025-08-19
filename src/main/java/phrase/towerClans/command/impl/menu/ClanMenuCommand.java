package phrase.towerClans.command.impl.menu;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.gui.MenuType;
import phrase.towerClans.util.Utils;

public class ClanMenuCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN_MAIN);
        Utils.sendMessage(player, Config.getCommandMessages().openClanMenu());

        return true;
    }
}
