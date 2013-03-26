package net.beastware.ArmorCheck;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.*;

import net.worldoftomorrow.noitem.NoItem;
import net.worldoftomorrow.noitem.permissions.Perm;
import net.worldoftomorrow.noitem.permissions.PermMan;
import net.worldoftomorrow.noitem.util.Messenger;
import net.worldoftomorrow.noitem.util.Messenger.AlertType;

public final class ArmorCheck extends JavaPlugin {
	
	PermMan perms;
	
	
    @Override
    public void onEnable(){
        // TODO Insert logic to be performed when the plugin is enabled
    	
    	perms = NoItem.getPermsManager();
    }
 
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("checkarmor") && args.length == 1) { 
    		//Player player = (Player) sender;
    		Player player = this.getServer().getPlayer(args[0]);
    		
    		if (player == null)
    		{
    			sender.sendMessage("Player does not exist.");
    			return false;
    		}
    		
    		if (!player.isOnline())
    		{
    			sender.sendMessage("Player must be online.");
    			return false;
    		}
    		
    		sender.sendMessage("Checking armor for " + player.getName() + ".");
    		checkArmor(player);
    		return true;
    	} 
    	
    	return false; 
    }
    
    public void checkArmor(Player player) {
    	Player p = player;
		PlayerInventory inv = p.getInventory();
		Boolean foundPerm = false;
		
		// used to update the player's armor
		ItemStack[] playerArmor = inv.getArmorContents();
    	
		// used for items that won't fit in the player's inventory
		ArrayList<ItemStack> armorToDrop = new ArrayList<ItemStack>();
    	
		// go through and find out which armor needs to be removed
		for(int i = 0; i < playerArmor.length; i++) {
			ItemStack armorPiece = playerArmor[i];
			if (armorPiece != null && perms.has(p, Perm.WEAR, armorPiece)) {
				foundPerm = true;
				playerArmor[i] = null;
    			
				// put the armor in the inventory or 
				//   add it to our drop list if inventory is full
				armorToDrop.addAll((inv.addItem(armorPiece).values()));
    			
    			
				Messenger.sendMessage(p, AlertType.WEAR, armorPiece);
				Messenger.alertAdmins(p, AlertType.WEAR, armorPiece);
			}
		}

		// don't bother doing this if everything was all good
		if (foundPerm) {
			// update player's armor
			inv.setArmorContents(playerArmor);
	    	
			// drop the items that were not able to be added to the inventory
			if (armorToDrop.size() > 0) {
				// get location for drops
				Location loc = p.getLocation();
				World world = loc.getWorld();
				
				for(ItemStack item : armorToDrop) {
					world.dropItemNaturally(loc, item);
				}
			}
		}
    }
}
