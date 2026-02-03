package phrase.towerclans.command.impl.storage;

import org.bukkit.entity.Player;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.gui.MenuType;
import phrase.towerclans.util.Utils;

public class ClanStorageCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (!modifiedPlayer.hasPermission(PermissionType.STORAGE)) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        clan.showMenu(modifiedPlayer, MenuType.MENU_CLAN_STORAGE);
        Utils.sendMessage(player, Config.getCommandMessages().openStorage());
        clan.getStorageManager().getPlayers().add(player.getUniqueId());
        return true;
    }
}
