package phrase.towerClans.commands.impls.reload;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanReloadCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {
        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.reload");
        Plugin.getInstance().reloadConfig();
        ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_have_reloaded_the_config"));

        return true;
    }

}
