package phrase.towerClans.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
        }
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (itemMeta != null) {
            itemMeta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder addLore(String loreLine) {
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
            lore.add(loreLine);
            itemMeta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
