package phrase.towerclans.command.impl.pvp;

import org.bukkit.entity.Player;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanPvpCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (!modifiedPlayer.hasPermission(PermissionType.PVP)) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan.isPvp()) {
            clan.setPvp(false);
            Utils.sendMessage(player, Config.getCommandMessages().disablePvp());
            return true;
        } else {
            clan.setPvp(true);
            Utils.sendMessage(player, Config.getCommandMessages().enablePvp());
        }
        return true;
    }
}
