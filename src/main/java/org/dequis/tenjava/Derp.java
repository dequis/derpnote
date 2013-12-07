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
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.Bukkit;

public class Derp extends JavaPlugin implements Listener{
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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
                    "8th RULE: If this is your first night at FIGHT CLUB, you HAVE to fight.\n",
                    "Don't sign the book, btw"
                );
                stack.setItemMeta(meat);
                w.dropItem(loc, stack);
            }

            sender.sendMessage("Derp successful.");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        final Player player = event.getPlayer();
        if (event.isSigning()) {
            this.nukePlayerAfterAWhile(player, 1);
        }

    }

    private void nukePlayerAfterAWhile(final Player player, int awhile) {
        player.sendMessage("gg no re");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.getEquipment().clear();
                player.setHealth(0);
            }
        }, awhile * 20);
    }
}
