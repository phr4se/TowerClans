package phrase.towerClans.gui;

import phrase.towerClans.gui.impl.*;

public class MenuFactory {

    public static MenuProvider getProvider(MenuType menuType) {
        return switch (menuType) {
            case MENU_CLAN_MAIN, MENU_CLAN_BACK -> new MenuClanMainProvider();
            case MENU_CLAN_MEMBERS -> new MenuClanMembersProvider();
            case MENU_CLAN_LEVEL -> new MenuClanLevelProvider();
            case MENU_CLAN_STORAGE -> new MenuClanStorageProvider();
            case MENU_CLAN_GLOW -> new MenuClanGlowProvider();
            default -> null;
        };
    }

}
