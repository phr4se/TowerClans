package phrase.towerClans.clan.event;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.List;

public class RegionFlag {

    public static void setRegionFlags(ProtectedRegion region, List<String> strings) {

        for(String string : strings) {

            String[] flag = string.split(": ");

            switch (flag[0].toLowerCase()) {
                case "build" -> region.setFlag(Flags.BUILD, StateFlag.State.valueOf(flag[1]));
                case "pvp" -> region.setFlag(Flags.PVP, StateFlag.State.valueOf(flag[1]));
                case "use" -> region.setFlag(Flags.USE, StateFlag.State.valueOf(flag[1]));
                case "invincible" -> region.setFlag(Flags.INVINCIBILITY, StateFlag.State.valueOf(flag[1]));
                case "tnt" -> region.setFlag(Flags.TNT, StateFlag.State.valueOf(flag[1]));
                case "wither-damage" -> region.setFlag(Flags.WITHER_DAMAGE, StateFlag.State.valueOf(flag[1]));
                case "chorus-fruit-teleport" -> region.setFlag(Flags.CHORUS_TELEPORT, StateFlag.State.valueOf(flag[1]));
                case "ghast-fireball" -> region.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.valueOf(flag[1]));
                case "chest-access" -> region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.valueOf(flag[1]));
                case "creeper-explosion" -> region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.valueOf(flag[1]));
                case "fall-damage" -> region.setFlag(Flags.FALL_DAMAGE, StateFlag.State.valueOf(flag[1]));
                case "pistons" -> region.setFlag(Flags.PISTONS, StateFlag.State.valueOf(flag[1]));
                case "potion-splash" -> region.setFlag(Flags.POTION_SPLASH, StateFlag.State.valueOf(flag[1]));
            }

        }

    }

}
