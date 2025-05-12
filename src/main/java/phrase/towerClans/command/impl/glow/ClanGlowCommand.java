package phrase.towerClans.command.impl.glow;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;

import java.util.Map;

public class ClanGlowCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;
    private ConfigurationSection section;

    public ClanGlowCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
        section = plugin.getConfig().getConfigurationSection("message.command.glow");
    }

    public void updateSection() {
        section = plugin.getConfig().getConfigurationSection("message.command.glow");
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (modifiedPlayer.getClan() == null) {
            chatUtil.sendMessage(player, section.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        clan.glow(modifiedPlayer);

        return true;
    }
}
