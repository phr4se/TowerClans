package phrase.towerClans.command.impl.base;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;

public class ClanDelBaseCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanDelBaseCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.base.delbase");
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if(clan == null) {
            chatUtil.sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        Location location = Base.getBase(clan);

        if(location == null) {
            chatUtil.sendMessage(player, configurationSection.getString("the_clan_doesn't_have_a_base"));
            return true;
        }

        if(!(clan.getMembers().get(modifiedPlayer).equals(Rank.RankType.LEADER.getName()))) {
            chatUtil.sendMessage(player, configurationSection.getString("you_are_not_a_leader"));
            return true;
        }

        Base.setBase(clan, null);
        chatUtil.sendMessage(player, configurationSection.getString("you_have_deleted_the_clan's_database"));
        return true;
    }
}
