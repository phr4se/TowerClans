package phrase.towerclans.config.data;

import org.bukkit.Material;
import phrase.towerclans.clan.event.privilege.PrivilegeType;
import phrase.towerclans.database.DatabaseType;
import phrase.towerclans.util.colorizer.ColorizerType;

import java.util.List;

public record Settings(DatabaseType databaseType,
                       ColorizerType colorizerType,
                       int costCreatingClan,
                       int xpForMurder,
                       int minSizeClanName,
                       int maxSizeClanName,
                       List<String> badWords,
                       String unknownClan,
                       String type,
                       List<PrivilegeType> disablePrivilegeType,
                       String symbolOne,
                       String symbolTwo,
                       int xpForKillPlayer,
                       int xpForBreakBlock,
                       List<Material> whiteBlocks,
                       String pathEventCapture,
                       List<String> blackListWorlds,
                       String regionChecker,
                       String host,
                       int port,
                       String database,
                       String username,
                       String password,
                       boolean useSSL,
                       String clanPvpEnable,
                       String clanPvpDisable,
                       String regex,
                       String symbolNot
) {
}
