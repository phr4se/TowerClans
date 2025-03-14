package phrase.towerClans.clan;

public interface Clan {

    boolean invite(ModifiedPlayer modifiedPlayer);
    boolean kick(ModifiedPlayer modifiedPlayer);
    boolean invest(ModifiedPlayer modifiedPlayer, int amount);
    boolean withdraw(ModifiedPlayer modifiedPlayer, int amount);
    boolean leave(ModifiedPlayer modifiedPlayer);
    boolean rank(ModifiedPlayer modifiedPlayer, int id);
    boolean disband(ModifiedPlayer modifiedPlayer, Clan clan);

}
