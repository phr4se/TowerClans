package phrase.towerclans.command.impl.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanSetBaseCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (!modifiedPlayer.hasPermission(PermissionType.BASE)) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        Location location = player.getLocation();
        Base.setBase(clan, location);
        Utils.sendMessage(player, Config.getCommandMessages().setBase());
        return true;
    }
}
