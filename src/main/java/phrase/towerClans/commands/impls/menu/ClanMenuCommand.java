package phrase.towerClans.commands.impls.menu;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanMenuCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.menu");

        if (modifiedPlayer.getClan() == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        clan.showMenu(modifiedPlayer, ClanImpl.MenuType.MENU_CLAN.getId());
        ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_have_opened_the_clan_menu"));

        return true;
    }
}
