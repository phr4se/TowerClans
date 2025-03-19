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


    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.disband");

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (modifiedPlayer.getClan() == null) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you're_not_in_the_clan"));
            return true;
        }

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

        ClanResponse clanResponse = clan.disband(modifiedPlayer);

        if (clanResponse.isSuccess()) {
            ChatUtil.getChatUtil().sendMessage(player, configurationSection.getString("you_deleted_the_clan"));
            return true;
        } else {
            if(clanResponse.getMessage() != null) {
                ChatUtil.getChatUtil().sendMessage(player, clanResponse.getMessage());
            }
        }

        return true;
    }
}
