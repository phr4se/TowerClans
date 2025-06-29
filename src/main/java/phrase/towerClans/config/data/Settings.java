package phrase.towerClans.config.data;

import phrase.towerClans.database.DatabaseType;

import java.util.List;

public record Settings(DatabaseType databaseType,
                       int costCreatingClan,
                       int xpForMurder,
                       int minSizeClanName,
                       int maxSizeClanName,
                       List<String> badWords
                       ){
}
