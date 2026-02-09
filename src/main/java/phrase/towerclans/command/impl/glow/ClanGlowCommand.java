package phrase.towerclans.command.impl.glow;

import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanGlowCommand implements CommandHandler {
    private final TowerClans plugin;

    public ClanGlowCommand(TowerClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        clan.glow(modifiedPlayer, plugin);
        return true;
    }
}
