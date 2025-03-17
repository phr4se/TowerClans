package phrase.towerClans.commands.impls.invite;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanInviteCommand implements CommandHandler {

    public static final Map<UUID, UUID> PLAYERS = new HashMap<>();

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invite");

        if (args.length < 2) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        if (modifiedPlayer.getClan() == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        if (!clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.LEADER.getName()) && !clan.getMembers().get(modifiedPlayer).equals(AbstractClan.RankType.DEPUTY.getName())) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_don't_have_permission"));
            return true;
        }


        String name = args[1];

        Player targetPlayer = Bukkit.getPlayer(name);

        if (targetPlayer == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("the_player_was_not_found"));
            return true;
        }

        PLAYERS.put(targetPlayer.getUniqueId(), player.getUniqueId());
        ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_have_sent_a_request_to_join_the_clan"));
        ChatUtil.getChatUtil().sendMessage(targetPlayer, configurationSection.getString("you_have_received_a_request_to_join_the_clan"));

        return true;
    }


}
