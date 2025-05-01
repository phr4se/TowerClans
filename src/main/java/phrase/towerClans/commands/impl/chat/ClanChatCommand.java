package phrase.towerClans.commands.impl.chat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

import java.util.Map;

public class ClanChatCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanChatCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.chat");

        if (modifiedPlayer.getClan() == null) {
            chatUtil.sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        String string = configurationSection.getString("message_format").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", clan.getMembers().get(modifiedPlayer)).replace("%message%", stringBuilder.toString());
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }
}
