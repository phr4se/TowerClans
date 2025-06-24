package phrase.towerClans.gui;

public enum MenuType {

    MENU_CLAN_MAIN(0),
    MENU_CLAN_MEMBERS(1),
    MENU_CLAN_LEVEL(2),
    MENU_CLAN_BACK(3),
    MENU_CLAN_EXIT(4),
    MENU_CLAN_STORAGE(5),
    MENU_CLAN_PREVIOUS(6),
    MENU_CLAN_NEXT(7),
    MENU_CLAN_GLOW(8),
    MENU_CLAN_SHOP(9);

    private final int id;

    MenuType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
