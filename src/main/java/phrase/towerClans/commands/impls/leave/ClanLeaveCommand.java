package phrase.towerClans.commands.impls.leave;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanLeaveCommand implements CommandHandler {


    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.leave");

        if (modifiedPlayer.getClan() == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (clan.getMembers().get(modifiedPlayer).equals("Лидер")) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_cannot_leave_the_clan"));
            return true;
        }

        boolean b = clan.leave(modifiedPlayer);

        if (b) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_have_left_the_clan"));
            return true;
        } else {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
        }

        return true;
    }
}
