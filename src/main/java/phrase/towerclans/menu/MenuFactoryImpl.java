package phrase.towerclans.menu;

import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.menu.impl.MenuClanGlow;
import phrase.towerclans.menu.impl.MenuClanLevel;
import phrase.towerclans.menu.impl.MenuClanMain;
import phrase.towerclans.menu.impl.MenuClanMembers;

public class MenuFactoryImpl {
    public static Menu getMenu(MenuType type, TowerClans plugin, ModifiedPlayer modifiedPlayer) {
        return switch (type) {
            case MENU_CLAN_MAIN -> new MenuClanMain(type.getFileName(), plugin, modifiedPlayer);
            case MENU_CLAN_MEMBERS -> new MenuClanMembers(type.getFileName(), plugin, modifiedPlayer);
            case MENU_CLAN_LEVEL -> new MenuClanLevel(type.getFileName(), plugin, modifiedPlayer);
            case MENU_CLAN_STORAGE -> {
                final ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
                yield clan.getClanImplStorage().getMenu();
            }
            case MENU_CLAN_GLOW -> new MenuClanGlow(type.getFileName(), plugin, modifiedPlayer);
            default -> null;
        };
    }
}
