package com.github.joelgodofwar.ph;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;



import org.mcstats.MetricsLite;

import com.github.joelgodofwar.ph.api.Ansi;
import com.github.joelgodofwar.ph.api.ConfigAPI;
import com.github.joelgodofwar.ph.api.NetherPortalFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener{
    
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static boolean UpdateCheck;
	public static boolean debug;
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	@Override // TODO:
	public void onEnable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		String[] serverversion;
		serverversion = getVersion().split("\\.");
		if(debug){debuglog("getVersion = " + getVersion());}
		if(debug){debuglog("serverversion = " + serverversion.length);}
		for (int i = 0; i < serverversion.length; i++)
            log(serverversion[i] + " i=" + i);
		if (!(Integer.parseInt(serverversion[1]) >= 9)&&!(Integer.parseInt(serverversion[1]) < 13)){
			
		//if(!getVersion().contains("1.9")&&!getVersion().contains("1.10")&&!getVersion().contains("1.11")){
			logger.info(ANSI_RED + "WARNING!" + ANSI_GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ANSI_RESET);
			logger.info(ANSI_RED + "WARNING! " + ANSI_YELLOW + "Server is NOT version 1.9.*+ or under version 1.13" + ANSI_RESET);
			logger.info(ANSI_RED + "WARNING! " + ANSI_YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + " disabling." + ANSI_RESET);
			logger.info(ANSI_RED + "WARNING!" + ANSI_GREEN + "*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ANSI_RESET);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		ConfigAPI.CheckForConfig(this);
		consoleInfo("enabled");
		getServer().getPluginManager().registerEvents(this, this);
		
		String varCheck = getConfig().getString("auto-update-check");
		String varCheck3 = getConfig().getString("debug");
		//log("varCheck " + varCheck);
		//log("varCheck2 " + varCheck2);
		//log("varCheck3 " + varCheck3);
		if(varCheck.contains("default")){
			getConfig().set("auto-update-check", true);
		}
		if(varCheck3.contains("default")){
			getConfig().set("debug", false);
		}
		saveConfig();
		ConfigAPI.Reloadconfig(this);
		
		/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			log("jarfile contains dev, debug set to true.");
		}
		
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		}catch (Exception e){
			// Failed to submit the stats
		}
	}
	
	@Override // TODO:
	public void onDisable(){
		consoleInfo("disabled");
	}
	
	public void consoleInfo(String state) {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.YELLOW + "**************************************" + Ansi.SANE);
		logger.info(Ansi.MAGENTA + pdfFile.getName() + Ansi.GREEN + " v" + pdfFile.getVersion() + Ansi.SANE + " is " + state);
		logger.info(Ansi.YELLOW + "**************************************" + Ansi.SANE);
	}
	
	public  void debuglog(String dalog){
		log(Ansi.RED + "[DEBUG]" + Ansi.SANE + dalog);
	}
	
	
	public  void log(String dalog){
		Bukkit.getLogger().info(Ansi.MAGENTA + this.getName() + " " + Ansi.SANE  + dalog);
	}
	
	public void PortalListener(Plugin plugin){
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
 
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
     
        try{
	    	Player player = event.getPlayer();
	        Action action = event.getAction();
	        ItemStack tinder = event.getItem();
	        Location loc1 = event.getClickedBlock().getLocation();//getPlayer().getLocation();
	        Block block = event.getClickedBlock();
	     
	        if(action.equals(Action.RIGHT_CLICK_BLOCK)==true){
	     
		        if(tinder.getType().equals(Material.FLINT_AND_STEEL)){
		     
			        if(block.getType().equals(Material.OBSIDIAN)){
			         
			        	if (!player.hasPermission("portalhelper.rightclick") && !player.isOp()) {
		                    //player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
		                    return;
		                }
			        	/**List<World> worlds = Bukkit.getWorlds();
			        	/** 
			        	 *  World 0 = Overworld
			        	 *  World 1 = Nether
			        	 *  World 2 = End
			        	 *
				        if(worlds.get(0).equals(loc1.getWorld())){
				        	Block block2 = worlds.get(1).getBlockAt(block.getX() / 8,block.getY(),block.getZ() / 8);
				        	if(block2.getType().equals(Material.PORTAL)||block2.getType().equals(Material.OBSIDIAN)){
				        		log("Obsidian at location in Nether");
				        	}else{
				        		log("You clicked " + block.getLocation());
				        		log("Block at x=" + block2.getLocation() + " is " + block2.getType().name());
				        	}
				        }*/
			        	Location findPortal = null;
			        	List<World> worlds = Bukkit.getWorlds();
			        	Location loc2 = null;
			        	if(worlds.get(0).equals(loc1.getWorld())){ // Overworld
			        		loc2 = new Location(worlds.get(1), Math.floor(loc1.getBlockX() / 8), loc1.getBlockY(), Math.floor(loc1.getBlockZ() / 8));
			        		log("loc1 blockX=" + loc1.getBlockX() + " blockY=" + loc1.getBlockY() + " blockZ=" + loc1.getBlockZ());
			        		log("loc1 X=" + loc1.getX() + " Y=" + loc1.getY() + " Z=" + loc1.getZ());
			        		log("loc2 blockX=" + loc2.getBlockX() + " blockY=" + loc2.getBlockY() + " blockZ=" + loc2.getBlockZ());
			        		log("loc2 X=" + loc2.getX() + " Y=" + loc2.getY() + " Z=" + loc2.getZ());
			        		if(findPortal(block, loc2)){
			        			//player.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.LIGHT_PURPLE + " Portals are linked.");
			        			
			        		}
			        		findPortal = NetherPortalFinder.locate(loc2);
			        	}
			        	if(worlds.get(1).equals(loc1.getWorld())){ // Nether
			        		loc2 = new Location(worlds.get(0), (loc1.getBlockX() * 8), loc1.getBlockY(), (loc1.getBlockZ() * 8));
			        		findPortal = NetherPortalFinder.locate(loc2);
			        	}
			        	
			        	if(findPortal != null){
			        		log("Portal found @ " + findPortal.getWorld() + " x:" + findPortal.getBlockX() + " y:" + findPortal.getBlockY() + " z:" + findPortal.getBlockZ());
			        		// Portal found
			        		// check if coords correspond to our portal
			        		if(!loc2.equals(null)){
				        		if(loc2.getBlockX() == findPortal.getBlockX() && loc2.getBlockZ() == findPortal.getBlockZ()||
				        				(loc2.getBlockX() + 1) == findPortal.getBlockX() && loc2.getBlockZ() == findPortal.getBlockZ()||
				        				(loc2.getBlockX() - 1) == findPortal.getBlockX() && loc2.getBlockZ() == findPortal.getBlockZ()||
				        				loc2.getBlockX() == findPortal.getBlockX() && (loc2.getBlockZ() + 1) == findPortal.getBlockZ()||
				        				loc2.getBlockX() == findPortal.getBlockX() && (loc2.getBlockZ() - 1) == findPortal.getBlockZ()||
				        				(loc2.getBlockX() + 1) == findPortal.getBlockX() && (loc2.getBlockZ() + 1) == findPortal.getBlockZ()||
				        				(loc2.getBlockX() - 1) == findPortal.getBlockX() && (loc2.getBlockZ() - 1) == findPortal.getBlockZ()){
				        			// Portals are synced.
				        			player.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.LIGHT_PURPLE + " Portals are linked.");
				        		}else{
				        			player.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.LIGHT_PURPLE + " Portal NOT linked.");
				        			player.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.LIGHT_PURPLE + " Build sister portal at x:" + loc2.getBlockX()
				        					+ " z:" + loc2.getBlockZ());
				        		}	
			        		}
			        	}else{
			        		log("Portal not found.");
			        		//player.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.LIGHT_PURPLE + "Portal NOT linked.");
		        			player.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.LIGHT_PURPLE + " Build sister portal at x:" + loc2.getBlockX()
		        					+ " z:" + loc2.getBlockZ());
			        	}
			        	/**if(findPortal(block, loc1)){
			        		log("Portal located.");
			        		// Portals synced.
			        	}else{
			        		log("No sister Portal.");
			        		// Portals not synced, notify player.
			        		player.sendMessage("Sister portal not found.");
			        		Location loc2 = null;
			        		List<World> worlds = Bukkit.getWorlds();
			        		if(getSisterWorld(loc1) == "Overworld"){
			        			loc2 = new Location (worlds.get(0), Math.floor(loc1.getBlockX() * 8), loc1.getBlockY(), Math.floor(loc1.getBlockZ() * 8));
			        		}else if(getSisterWorld(loc1) == "Nether"){
			        			loc2 = new Location (worlds.get(1), loc1.getBlockX() / 8, loc1.getBlockY(), loc1.getBlockZ() / 8);
			        		}
			        		try{
			        			player.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.LIGHT_PURPLE + " Sister portal should be built at x:" + loc2.getBlockX() + " y:" + loc2.getBlockY() + " z:" + loc2.getBlockZ() + " in the " + getSisterWorld(loc1));
			        		}catch (Exception e){
			        			
			        		}
			        		
			        	}*/
				         
			        	//if(player.hasPermission("ph.hermits.portal")){
			                 
			                //    event.setCancelled(false);
			                //}else{            
			                    //event.setCancelled(false);
			                    //loc1.getWorld().createExplosion(loc1, 5);
			                    //block.getWorld().createExplosion(loc1, 5);
			                //}
			        }
		        }
	        }
        }catch (Exception e){
        	//e.printStackTrace();
        }
        
    }
    
    @EventHandler
    public void onPortalEnter(PlayerPortalEvent event){
     
        Player player = event.getPlayer();
     
        if (player.hasPermission("ph.hermits.enter")){
            //event.setCancelled(false);
     
        }else{
	        //event.setCancelled(true);
	        Location loc = event.getPlayer().getLocation();
	        loc.getWorld();
	        
	        //loc.getWorld().createExplosion(loc, 5);
	        //event.setCancelled(true);
	        //player.sendMessage(ChatColor.DARK_PURPLE + "The portal has been overloaded by your stupidness!");
        }
     
    }
    
    @EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
	    Player p = event.getPlayer();
	    if(p.isOp() && UpdateCheck){	
			try {
			
				URL url = new URL("https://raw.githubusercontent.com/JoelGodOfwar/PortalHelper/master/versions/1.12/version.txt");
				final URLConnection conn = url.openConnection();
	            conn.setConnectTimeout(5000);
	            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            final String response = reader.readLine();
	            final String localVersion = this.getDescription().getVersion();
	            if(debug){log("response= ." + response + ".");} //TODO: Logger
	            if(debug){log("localVersion= ." + localVersion + ".");} //TODO: Logger
	            if (!response.equalsIgnoreCase(localVersion)) {
					p.sendMessage(ChatColor.DARK_PURPLE + this.getName() + ChatColor.RED + " New version available!");
				}
			} catch (MalformedURLException e) {
				log("MalformedURLException");
				e.printStackTrace();
			} catch (IOException e) {
				log("IOException");
				e.printStackTrace();
			}catch (Exception e) {
				log("Exception");
				e.printStackTrace();
			}
		}
	    if(p.getDisplayName().equals("JoelYahwehOfWar")){
	    	p.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
	    }
	}
    
    public boolean findPortal(Block block, Location loc1){
    	List<World> worlds = Bukkit.getWorlds();
    	/** 
    	 *  World 0 = Overworld
    	 *  World 1 = Nether
    	 *  World 2 = End
    	 */ 
        if(worlds.get(0).equals(loc1.getWorld())){             // We're in the Overworld
        	double X = Math.floor(loc1.getBlockX() / 8);                          // so check the nether for
        	double Y = loc1.getBlockY();                              // a sister portal
        	double Z = Math.floor(loc1.getBlockZ() / 8);                          // check same xyz
        	Location loc2 = new Location(worlds.get(1), X, Y, Z);
        	Block block2 = worlds.get(1).getBlockAt(loc2);  // check one below and one above xyz
        	if(block2.getType().equals(Material.PORTAL)){
        		log("block2 true");
        		return true;
        	}
        	log(loc2.getBlockX() + " " + loc2.getBlockY() + " " + loc2.getBlockZ() + " loc2");
        	log(block2.getX() + " " + block2.getY() + " " + block2.getZ() + " block2 " + block2.getType());
        	Location loc3 = new Location(worlds.get(1), X, Y + 1, Z);
        	Block block3 = worlds.get(1).getBlockAt(loc3);
        	if(block3.getType().equals(Material.PORTAL)){
        		log("block3 true");
        		return true;
        	}
        	log(loc3.getBlockX() + " " + loc3.getBlockY() + " " + loc3.getBlockZ() + " loc3");
        	log(block3.getX() + " " + block3.getY() + " " + block3.getZ() + " Block3 " + block3.getType());
        	Location loc4 = new Location(worlds.get(1), X, Y + 2, Z);
        	Block block4 = worlds.get(1).getBlockAt(loc4);
        	if(block4.getType().equals(Material.PORTAL)){
        		log("block4 true");
        		return true;
        	}
        	log(loc4.getBlockX() + " " + loc4.getBlockY() + " " + loc4.getBlockZ() + " loc4");
        	log(block4.getX() + " " + block4.getY() + " " + block4.getZ() + " Block4 " + block4.getType());
        	Location loc5 = new Location(worlds.get(1), X, Y - 1, Z);
        	Block block5 = worlds.get(1).getBlockAt(loc5);
        	if(block5.getType().equals(Material.PORTAL)){
        		log("block5 true");
        		return true;
        	}
        	log(loc5.getBlockX() + " " + loc5.getBlockY() + " " + loc5.getBlockZ() + " loc5");
        	log(block5.getX() + " " + block5.getY() + " " + block5.getZ() + " Block5 " + block5.getType());
        	Location loc6 = new Location(worlds.get(1), X, Y - 2, Z);
        	Block block6 = worlds.get(1).getBlockAt(loc6);
        	if(block6.getType().equals(Material.PORTAL)){
        		log("block6 true");
        		return true;
        	}
        	log(loc6.getBlockX() + " " + loc6.getBlockY() + " " + loc6.getBlockZ() + " loc6");
        	log(block6.getX() + " " + block6.getY() + " " + block6.getZ() + " block6 " + block6.getType());
        }
        if(worlds.get(1).equals(loc1.getWorld())){             // We're in the Nether
        	double X = Math.floor(block.getX() * 8);                          // so check the Overworld for
        	double Y = block.getY();                              // a sister portal
        	double Z = Math.floor(block.getZ() * 8);                          // check same xyz
        	Location loc7 = new Location(worlds.get(0), X, Y, Z);
        	Block block2 = worlds.get(0).getBlockAt(loc7);  // check one below and one above xyz
        	Chunk chunk = worlds.get(0).getChunkAt(block2);
        	int maxY = 100;
        	log("chunk x:" + chunk.getX() + " z:" + chunk.getZ());
        	for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < maxY; y++) {
                        if (chunk.getBlock(x, y, z).getType() == Material.PORTAL) {
                        	log("true");
                        	return true;
                        }
                    }
                }
        	}    
        	log("False");
        	return false;
        }
    	return false;
    }
    
    public String getSisterWorld(Location loc1){
    	List<World> worlds = Bukkit.getWorlds();
    	/** 
    	 *  World 0 = Overworld
    	 *  World 1 = Nether
    	 *  World 2 = End
    	 */ 
        if(worlds.get(0).equals(loc1.getWorld())){
        	return "Nether";
        }else if(worlds.get(1).equals(loc1.getWorld())){
        	return "Overworld";
        }
    	return null;
    }
    
    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

	    if (cmd.getName().equalsIgnoreCase("PH")||cmd.getName().equalsIgnoreCase("PortalHelper"))
	    {
	    	if (args.length == 0)
	    	{
	    		sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.DARK_PURPLE + "PortalHelper" + ChatColor.GREEN + "]===============[]");
	    		sender.sendMessage(ChatColor.GOLD + " /PH Link - Displays on screen if your current location");
	    		sender.sendMessage(ChatColor.GOLD + "            will properly sync with a nether portal");
			    sender.sendMessage(ChatColor.GOLD + " ");
			    if(sender.isOp()||sender.hasPermission("portalhelper.config")){
			    	sender.sendMessage(ChatColor.GOLD + " OP Commands");				        
			    	sender.sendMessage(ChatColor.GOLD + " /PH Update - Check for update.");//Check for update.");
			    	sender.sendMessage(ChatColor.GOLD + " /PH Reload - Reload Config file." );//Reload config file.");
			    	sender.sendMessage(ChatColor.GOLD + " /PH Check True/False - Set auto update check." );//set auto-update-check to true or false.");
			    }
			    sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.DARK_PURPLE + "PortalHelper" + ChatColor.GREEN + "]===============[]");
			    return true;
	    	}
	    
	    	if(args[0].equalsIgnoreCase("check")){
	    		if(args.length< 1){
					return false;
	    		}
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("portalhelper.config") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
		    	if(!args[1].equalsIgnoreCase("true") & !args[1].equalsIgnoreCase("false")){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " §c" + "Argument must be boolean. Usage: /PH Check True/False");
					return false;
		    	}else if(args[1].contains("true") || args[1].contains("false")){
		    		FileConfiguration config = getConfig();
					config.set("auto-update-check", "" + args[1]);
					saveConfig();
					ConfigAPI.Reloadconfig(this);
					sender.sendMessage(ChatColor.YELLOW + this.getName() + " " + " " + args[1]);
					if(args[1].contains("false")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " Will not check for updates." );
					}else if(args[1].contains("true")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + " Will check for updates." );
					}
					reloadConfig();
					return true;
				}
	    	}
	    	if(args[0].equalsIgnoreCase("reload")){
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("portalhelper.config") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
	            ConfigAPI.Reloadconfig(this);
	    	}
	    	if(args[0].equalsIgnoreCase("update")){
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("portalhelper.config") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
	            }
	            /** Command code */
				try {
					
					URL url = new URL("https://raw.githubusercontent.com/JoelGodOfwar/PortalHelper/master/version.txt");
					final URLConnection conn = url.openConnection();
					conn.setConnectTimeout(5000);
					final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					final String response = reader.readLine();
					final String localVersion = this.getDescription().getVersion();
					if(debug){log("response= ." + response + ".");} //TODO: Logger
					if(debug){log("localVersion= ." + localVersion + ".");} //TODO: Logger
					if (!response.equalsIgnoreCase(localVersion)) {
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " New version available!");
					}else{
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.GREEN + " Version is up to date." );
					}
				} catch (MalformedURLException e) {
					log(this.getName() + " MalformedURLException");
					e.printStackTrace();
				} catch (IOException e) {
					log(this.getName() + " IOException");
					e.printStackTrace();
				}catch (Exception e) {
					log(this.getName() + " Exception");
					e.printStackTrace();
				}
					
				
	    	}
	    	if(args[0].equalsIgnoreCase("link")){
	    		if(args.length< 1){
					return false;
	    		}
	    		/** Check if player has permission */
	            Player player = null;
	            if (sender instanceof Player) {
	                player = (Player) sender;
	                if (!player.hasPermission("portalhelper.linkcommand") && !player.isOp()) {
	                    player.sendMessage(ChatColor.DARK_RED + "You do not have permission for this command!");
	                    return true;
	                }
		            /** Command code */
			    	List<World> worlds = Bukkit.getWorlds();
			        /** 
			         *  World 0 = Overworld
			         *  World 1 = Nether
			         *  World 2 = End
			         */ 
			        if(worlds.get(0).equals(player.getWorld())){             // We're in the Overworld
			        Location loc = player.getLocation();
			        int X = loc.getBlockX();
			        int Y = loc.getBlockY();
			        int Z = loc.getBlockZ();
			        double X2 = Math.floor(X / 8);
			        double Z2 = Math.floor(Z / 8);
			        double X3 = (X2 * 8);
			        double Z3 = (Z2 * 8);
			        player.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.WHITE + " You're standing on X: " + X + " Y: " + Y + " Z: " + Z);
			        //p.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.WHITE + " Nether: (" + X + " / 8) = " + X2 + " Y = " + Y + " (" + Z + " / 8) = " + Z2);
			        //p.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.WHITE + " OverWorld: (" + X2 + " * 8) = " + X3 + " Y = " + Y + " (" + Z2 + " * 8) = " + Z3);
			        player.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.YELLOW + " Overworld portal should be at " + ChatColor.RESET + "X: " + X3 + " Z: " + Z3);
			        player.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.RED + " Nether portal should be at " + ChatColor.RESET + "X: " + X2 + " Z: " + Z2);
			        }
			        if(worlds.get(1).equals(player.getWorld())){             // We're in the Nether
			        	Location loc = player.getLocation();
			        	int X = loc.getBlockX();
			        	int Y = loc.getBlockY();
			        	int Z = loc.getBlockZ();
			        	double X2 = (X * 8);
			        	double Z2 = (Z * 8);
			        	double X3 = (X2 / 8);
			        	double Z3 = (Z2 / 8);
			        	player.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.WHITE + " You're standing on X: " + X + " Y: " + Y + " Z: " + Z);
			           	//p.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.WHITE + " Overworld: (" + X + " * 8) = " + X3 + " Y = " + Y + " (" + Z + " * 8) = " + Z3);
			        	player.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.YELLOW + " Overworld portal should be at " + ChatColor.RESET + "X: " + X2 + " Z: " + Z2);
			        	player.sendMessage(ChatColor.DARK_PURPLE + "PH" + ChatColor.RED + " Nether portal should be at " + ChatColor.RESET + "X: " + X3 + " Z: " + Z3);
			            	
			        }
					return true;
	    		}else{
	    			sender.sendMessage(ChatColor.DARK_RED + "This command cannot be used by the Console.");
	    			return true;
	    		}
	    	}
	    	
	    }
	    
	    return true;
	}
    
    public static String getVersion() {
		String strVersion = Bukkit.getVersion();
		strVersion = strVersion.substring(strVersion.indexOf("MC: "), strVersion.length());
		strVersion = strVersion.replace("MC: ", "").replace(")", "");
		return strVersion;
	}
    
}//The End
