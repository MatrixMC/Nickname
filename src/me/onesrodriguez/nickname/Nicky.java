package me.onesrodriguez.nickname;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import me.onesrodriguez.nickname.commands.NickCommand;
import me.onesrodriguez.nickname.databases.MySQL;
import me.onesrodriguez.nickname.databases.SQL;
import me.onesrodriguez.nickname.databases.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Nicky
  extends JavaPlugin
{
  
  private static Nicky instance;
  private static final String HIVEPREFIX = "§e[HiveMC] §a";
  private static final String FORTIESPREFIX = "§8❙ §5FortiesNickname §8❙ §7» §6";
  private static final String EMPIREPREFIX = "§7[§5EmpireMC§7] §3";
  private static final String MATRIXPREFIX = "§e[MatrixMC] §3";
  private final Set<SQL> databases;
  private SQL database;
  private boolean usesTagAPI = false;
  private boolean updateTab = true;
  private boolean uniqueNicks = true;
  
  public Nicky()
  {
    this.databases = new HashSet();
  }
  
  @Override
  public void onEnable()
  {
    this.databases.add(new MySQL(this));
    this.databases.add(new SQLite(this));
    
    saveDefaultConfig();
    
    setupDatabase();
    
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new PlayerListener(this), this);
    if ((pm.isPluginEnabled("TagAPI")) && (getConfig().get("tagapi").equals("true")))
    {
      pm.registerEvents(new TagAPIListener(this), this);
      log("TagAPI support enabled.");
      this.usesTagAPI = true;
    }
    if (getConfig().get("tab").equals("false")) {
      this.updateTab = false;
    }
    if (getConfig().get("unique").equals("false")) {
      this.uniqueNicks = false;
    }
    getCommand("nick").setExecutor(new NickCommand(this));
    if (!this.database.checkConnection())
    {
      log("Error with database");
      pm.disablePlugin(this);
    }
    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      Nick nick = new Nick(this, player);
      
      nick.load();
    }
  }
  
  @Override
  public void onDisable()
  {
    this.database.disconnect();
  }
  
  public SQL getNickDatabase()
  {
    return this.database;
  }
  
  private boolean setupDatabase()
  {
    String type = getConfig().getString("type");
    
    this.database = null;
    for (SQL database : this.databases) {
      if (type.equalsIgnoreCase(database.getConfigName()))
      {
        this.database = database;
        
        log("Database set to " + database.getConfigName() + ".");
        
        break;
      }
    }
    if (this.database == null)
    {
      log("Database type does not exist!");
      
      return false;
    }
    return true;
  }
  
  public void log(String message)
  {
    getLogger().info(message);
  }

  public static Nicky getInstance() {
      return instance;
  }
 
  public static String getHivePrefix()
  {
    return HIVEPREFIX;
  }
  
  public static String getFortiesPrefix()
  {
    return FORTIESPREFIX;
  }
  
  public static String getEmpirePrefix()
  {
    return EMPIREPREFIX;
  }

  public static String getMatrixPrefix() {
    return MATRIXPREFIX;
  }
  
  public static void logToFile(String message){   
        try{
            File dataFolder = getInstance().getDataFolder();
            if(!dataFolder.exists()){
                dataFolder.mkdir();
            }
 
            File saveTo = new File(getInstance().getDataFolder(), "nickname.txt");
            if (!saveTo.exists()){
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(message);
            pw.flush();
            pw.close();
 
        } catch (IOException e){  
        }
    }
  
  public boolean isTagAPIUsed()
  {
    return this.usesTagAPI;
  }
  
  public boolean isUpdateTab()
  {
    return this.updateTab;
  }
  
  public boolean isUniqueNicks()
  {
    return this.uniqueNicks;
  }
}
