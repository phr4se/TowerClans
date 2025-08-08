package phrase.towerClans.command.impl.kick;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
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

public class ClanKickCommand implements CommandHandler {

    private final Plugin plugin;

    public ClanKickCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }

        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (!clan.getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(Rank.RankType.DEPUTY.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }

        String name = args[1];

        Player targetPlayer = Bukkit.getPlayer(name);

        if (targetPlayer == null) {
            OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(name);

            ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetOfflinePlayer);

            if(!clan.getMembers().containsKey(targetModifiedPlayer)) {
                Utils.sendMessage(player, Config.getCommandMessages().playerNotInYourselfClan());
                return true;
            }

            if (clan.getMembers().get(targetModifiedPlayer).equals(Rank.RankType.LEADER.getName())) {
                Utils.sendMessage(player, Config.getCommandMessages().notLeaveWithClan());
                return true;
            }

            ClanResponse clanResponse = clan.kick(targetModifiedPlayer);

            if (clanResponse.isSuccess()) {
                Utils.sendMessage(player, Config.getCommandMessages().kickPlayerWithClan());
                return true;
            } else {
                if(clanResponse.getMessage() != null) {
                    Utils.sendMessage(player, clanResponse.getMessage());
                }

            }
            return true;
        }

        ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);

        if(player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            Utils.sendMessage(player, Config.getCommandMessages().notKickYourself());
            return true;
        }

        if(!clan.getMembers().containsKey(targetModifiedPlayer)) {
            Utils.sendMessage(player, Config.getCommandMessages().playerNotInYourselfClan());
            return true;
        }

        if (clan.getMembers().get(targetModifiedPlayer).equals(Rank.RankType.LEADER.getName())) {
            Utils.sendMessage(player, Config.getCommandMessages().notLeaveWithClan());
            return true;
        }

        ClanResponse clanResponse = clan.kick(targetModifiedPlayer);

        if (clanResponse.isSuccess()) {
            Utils.sendMessage(player, Config.getCommandMessages().kickPlayerWithClan());
            Utils.sendMessage(targetPlayer, Config.getCommandMessages().kickedWithClan());
            plugin.getServer().getPluginManager().callEvent(new LeaveEvent(clan, targetModifiedPlayer));
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                Utils.sendMessage(player, clanResponse.getMessage());
            }

        }

        return true;
    }
}
