package org.dequis.tenjava;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.BookMeta;
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
                ItemStack stack = new ItemStack(Material.BOOK_AND_QUILL);
                ItemMeta meat = stack.getItemMeta();
                meat.setDisplayName("Derpbook");
                ((BookMeta) meat).setPages(
                    "Rules of Fight Club\n" +
                    "1st RULE: You do not talk about FIGHT CLUB.\n" +
                    "2nd RULE: You DO NOT talk about FIGHT CLUB.\n" +
                    "3rd RULE: If someone says \"stop\" or goes limp, taps out the fight is over.\n" +
                    "4th RULE: Only two guys to a fight.\n" +
                    "5th RULE: One fight at a time.\n",
                    "6th RULE: No shirts, no shoes.\n" +
                    "7th RULE: Fights will go on as long as they have to.\n" +
                    "8th RULE: If this is your first night at FIGHT CLUB, you HAVE to fight.\n"
                );
                stack.setItemMeta(meat);
                w.dropItem(loc, stack);
            }

            sender.sendMessage("Derp successful.");
            return true;
        }
        return false;
    }
}
