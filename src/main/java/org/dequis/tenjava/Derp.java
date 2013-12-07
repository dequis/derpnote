package org.dequis.tenjava;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

public class Derp extends JavaPlugin {
    public void onEnable() {
        this.getLogger().info("Enablified");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("derp")){
            if (!(sender instanceof Player)) {
                sender.sendMessage("pls no");
                return true;
            }

            if (args.length == 0) {
                Location loc = ((Player) sender).getLocation();
                World w = loc.getWorld();
                loc.setY(loc.getY() + 64);
                ItemStack stack = new ItemStack(Material.BOOK);
                ItemMeta meat = stack.getItemMeta();
                meat.setDisplayName("Derpbook");
                stack.setItemMeta(meat);
                w.dropItem(loc, stack);
            }

            sender.sendMessage("Derp successful.");
            return true;
        }
        return false;
    }
}
