package phrase.towerClans.command.impl.invite;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;

public class ClanInviteCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanInviteCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }


    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.invite");

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

        if(player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            chatUtil.sendMessage(player, configurationSection.getString("you_can't_invite_yourself"));
            return true;
        }

        PlayerCalls.addPlayers(targetPlayer.getUniqueId(), player.getUniqueId());
        chatUtil.sendMessage(player, configurationSection.getString("you_have_sent_a_request_to_join_the_clan"));
        chatUtil.sendMessage(targetPlayer, configurationSection.getString("you_have_received_a_request_to_join_the_clan").replace("%clan_name%", ((ClanImpl)modifiedPlayer.getClan()).getName()));

        return true;
    }


}
