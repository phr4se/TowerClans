package phrase.towerClans.commands.impls.create;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanCreateCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.create");

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (args.length < 2) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        if (modifiedPlayer.getClan() != null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_are_in_a_clan"));
            return true;
        }

        configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("settings");

        int amount = configurationSection.getInt("the_cost_of_creating_a_clan");

        configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.create");

        if (ClanImpl.getClans().containsKey(args[1])) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("a_clan_with_that_name_already_exists"));
            return true;
        }

        if (Plugin.getInstance().economy.getBalance(player) < amount) {
            String string = configurationSection.getString("you_don't_have_enough").replace("%amount%", String.valueOf(amount - (int)Plugin.getInstance().economy.getBalance(player)));
            ChatUtil.getChatUtil().sendMessage(player, string);
            return true;
        }


        Plugin.getInstance().economy.withdrawPlayer(player, amount);
        String name = args[1];

        ClanImpl clan = new ClanImpl(name);
        modifiedPlayer.setClan(clan);
        clan.getMembers().put(modifiedPlayer, AbstractClan.RankType.LEADER.getName());
        ClanImpl.getClans().put(args[1], clan);

        ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_have_created_a_clan"));

        return true;
    }
}
