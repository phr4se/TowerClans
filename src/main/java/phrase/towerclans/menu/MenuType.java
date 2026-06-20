package phrase.towerclans.menu;

public enum MenuType {
    MENU_CLAN_MAIN("menus/menu-clan-main.yml", false),
    MENU_CLAN_MEMBERS("menus/menu-clan-members.yml", false),
    MENU_CLAN_LEVEL("menus/menu-clan-level.yml", false),
    MENU_CLAN_STORAGE("menus/menu-clan-storage.yml", false),
    MENU_CLAN_PREVIOUS(null, false),
    MENU_CLAN_NEXT(null, false),
    MENU_CLAN_GLOW("menus/menu-clan-glow.yml", false);
    private final String fileName;
    private final boolean withAlliance;

    MenuType(String fileName, boolean withAlliance) {
        this.fileName = fileName;
        this.withAlliance = withAlliance;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean withAlliance() {
        return withAlliance;
    }
}
