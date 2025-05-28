package phrase.towerClans.command.impl.kick;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.event.LeaveEvent;
import phrase.towerClans.util.ChatUtil;

public class ClanKickCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanKickCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.kick");

        if (args.length < 2) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        if (modifiedPlayer.getClan() == null) {
            chatUtil.sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (!clan.getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(Rank.RankType.DEPUTY.getName())) {
            chatUtil.sendMessage(player, configurationSection.getString("you_don't_have_permission"));
            return true;
        }

        String name = args[1];

        Player targetPlayer = Bukkit.getPlayer(name);

        if (targetPlayer == null) {
            chatUtil.sendMessage(player, configurationSection.getString("the_player_was_not_found"));
            return true;
        }

        ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);

        if(player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            chatUtil.sendMessage(player, configurationSection.getString("you_can't_kick_yourself"));
            return true;
        }

        if (clan.getMembers().get(targetModifiedPlayer).equals(Rank.RankType.LEADER.getName())) {
            chatUtil.sendMessage(player, configurationSection.getString("you_cannot_leave_the_clan"));
            return true;
        }

        ClanResponse clanResponse = clan.kick(targetModifiedPlayer);

        if (clanResponse.isSuccess()) {
            chatUtil.sendMessage(player, configurationSection.getString("you_kicked_a_player_from_the_clan"));
            chatUtil.sendMessage(targetPlayer, configurationSection.getString("you_were_kicked_out_of_the_clan"));
            plugin.getServer().getPluginManager().callEvent(new LeaveEvent(clan, targetModifiedPlayer));
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                chatUtil.sendMessage(player, clanResponse.getMessage());
            }

        }

        return true;
    }
}
