package phrase.towerclans.command.impl.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanBaseCommand implements CommandHandler {
    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        Location location = Base.getBase(clan);
        if (location == null) {
            Utils.sendMessage(player, Config.getCommandMessages().noBase());
            return true;
        }
        player.teleport(location);
        Utils.sendMessage(player, Config.getCommandMessages().teleportBase());
        return true;
    }
}
