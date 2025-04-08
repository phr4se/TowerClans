package phrase.towerClans.commands.impls.base;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.Clan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanSetBaseCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanSetBaseCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.base.setbase");
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if(clan == null) {
            chatUtil.sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        Location location = player.getLocation();

        if(!(clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()))) {
            chatUtil.sendMessage(player, configurationSection.getString("you_are_not_a_leader"));
            return true;
        }

        Base.setBase(clan, location);
        chatUtil.sendMessage(player, configurationSection.getString("have_you_established_a_clan_base"));
        return true;
    }
}
