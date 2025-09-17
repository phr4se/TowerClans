package phrase.towerClans.gui.impl;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuProvider;
import phrase.towerClans.gui.Pages;

import java.util.List;
import java.util.UUID;

public class MenuClanMembersProvider extends MenuProvider implements Pages {

    public MenuClanMembersProvider() {
        super(new MenuClanMembersService());
    }

    @Override
    public Inventory getMenu(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        return getMenuService().create(modifiedPlayer, clan, plugin);
    }

    public List<ItemStack> getPlayers(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        return ((MenuClanMembersService) getMenuService()).getPlayers(modifiedPlayer, clan, plugin);
    }

    @Override
    public MenuPages register(UUID player, MenuPages menuPages) {
        return MenuClanMembersService.register(player, menuPages);
    }

    @Override
    public void unRegister(UUID player) {
        MenuClanMembersService.unRegister(player);
    }

    @Override
    public boolean isRegistered(UUID player) {
        return MenuClanMembersService.isRegistered(player);
    }

    @Override
    public MenuPages getMenuPages(UUID player) {
        return MenuClanMembersService.getMenuPages(player);
    }

    @Override
    public boolean menuPages() {
        return getMenuService().menuPages();
    }

    @Override
    public List<ItemStack> getContents(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        return ((MenuClanMembersService) getMenuService()).getPlayers(modifiedPlayer, clan, plugin);
    }
}
