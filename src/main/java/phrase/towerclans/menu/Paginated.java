package phrase.towerclans.menu;

import org.bukkit.inventory.ItemStack;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;

import java.util.List;

public interface Paginated {
    List<ItemStack> getContents(ModifiedPlayer modifiedPlayer, TowerClans plugin, Object... args);
    int offsetRelativeZero();
}
