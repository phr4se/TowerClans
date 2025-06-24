package phrase.towerClans.command.impl.invite;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

public class ClanInviteCommand implements CommandHandler {

    private final Plugin plugin;

    public ClanInviteCommand(Plugin plugin) {
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
            Utils.sendMessage(player, Config.getCommandMessages().playerNotFound());
            return true;
        }

        if(player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            Utils.sendMessage(player, Config.getCommandMessages().noInviteYourself());
            return true;
        }

        PlayerCalls.addPlayers(targetPlayer.getUniqueId(), player.getUniqueId());
        Utils.sendMessage(player, Config.getCommandMessages().inviteInClan());
        Utils.sendMessage(targetPlayer, Config.getCommandMessages().invitedInClan().replace("%clan_name%", ((ClanImpl)modifiedPlayer.getClan()).getName()));

        return true;
    }


}
