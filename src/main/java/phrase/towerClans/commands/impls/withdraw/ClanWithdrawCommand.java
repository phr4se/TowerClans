package phrase.towerClans.commands.impls.withdraw;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanWithdrawCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanWithdrawCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.withdraw");

        if (args.length < 2) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        if (modifiedPlayer.getClan() == null) {
            chatUtil.sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
            chatUtil.sendMessage(player, configurationSection.getString("you_don't_have_permission"));
            return true;
        }


        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        ClanResponse clanResponse = clan.withdraw(modifiedPlayer, amount);
        if (clanResponse.isSuccess()) {
            chatUtil.sendMessage(player, configurationSection.getString("you_have_withdrawn_from_the_clan"));
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                chatUtil.sendMessage(player, clanResponse.getMessage());
            }
        }

        return true;
    }
}
