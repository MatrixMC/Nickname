package me.onesrodriguez.nickname.commands;

import me.onesrodriguez.nickname.Nick;
import me.onesrodriguez.nickname.Nicky;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand
  implements CommandExecutor
{
  Nicky plugin;
  
  public NickCommand(Nicky plugin)
  {
    this.plugin = plugin;
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if (!(sender instanceof Player)) {
      runAsConsole(args);
    } else if (args.length >= 2) {
      runAsAdmin(sender, args);
    } else {
      runAsPlayer(sender, args);
    }
    return true;
  }
  
  private void runAsConsole(String[] args)
  {
    if (args.length >= 2)
    {
      Player receiver = this.plugin.getServer().getPlayer(args[0]);
      if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("reset")){
          if (receiver == null){
            this.plugin.log("Could not find '" + args[0] + "', are you sure they are online?");
            return;
          }
          
          Nick nick = new Nick(this.plugin, receiver);
          nick.unSet();
          //receiver.performCommand("enick off");
      
          receiver.sendMessage(Nicky.getMatrixPrefix() + "Nickname reset.");
          //receiver.sendMessage(Nicky.getHivePrefix() + "Nickname removed.");
          //receiver.sendMessage(Nicky.getFortiesPrefix() + "§6Successfully removed nickname.");
          //receiver.sendMessage(Nicky.getEmpirePrefix() + "You've been reset! You're back to your original name, " + receiver.getDisplayName() + ".");
          this.plugin.log(receiver.getName() + "'s nickname has been reset.");
          Nicky.logToFile("User CONSOLE reset " + receiver.getName() + "'s nickname at " + System.currentTimeMillis());
          return;
      }
      if (receiver == null)
      {
        this.plugin.log("Could not find '" + args[0] + "', are you sure they are online?");
        return;
      }
      String nickname = ChatColor.translateAlternateColorCodes('&', args[1]) + ChatColor.RESET;
      
      Nick nick = new Nick(this.plugin, receiver);
      if (nick.isUsed(nickname))
      {
        this.plugin.log("Sorry, an error occurred.");
        return;
      }
      nick.set(nickname);
      //receiver.performCommand("enick &9" + nickname);
      
      receiver.sendMessage(Nicky.getMatrixPrefix() + "Usage of this command is logged! You are now known as " + nickname + ".");
      //receiver.sendMessage(Nicky.getHivePrefix() + "WARNING: Usage of this command is logged!");
      //receiver.sendMessage(Nicky.getHivePrefix() + "You are now known as " + nickname);
      
      //receiver.sendMessage(Nicky.getFortiesPrefix() + "§6Successfully nicknamed as " + nickname + "§6.");
      //receiver.sendMessage(Nicky.getEmpirePrefix() + "Don't misuse this command! Your nondeploom is now: " + nickname);
      
      Nicky.logToFile("User " + receiver.getName() + " nicknamed as " + nickname + " at " + System.currentTimeMillis());
      this.plugin.log(receiver.getName() + "'s nick has been set to " + nickname);
    }
    else
    {
      this.plugin.log("Usage: /nick <player> <nickname>");
    }
  }
  
  private void runAsAdmin(CommandSender sender, String[] args)
  {
    Player receiver = this.plugin.getServer().getPlayer(args[0]);
    if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("reset")){
        if (receiver == null){
         sender.sendMessage(Nicky.getMatrixPrefix() + "§4Sorry, an error occurred.");
         //sender.sendMessage(Nicky.getHivePrefix() + "§4Sorry, an error occurred.");
         //sender.sendMessage(Nicky.getFortiesPrefix() + "§4Sorry, an error occurred.");
         //sender.sendMessage(Nicky.getEmpirePrefix() + "§4Sorry, an error occurred.");
         return;
        }
        
        if (sender.hasPermission("nickname.reset.other")){
          Nick nick = new Nick(this.plugin, receiver);
          nick.unSet();
          //receiver.performCommand("enick off");

          receiver.sendMessage(Nicky.getMatrixPrefix() + "Nickname reset.");
          //receiver.sendMessage(Nicky.getHivePrefix() + "Nickname removed.");
          //receiver.sendMessage(Nicky.getFortiesPrefix() + "§6Successfully removed nickname.");
          //receiver.sendMessage(Nicky.getEmpirePrefix() + "You've been reset! You're back to your original name, " + receiver.getDisplayName() + ".");
          return;
        }else{
          sender.sendMessage(Nicky.getMatrixPrefix() + "§c#NoPerms");
          //sender.sendMessage(Nicky.getHivePrefix() + "§4Insert funny access denied message here!");
          //sender.sendMessage(Nicky.getFortiesPrefix() + "§cNo permission.");
          //sender.sendMessage(Nicky.getEmpirePrefix() + "§cInsufficient permissions. If you should have access, contact Alex immediately.");
          return;
        }
    }
    if (receiver == null)
    {
      return;
    }
    String nickname = args[1] + ChatColor.RESET;
    if (sender.hasPermission("nickname.use.other"))
    {
      Nick nick = new Nick(this.plugin, receiver);
      if (nick.isUsed(nickname))
      {
        //sender.sendMessage(Nicky.getHivePrefix() + "§4Sorry, an error occurred.");
        //sender.sendMessage(Nicky.getFortiesPrefix() + "§4Sorry, an error occurred.");
        //sender.sendMessage(Nicky.getEmpirePrefix() + "§4Sorry, an error occurred.");
        sender.sendMessage(Nicky.getMatrixPrefix() + "§4Sorry, an error occurred.");
        return;
      }
      nick.set(nickname);
      //receiver.performCommand("enick &9" + nickname);
      
      sender.sendMessage(Nicky.getMatrixPrefix() + "Usage of this command is logged! Successfully set " + receiver.getName() + "'s nickname to " + nickname + ".");
      
      receiver.sendMessage(Nicky.getMatrixPrefix() + "You are now known as " + nickname + ".");
      //receiver.sendMessage(Nicky.getHivePrefix() + "WARNING: Usage of this command is logged!");
      //receiver.sendMessage(Nicky.getHivePrefix() + "You are now known as " + nickname);
      
      //receiver.sendMessage(Nicky.getFortiesPrefix() + "§6Successfully nicknamed as " + nickname + "§6.");
      
      //receiver.sendMessage(Nicky.getEmpirePrefix() + "Don't misuse this command! Your nondeploom is now: " + nickname);
      receiver.sendMessage(Nicky.getMatrixPrefix() + "You are now known as " + nickname + ".");

      Nicky.logToFile("User " + receiver.getName() + " was nicknamed by " + sender.getName() + "as " + nickname + " at " + System.currentTimeMillis());
      this.plugin.log(receiver.getName() + "'s nick has been set to " + nickname);
    }else{
      sender.sendMessage(Nicky.getMatrixPrefix() + "§c#NoPerms");
      //sender.sendMessage(Nicky.getHivePrefix() + "§4Insert funny access denied message here!");
      //sender.sendMessage(Nicky.getFortiesPrefix() + "§cNo permission.");
      //sender.sendMessage(Nicky.getEmpirePrefix() + "§cInsufficient permissions. If you should have access, contact Alex immediately.");
    }
  }
  
  private void runAsPlayer(CommandSender sender, String[] args)
  {
    Player player = (Player)sender;
    if (sender.hasPermission("nickname.use"))
    {
      if (args.length >= 1)
      {
        if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("reset")){
            if (sender.hasPermission("nickname.reset")){
                Nick nick = new Nick(this.plugin, (Player)sender);
                nick.unSet();
                
                //player.performCommand("enick off");
                
                player.sendMessage(Nicky.getMatrixPrefix() + "Nickname reset.");
                //player.sendMessage(Nicky.getHivePrefix() + "Nickname removed.");
                //player.sendMessage(Nicky.getFortiesPrefix() + "§6Successfully removed nickname.");
                //player.sendMessage(Nicky.getEmpirePrefix() + "You've been reset! You're back to your original name, " + player.getDisplayName() + ".");
                
                Nicky.logToFile("User" + player.getName() + " reset their nickname at " + System.currentTimeMillis());
                return;
            }else{
                player.sendMessage(Nicky.getMatrixPrefix() + "§c#NoPerms");
                //player.sendMessage(Nicky.getHivePrefix() + "§4Insert funny access denied message here!");
                //player.sendMessage(Nicky.getFortiesPrefix() + "§cNo permission.");
                //player.sendMessage(Nicky.getEmpirePrefix() + "§cInsufficient permissions. If you should have access, contact Alex immediately.");
                return;
            }
        }
        String nickname = args[0] + ChatColor.RESET;
        Nick nick = new Nick(this.plugin, player);
        if (nick.isUsed(nickname))
        {
        player.sendMessage(Nicky.getMatrixPrefix() + "§4Sorry, an error occurred.");
        //player.sendMessage(Nicky.getHivePrefix() + "§4Sorry, an error occurred.");
        //player.sendMessage(Nicky.getFortiesPrefix() + "§4Sorry, an error occurred.");
        player.sendMessage(Nicky.getEmpirePrefix() + "§4Sorry, an error occurred.");
          return;
        }
        nick.set(nickname);
        //player.performCommand("enick &9" + nickname);
        
        player.sendMessage(Nicky.getMatrixPrefix() + "Usage of this command is logged! You are now known as " + nickname + ".");
        //player.sendMessage(Nicky.getHivePrefix() + "WARNING: Usage of this command is logged!");
        //player.sendMessage(Nicky.getHivePrefix() + "You are now known as " + nickname);
        //player.sendMessage(Nicky.getFortiesPrefix() + "Successfully nicknamed as " + nickname + "§6.");
        //player.sendMessage(Nicky.getEmpirePrefix() + "Don't misuse this command! Your nondeploom is now: " + nickname);
      }
      else
      {
        player.sendMessage(Nicky.getMatrixPrefix() + "Usage: §5/nick [name]");
        //player.sendMessage(Nicky.getHivePrefix() + "§4Invalid amount of arguments supplied!");
        //player.sendMessage(Nicky.getFortiesPrefix() + "§4Incorrect usage. The correct syntax is: /nick <name/off/reset>");
        player.sendMessage(Nicky.getEmpirePrefix() + "§4Incorrect syntax! (/nick <name/off/reset>");
      }
    }
    else {   
        player.sendMessage(Nicky.getMatrixPrefix() + "§c#NoPerms");
        //player.sendMessage(Nicky.getHivePrefix() + "§4Insert funny access denied message here!");
        //player.sendMessage(Nicky.getFortiesPrefix() + "§cNo permission.");
        //player.sendMessage(Nicky.getEmpirePrefix() + "§cInsufficient permissions. If you should have access, contact Alex immediately.");
    }
  }
}

