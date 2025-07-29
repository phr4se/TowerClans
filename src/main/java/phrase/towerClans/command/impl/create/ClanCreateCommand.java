package phrase.towerClans.command.impl.create;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.command.impl.base.Base;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClanCreateCommand implements CommandHandler {

    private final Plugin plugin;

    public ClanCreateCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (args.length < 2) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }

        if (modifiedPlayer.getClan() != null) {
            Utils.sendMessage(player, Config.getCommandMessages().inClan());
            return true;
        }

        int min = Config.getSettings().minSizeClanName();
        int max = Config.getSettings().maxSizeClanName();
        String name = args[1];
        if(!(name.length() < max && name.length() > min)) {
            Utils.sendMessage(player, Config.getMessages().clanNameLimit().replace("%min%", String.valueOf(min)).replace("%max%", String.valueOf(max)));
            return true;
        }


        if(checkClanName(name, Config.getSettings().badWords())) {
            Utils.sendMessage(player, Config.getMessages().clanNameBadWord());
            return true;
        }


        int amount = Config.getSettings().costCreatingClan();

        if (ClanImpl.getClans().containsKey(name)) {
            Utils.sendMessage(player, Config.getCommandMessages().clanNameExists());
            return true;
        }

        int balance = (int) plugin.getEconomy().getBalance(player);

        if (balance < amount) {
            String string = Config.getCommandMessages().notEnough().replace("%amount%", String.valueOf(  amount - balance));
            Utils.sendMessage(player, string);
            return true;
        }

        plugin.getEconomy().withdrawPlayer(player, amount);

        ClanImpl clan = new ClanImpl(name, plugin);
        modifiedPlayer.setClan(clan);
        clan.getMembers().put(modifiedPlayer, Rank.RankType.LEADER.getName());
        ClanImpl.getClans().put(args[1], clan);
        Base.setBase(clan, null);

        Utils.sendMessage(player, Config.getCommandMessages().creatingClan());

        return true;
    }

    private boolean checkClanName(String clanName, List<String> badWords) {

        if(badWords == null || badWords.isEmpty()) {
            return false;
        }

        String regex = badWords.stream()
                .map(Pattern::quote)
                .map(word -> "\\b" + word + "\\b")
                .collect(Collectors.joining("|"));

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(clanName);

        return matcher.find();
    }
}
