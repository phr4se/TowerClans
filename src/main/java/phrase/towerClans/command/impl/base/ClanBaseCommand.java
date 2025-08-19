package phrase.towerClans.command.impl.base;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.clan.permission.PermissionType;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;


public class ClanBaseCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if(clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }

        Location location = Base.getBase(clan);

        if(location == null) {
            Utils.sendMessage(player, Config.getCommandMessages().noBase());
            return true;
        }

        player.teleport(location);
        Utils.sendMessage(player, Config.getCommandMessages().teleportBase());
        return true;
    }

}
