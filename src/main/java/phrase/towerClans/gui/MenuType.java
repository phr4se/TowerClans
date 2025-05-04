package phrase.towerClans.gui;

public enum MenuType {

    MENU_CLAN_MAIN(1),
    MENU_CLAN_MEMBERS(2),
    MENU_CLAN_LEVEL(3),
    MENU_CLAN_BACK(4),
    MENU_CLAN_EXIT(5),
    MENU_CLAN_STORAGE(6),
    MENU_CLAN_PREVIOUS(7),
    MENU_CLAN_NEXT(8);

    private final int id;

    MenuType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
