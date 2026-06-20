package phrase.towerclans.menu.impl;

import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.menu.Menu;

public class MenuClanMain extends Menu {
    public MenuClanMain(String fileName, TowerClans plugin, ModifiedPlayer modifiedPlayer) {
        super(fileName, plugin, modifiedPlayer);
        setupDefaultItems();
    }
}
