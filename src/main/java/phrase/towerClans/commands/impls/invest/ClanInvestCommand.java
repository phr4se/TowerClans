package phrase.towerClans.commands.impls.invest;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanInvestCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invest");

        if (args.length < 2) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        if (modifiedPlayer.getClan() == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        boolean success = clan.invest(modifiedPlayer, amount);
        if (success) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_put_it_in_the_clan"));
            return true;
        } else {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_don't_have_enough"));
        }

        return true;
    }
}
