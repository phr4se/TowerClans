package phrase.towerClans.config.data;

import phrase.towerClans.clan.event.privilege.PrivilegeType;
import phrase.towerClans.database.DatabaseType;

import java.util.List;

public record Settings(DatabaseType databaseType,
                       int costCreatingClan,
                       int xpForMurder,
                       int minSizeClanName,
                       int maxSizeClanName,
                       List<String> badWords,
                       String unknownClan,
                       String type,
                       List<PrivilegeType> disablePrivilegeType,
                       String symbolOne,
                       String symbolTwo
                       ){
}
