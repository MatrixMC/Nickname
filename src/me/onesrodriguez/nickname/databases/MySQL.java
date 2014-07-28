package me.onesrodriguez.nickname.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import me.onesrodriguez.nickname.Nicky;
import org.bukkit.configuration.Configuration;

public class MySQL
  extends SQL
{
  public MySQL(Nicky plugin)
  {
    super(plugin);
  }
  
  @Override
  protected Connection getNewConnection()
  {
    Configuration config = this.plugin.getConfig();
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      
      String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("database");
      
      return DriverManager.getConnection(url, config.getString("user"), config.getString("password"));
    }
    catch (Exception e) {}
    return null;
  }
  
  public String getName()
  {
    return "MySQL";
  }
}