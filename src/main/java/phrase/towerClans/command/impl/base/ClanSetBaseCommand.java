package phrase.towerClans.command.impl.base;

import org.bukkit.Location;
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

public class ClanSetBaseCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if(!modifiedPlayer.hasPermission(PermissionType.BASE)) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if(clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }

        Location location = player.getLocation();

        Base.setBase(clan, location);
        Utils.sendMessage(player, Config.getCommandMessages().setBase());
        return true;
    }
}
