package phrase.towerClans.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.config.Config;

import java.util.List;

public class Placeholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "towerclans";
    }

    @Override
    public @NotNull String getAuthor() {
        return "phrase";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.6z";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (identifier.equals("clan_name")) {
            ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
            if(modifiedPlayer.getClan() == null) return Config.getSettings().unknownClan();
            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
            return clan.getName();
        }

        if (identifier.equals("clan_name_chat")) {
            ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
            if(modifiedPlayer.getClan() == null) return Config.getSettings().unknownClan();
            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
            return Config.getSettings().symbolOne() + clan.getName() + Config.getSettings().symbolTwo();
        }

        if(identifier.startsWith("top_")) {

            if(identifier.split("_").length != 2) return Config.getSettings().unknownClan();

            int num = Integer.parseInt(identifier.split("_")[1]);

            if(num < 0) return Config.getSettings().unknownClan();


            List<ClanImpl> clanList = ClanImpl.getClans().values().stream().sorted((o, o1) -> Integer.compare(o1.getXp(), o.getXp())).limit(10).toList();

            if (num >= clanList.size()) return Config.getSettings().unknownClan();


            return clanList.get(num).getName();

        }

        return null;

    }
}
