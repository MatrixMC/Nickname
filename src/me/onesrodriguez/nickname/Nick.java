package me.onesrodriguez.nickname;

import java.util.UUID;
import me.onesrodriguez.nickname.databases.SQL;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

public class Nick
{
  private Nicky plugin;
  private Player player;
  private SQL database;
  private String uuid;
  
  public Nick(Nicky plugin, Player player)
  {
    this.plugin = plugin;
    this.player = player;
    this.database = plugin.getNickDatabase();
    
    this.uuid = player.getUniqueId().toString();
  }
  
  public boolean load()
  {
    String nickname = get();
    if (nickname != null)
    {
      this.player.setDisplayName("§9" + nickname);
      if (this.plugin.isUpdateTab()) {
        this.player.setPlayerListName("§9" + nickname);
      }
      return true;
    }
    return false;
  }
  
  public void unLoad()
  {
    this.database.removeFromCache(this.uuid);
    this.player.setDisplayName(this.player.getName());
  }
  
  public String get()
  {
    return this.database.downloadNick(this.uuid);
  }
  
  public void set(String nick)
  {
    if (get() != null) {
      unSet();
    }
    this.database.uploadNick(this.uuid, nick);
    refresh();
  }
  
  public void unSet()
  {
    this.database.deleteNick(this.uuid);
    player.setPlayerListName(this.player.getDisplayName());
    refresh();
  }
  
  public boolean isUsed(String nick)
  {
    if (this.plugin.isUniqueNicks()) {
      return this.database.isUsed(nick);
    }
    return false;
  }
  
  private void refresh()
  {
    unLoad();
    load();
    if (this.plugin.isTagAPIUsed()) {
      TagAPI.refreshPlayer(this.player);
    }
  }
}
