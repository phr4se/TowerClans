package phrase.towerClans.command.impl.leave;

import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.event.LeaveEvent;
import phrase.towerClans.util.Utils;

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

        if (clan.getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().notLeaveWithClan());
            return true;
        }

        ClanResponse clanResponse = clan.leave(modifiedPlayer);

        if (clanResponse.isSuccess()) {
            Utils.sendMessage(player, Config.getCommandMessages().leaveWithClan());
            plugin.getServer().getPluginManager().callEvent(new LeaveEvent(clan, modifiedPlayer));
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }
        }

        return true;
    }
}
