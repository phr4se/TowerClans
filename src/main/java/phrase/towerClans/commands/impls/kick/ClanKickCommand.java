package phrase.towerClans.commands.impls.kick;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

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

        if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
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

        if (clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName())) {
            chatUtil.sendMessage(player, configurationSection.getString("you_cannot_leave_the_clan"));
            return true;
        }

        ClanResponse clanResponse = clan.kick(targetModifiedPlayer);

        if (clanResponse.isSuccess()) {
            chatUtil.sendMessage(player, configurationSection.getString("you_kicked_a_player_from_the_clan"));
            chatUtil.sendMessage(targetPlayer, configurationSection.getString("you_were_kicked_out_of_the_clan"));
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                chatUtil.sendMessage(player, clanResponse.getMessage());
            }

        }

        return true;
    }
}
