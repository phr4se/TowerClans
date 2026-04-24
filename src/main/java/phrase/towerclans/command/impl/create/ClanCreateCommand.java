package phrase.towerclans.command.impl.create;

import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.ClanManager;
import phrase.towerclans.clan.ClanResponse;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClanCreateCommand implements CommandHandler {
    private final ClanManager<ClanImpl> clanManager;

    public ClanCreateCommand(TowerClans plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }
        String name = args[1];
        ClanResponse clanResponse = clanManager.createClan(modifiedPlayer, name);
        if(clanResponse.getMessage() != null) Utils.sendMessage(player, clanResponse.getMessage());
        return true;
    }
}
