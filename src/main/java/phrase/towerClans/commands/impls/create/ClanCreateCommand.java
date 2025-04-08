package phrase.towerClans.commands.impls.create;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.commands.impls.base.Base;
import phrase.towerClans.utils.ChatUtil;

import java.util.List;

public class ClanCreateCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanCreateCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.create");

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (args.length < 2) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        if (modifiedPlayer.getClan() != null) {
            chatUtil.sendMessage(player, configurationSection.getString("you_are_in_a_clan"));
            return true;
        }

        configurationSection = plugin.getConfig().getConfigurationSection("settings.clan_name");
        int min = configurationSection.getInt("min");
        int max = configurationSection.getInt("max");
        String name = args[1];
        if(!(name.length() < max && name.length() > min)) {
            chatUtil.sendMessage(player, (configurationSection.getString("message_about_exceeding_the_limits")).replace("%min%", String.valueOf(min)).replace("%max%", String.valueOf(max)));
            return true;
        }

        List<String> badWords = configurationSection.getStringList("bad_words");

        for(String badWord : badWords) {
            if(!(badWord.equals(name))) continue;

            chatUtil.sendMessage(player, configurationSection.getString("the_clan_name_is_on_the_list_of_bad_words"));
            return true;
        }


        configurationSection = plugin.getConfig().getConfigurationSection("settings");
        int amount = configurationSection.getInt("the_cost_of_creating_a_clan");

        configurationSection = plugin.getConfig().getConfigurationSection("message.command.create");

        if (ClanImpl.getClans().containsKey(name)) {
            chatUtil.sendMessage(player, configurationSection.getString("a_clan_with_that_name_already_exists"));
            return true;
        }

        if (plugin.economy.getBalance(player) < amount) {
            String string = configurationSection.getString("you_don't_have_enough").replace("%amount%", String.valueOf(amount - (int)plugin.economy.getBalance(player)));
            chatUtil.sendMessage(player, string);
            return true;
        }


        plugin.economy.withdrawPlayer(player, amount);

        ClanImpl clan = new ClanImpl(name, plugin);
        modifiedPlayer.setClan(clan);
        clan.getMembers().put(modifiedPlayer, AbstractClan.RankType.LEADER.getName());
        ClanImpl.getClans().put(args[1], clan);
        Base.setBase(clan, null);

        chatUtil.sendMessage(player, configurationSection.getString("you_have_created_a_clan"));

        return true;
    }
}
