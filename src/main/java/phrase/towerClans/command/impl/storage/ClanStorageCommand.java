package phrase.towerClans.command.impl.storage;

import org.bukkit.entity.Player;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.clan.permission.PermissionType;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.gui.MenuType;
import phrase.towerClans.util.Utils;


public class ClanStorageCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(!modifiedPlayer.hasPermission(PermissionType.STORAGE)) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if(clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }

        clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN_STORAGE);
        Utils.sendMessage(player, Config.getCommandMessages().openStorage());
        clan.getStorage().getPlayers().add(player.getUniqueId());
        return true;
    }
}
