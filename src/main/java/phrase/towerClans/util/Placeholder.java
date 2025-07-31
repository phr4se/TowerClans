package phrase.towerClans.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;

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
        return "1.2c";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (identifier.equals("clan_name")) {
            ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
            if(modifiedPlayer.getClan() == null) return "Нет";
            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
            return clan.getName();
        }

        return null;
    }
}
