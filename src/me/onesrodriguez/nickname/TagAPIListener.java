package me.onesrodriguez.nickname;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

public class TagAPIListener
  implements Listener
{
  private Nicky plugin;
  
  public TagAPIListener(Nicky plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onNameTag(AsyncPlayerReceiveNameTagEvent event)
  {
    Nick nick = new Nick(this.plugin, event.getNamedPlayer());
    if (nick.get() != null) {
      event.setTag(nick.get());
    }
  }
}
