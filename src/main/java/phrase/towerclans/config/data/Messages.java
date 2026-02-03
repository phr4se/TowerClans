package phrase.towerclans.config.data;

import java.util.List;

public record Messages(String noPermission,
                       List<String> notInClan,
                       List<String> inClan,
                       String clanLevelUp,
                       String notPlayer,
                       String unknownCommand,
                       String error,
                       String clanNameLimit,
                       String clanNameBadWord,
                       String noClan,
                       String placeFormat,
                       String useBlockedCommand
) {
}
