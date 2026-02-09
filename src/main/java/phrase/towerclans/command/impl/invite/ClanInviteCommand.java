package phrase.towerclans.command.impl.invite;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.clan.permission.PermissionType;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

public class ClanInviteCommand implements CommandHandler {
    private final TowerClans plugin;

    public ClanInviteCommand(TowerClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
        if (!modifiedPlayer.hasPermission(PermissionType.INVITE)) {
            Utils.sendMessage(player, Config.getCommandMessages().noPermission());
            return true;
        }
        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }
        if (modifiedPlayer.getClan() == null) {
            Utils.sendMessage(player, Config.getCommandMessages().notInClan());
            return true;
        }
        String name = args[1];
        Player targetPlayer = Bukkit.getPlayer(name);
        if (targetPlayer == null) {
            Utils.sendMessage(player, Config.getCommandMessages().playerNotFound());
            return true;
        }
        if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            Utils.sendMessage(player, Config.getCommandMessages().noInviteYourself());
            return true;
        }
        PlayerCalls.addPlayers(targetPlayer.getUniqueId(), player.getUniqueId());
        Utils.sendMessage(player, Config.getCommandMessages().inviteInClan());
        Utils.sendMessage(targetPlayer, Config.getCommandMessages().invitedInClan().replace("%clan_name%", ((ClanImpl) modifiedPlayer.getClan()).getName()));
        return true;
    }
}
