package phrase.towerClans.command.impl.stats;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.attribute.player.Stats;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;

import java.util.List;

public class ClanStatsCommand implements CommandHandler {
    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanStatsCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.stats");

        if(clan == null) {
            chatUtil.sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        if (args.length < 2) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);

        ModifiedPlayer targetModifiedPlayer = ModifiedPlayer.get(targetPlayer);

        if(targetModifiedPlayer == null) {
            chatUtil.sendMessage(player, configurationSection.getString("the_player_is_offline"));
            return true;
        }

        ClanImpl targetClan = (ClanImpl) targetModifiedPlayer.getClan();

        if (targetClan == null) {
            chatUtil.sendMessage(player, configurationSection.getString("the_player_is_not_in_the_clan"));
            return true;
        }

        if (!clan.getName().equals(targetClan.getName())) {
            chatUtil.sendMessage(player, configurationSection.getString("the_player_is_not_in_your_clan"));
            return true;
        }

        Stats playerStats = Stats.PLAYERS.get(targetPlayer.getUniqueId());
        int kills = playerStats.getKills();
        int deaths = playerStats.getDeaths();

        List<String> list = configurationSection.getStringList("player_statistics");

        for (String string : list) {
            chatUtil.sendMessage(player, string.replace("%kills%", String.valueOf(kills)).replace("%deaths%", String.valueOf(deaths)).replace("%player%", targetPlayer.getName()));
        }

        return true;
    }

}
