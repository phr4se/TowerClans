package phrase.towerClans.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandDescription;
import phrase.towerClans.command.CommandLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClanTabCompleter implements TabCompleter {

    private final Plugin plugin;

    public ClanTabCompleter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player player) {

            ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

            if(modifiedPlayer.getClan() == null) {

                if (args.length == 1) return new ArrayList<>(CommandLogger.getCommands().entrySet().stream().filter(entry -> player.hasPermission(entry.getValue().getPermission()) && (entry.getValue().getCommandType() == CommandDescription.CommandType.WITHOUT_CLAN || entry.getValue().getCommandType() == CommandDescription.CommandType.ADMIN)).map(Map.Entry::getKey).toList());
                if(args[0].equals("event") && player.hasPermission(CommandLogger.getCommands().get("event").getPermission())) {
                    if(args.length == 2) return List.of("capture");
                    if(args.length == 3) return List.of("start",
                            "stop");
                }

            } else {

                ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

                if (args.length == 1) return new ArrayList<>(CommandLogger.getCommands().entrySet().stream().filter(entry -> player.hasPermission(entry.getValue().getPermission()) && (entry.getValue().getCommandType() == CommandDescription.CommandType.WITH_CLAN || entry.getValue().getCommandType() == CommandDescription.CommandType.ADMIN) || entry.getKey().equals("top")).map(Map.Entry::getKey).toList());

                if(args[0].equals("invite")) return new ArrayList<>(plugin.getServer().getOnlinePlayers().stream().filter(p -> ModifiedPlayer.get(p).getClan() == null).map(Player::getName).toList());
                if(args[0].equals("kick")) return new ArrayList<>(clan.getMembers().keySet().stream().map(o1 -> (o1.getPlayer() == null) ? Bukkit.getOfflinePlayer(o1.getPlayerUUID()).getName() : o1.getPlayer().getName()).toList());
                if(args[0].equals("rank")) {
                    if(args.length == 2) return new ArrayList<>(clan.getMembers().keySet().stream().map(o1 -> (o1.getPlayer() == null) ? Bukkit.getOfflinePlayer(o1.getPlayerUUID()).getName() : o1.getPlayer().getName()).toList());
                    if(args.length == 3) return List.of("2",
                            "3");
                }
                if(args[0].equals("stats")) return new ArrayList<>(clan.getMembers().keySet().stream().map(o1 -> (o1.getPlayer() == null) ? Bukkit.getOfflinePlayer(o1.getPlayerUUID()).getName() : o1.getPlayer().getName()).toList());
                if(args[0].equals("info")) return new ArrayList<>(ClanImpl.getClans().keySet().stream().toList());
                if(args[0].equals("event") && player.hasPermission(CommandLogger.getCommands().get("event").getPermission())) {
                    if(args.length == 2) return List.of("capture");
                    if(args.length == 3) return List.of("start",
                            "stop");
                }


            }

        }


        return List.of();
    }
}
