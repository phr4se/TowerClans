package phrase.towerClans.commands.impls.pvp;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanPvpCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.pvp");

        if (modifiedPlayer.getClan() == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_don't_have_permission"));
            return true;
        }

        if (clan.isPvp()) {
            clan.setPvp(false);
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_have_disabled_pvp_in_the_clan"));
            return true;
        } else {
            clan.setPvp(true);
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_have_enabled_pvp_in_the_clan"));
        }

        return true;
    }
}
