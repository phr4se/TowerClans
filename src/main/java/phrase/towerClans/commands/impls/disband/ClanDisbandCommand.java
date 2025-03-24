package phrase.towerClans.commands.impls.disband;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.ClanResponse;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;

public class ClanDisbandCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanDisbandCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.disband");

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (modifiedPlayer.getClan() == null) {
            chatUtil.sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        ClanResponse clanResponse = clan.disband(modifiedPlayer);

        if (clanResponse.isSuccess()) {
            chatUtil.sendMessage(player, configurationSection.getString("you_deleted_the_clan"));
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                chatUtil.sendMessage(player, clanResponse.getMessage());
            }
        }

        return true;
    }
}
