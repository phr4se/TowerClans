package phrase.towerclans.gui;

import org.bukkit.inventory.ItemStack;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;

import java.util.List;
import java.util.UUID;

public interface Pages {
    MenuPages register(UUID player, MenuPages menuPages);
    void unRegister(UUID player);
    boolean isRegistered(UUID player);
    MenuPages getMenuPages(UUID player);
    List<ItemStack> getContents(ModifiedPlayer modifiedPlayer, ClanImpl clan, TowerClans plugin);
}
