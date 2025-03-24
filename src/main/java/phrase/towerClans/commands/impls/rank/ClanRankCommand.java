package phrase.towerClans.commands.impls.rank;

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

import java.util.Objects;

public class ClanRankCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanRankCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.rank");

        if (args.length < 3) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        if (modifiedPlayer.getClan() == null) {
            chatUtil.sendMessage(player, configurationSection.getString("message.command.rank.you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName())) {
            chatUtil.sendMessage(player, configurationSection.getString("you_don't_have_permission"));
            return true;
        }

        String name = args[1];
        Player targetPlayer;
        try {
            targetPlayer = Objects.requireNonNull(Bukkit.getPlayer(name));
        } catch (NullPointerException e) {
            chatUtil.sendMessage(player, configurationSection.getString("the_player_is_offline"));
            return false;
        }

        ModifiedPlayer targetModifierPlayer = ModifiedPlayer.get(targetPlayer);

        int id = 0;
        try {
            id = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
        }

        ClanResponse clanResponse = clan.rank(targetModifierPlayer, id);

        if (clanResponse.isSuccess()) {
            String string = configurationSection.getString("you_have_set_the_player's_rank").replace("%player%", targetPlayer.getName()).replace("%rank%", (id == 2) ? AbstractClan.RankType.DEPUTY.getName() : AbstractClan.RankType.MEMBER.getName());
            chatUtil.sendMessage(player, string);
            string = configurationSection.getString("you_have_been_set_a_rank").replace("%player%", player.getName()).replace("%rank%", (id == 2) ? AbstractClan.RankType.DEPUTY.getName() : AbstractClan.RankType.MEMBER.getName());
            chatUtil.sendMessage(player, string);
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                chatUtil.sendMessage(player, clanResponse.getMessage());
            }
        }

        return false;
    }
}
