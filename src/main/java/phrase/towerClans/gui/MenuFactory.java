package phrase.towerClans.gui;

import phrase.towerClans.gui.impl.MenuClanLevelProvider;
import phrase.towerClans.gui.impl.MenuClanMainProvider;
import phrase.towerClans.gui.impl.MenuClanMembersProvider;
import phrase.towerClans.gui.impl.MenuClanStorageProvider;

public class MenuFactory {

    public static MenuProvider getProvider(MenuType menuType) {
        return switch (menuType) {
            case MENU_CLAN_MAIN, MENU_CLAN_BACK -> new MenuClanMainProvider();
            case MENU_CLAN_MEMBERS -> new MenuClanMembersProvider();
            case MENU_CLAN_LEVEL -> new MenuClanLevelProvider();
            case MENU_CLAN_STORAGE -> new MenuClanStorageProvider();
            default -> null;
        };
    }

}
