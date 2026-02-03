package phrase.towerclans.command.impl.leave;

import org.bukkit.entity.Player;
import phrase.towerclans.Plugin;
import phrase.towerclans.clan.ClanResponse;
import phrase.towerclans.clan.attribute.clan.RankManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.event.LeaveEvent;
import phrase.towerclans.util.Utils;

public class ClanLeaveCommand implements CommandHandler {
    private final Plugin plugin;

    public ClanLeaveCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan.getMembers().get(modifiedPlayer).equals(RankManager.RankType.LEADER.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().notLeaveWithClan());
            return true;
        }
        ClanResponse clanResponse = clan.leave(modifiedPlayer);
        if (clanResponse.isSuccess()) {
            Utils.sendMessage(player, Config.getCommandMessages().leaveWithClan());
            plugin.getServer().getPluginManager().callEvent(new LeaveEvent(clan, modifiedPlayer));
            return true;
        } else {
            if (clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }
        }
        return true;
    }
}
