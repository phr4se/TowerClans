package phrase.towerClans.command.impl.pvp;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.clan.permission.PermissionType;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

public class ClanPvpCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(!modifiedPlayer.hasPermission(PermissionType.PVP)) {
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
