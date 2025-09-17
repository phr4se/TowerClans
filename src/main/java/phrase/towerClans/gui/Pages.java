package phrase.towerClans.gui;

import org.bukkit.inventory.ItemStack;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;

import java.util.List;
import java.util.UUID;

public interface Pages {

    MenuPages register(UUID player, MenuPages menuPages);
    void unRegister(UUID player);
    boolean isRegistered(UUID player);
    MenuPages getMenuPages(UUID player);
    List<ItemStack> getContents(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin);

}
