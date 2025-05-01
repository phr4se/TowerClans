package phrase.towerClans.commands.impl.invite;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

import java.util.UUID;

public class ClanCancelCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanCancelCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.invite.cancel");

        UUID senderPlayer = PlayerCalls.removePlayers(player.getUniqueId());

        if (senderPlayer == null) {
            chatUtil.sendMessage(player, configurationSection.getString("has_anyone_sent_you_a_request_to_join_clan"));
            return true;
        }

        chatUtil.sendMessage(player, configurationSection.getString("you_rejected_the_request_to_join_the_clan"));
        chatUtil.sendMessage(Bukkit.getPlayer(senderPlayer), configurationSection.getString("the_player_rejected_the_request_to_join_the_clan"));

        return true;
    }
}
