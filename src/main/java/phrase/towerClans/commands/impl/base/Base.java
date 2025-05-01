package phrase.towerClans.commands.impl.base;

import org.bukkit.Location;
import phrase.towerClans.clan.Clan;

import java.util.HashMap;
import java.util.Map;

public class Base {

    private static final Map<Clan, Location> BASES = new HashMap<>();

    public static Location getBase(Clan clan) {
        return BASES.get(clan);
    }

    public static void setBase(Clan clan, Location location) {
        if(BASES.containsKey(clan)) {
            BASES.replace(clan, BASES.get(clan), location);
            return;
        }
        BASES.put(clan, location);
    }

}
