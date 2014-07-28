package me.onesrodriguez.nickname.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import me.onesrodriguez.nickname.Nicky;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class SQL
{
  private Connection connection;
  private HashMap<String, String> cache = new HashMap();
  protected Nicky plugin;
  
  public SQL(Nicky plugin)
  {
    this.plugin = plugin;
    
    plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable()
    {
      public void run()
      {
        try
        {
          if ((SQL.this.connection != null) && (!SQL.this.connection.isClosed())) {
            SQL.this.connection.createStatement().execute("/* ping */ SELECT 1");
          }
        }
        catch (SQLException e)
        {
          SQL.this.connection = SQL.this.getNewConnection();
        }
      }
    }, 1200L, 1200L);
  }
  
  protected abstract Connection getNewConnection();
  
  protected abstract String getName();
  
  public boolean query(String sql)
    throws SQLException
  {
    return this.connection.createStatement().execute(sql);
  }
  
  public String getConfigName()
  {
    return getName().toLowerCase().replace(" ", "");
  }
  
  public ConfigurationSection getConfigSection()
  {
    return this.plugin.getConfig().getConfigurationSection(getConfigName());
  }
  
  public boolean checkConnection()
  {
    try
    {
      if ((this.connection == null) || (this.connection.isClosed()))
      {
        this.connection = getNewConnection();
        if ((this.connection == null) || (this.connection.isClosed())) {
          return false;
        }
        query("CREATE TABLE IF NOT EXISTS nicky (uuid varchar(36) NOT NULL, nick varchar(64) NOT NULL, PRIMARY KEY (uuid))");
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      
      return false;
    }
    return true;
  }
  
  public void disconnect()
  {
    this.cache.clear();
    try
    {
      if (this.connection != null) {
        this.connection.close();
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public String downloadNick(String uuid)
  {
    String nick = null;
    if (this.cache.containsKey(uuid)) {
      return (String)this.cache.get(uuid);
    }
    if (!checkConnection())
    {
      this.plugin.log("Error with database");
      return null;
    }
    try
    {
      PreparedStatement statement = this.connection.prepareStatement("SELECT nick FROM nicky WHERE uuid = '" + uuid + "';");
      
      ResultSet set = statement.executeQuery();
      while (set.next())
      {
        nick = set.getString("nick");
        
        this.cache.put(uuid, nick);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return nick;
  }
  
  public boolean isUsed(String nick)
  {
    String result = null;
    if (!checkConnection())
    {
      this.plugin.log("Error with database");
      return false;
    }
    try
    {
      PreparedStatement statement = this.connection.prepareStatement("SELECT nick FROM nicky WHERE nick = '" + nick + "';");
      
      ResultSet set = statement.executeQuery();
      while (set.next()) {
        result = set.getString("nick");
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    if (result != null) {
      return true;
    }
    return false;
  }
  
  public void removeFromCache(String uuid)
  {
    if (this.cache.containsKey(uuid)) {
      this.cache.remove(uuid);
    }
  }
  
  public void uploadNick(String uuid, String nick)
  {
    if (!checkConnection())
    {
      this.plugin.log("Error with database");
      return;
    }
    if (downloadNick(uuid) != null) {
      deleteNick(uuid);
    }
    try
    {
      PreparedStatement statement = this.connection.prepareStatement("INSERT INTO nicky (uuid, nick) VALUES ('" + uuid + "','" + nick + "');");
      
      statement.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public void deleteNick(String uuid)
  {
    if (!checkConnection())
    {
      this.plugin.log("Error with database");
      return;
    }
    try
    {
      PreparedStatement statement = this.connection.prepareStatement("DELETE FROM nicky WHERE uuid = '" + uuid + "';");
      
      statement.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
}

