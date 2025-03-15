package phrase.towerClans.commands.impls.invite;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

import java.util.UUID;

public class ClanCancelCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invite.cancel");

        UUID senderPlayer = ClanInviteCommand.PLAYERS.remove(player.getUniqueId());

        if (senderPlayer == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("has_anyone_sent_you_a_request_to_join_clan"));
            return true;
        }

        ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_rejected_the_request_to_join_the_clan"));
        ChatUtil.getChatUtil().sendMessage(Bukkit.getPlayer(senderPlayer), configurationSection.getString("the_player_rejected_the_request_to_join_the_clan"));

        return true;
    }
}
