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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Item;
import java.util.HashSet;
import java.util.List;

public class Derp extends JavaPlugin implements Listener{
    final static String LOL_BOOK_IDENTIFIER = "derpbook";
    final static int A_LOT = 2147483647;

    private Item book;
    private Item fire;
    private Skeleton skeleton;
    private HashSet<String> seenLines;

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.seenLines = new HashSet<String>();
        this.getLogger().info("Enablified");
    }

    public void onDisable() {
        this.nukeFireAndSkeletonAndBook();
        this.seenLines.clear();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("derp")){
            if (!(sender instanceof Player)) {
                sender.sendMessage("pls no");
                return true;
            }

            Location loc = ((Player) sender).getLocation();
            World world = loc.getWorld();
            if (args.length == 0) {
                loc.setY(loc.getY() + 64);
                ItemStack stack = new ItemStack(Material.BOOK_AND_QUILL);
                ItemMeta meat = stack.getItemMeta();
                meat.setDisplayName(LOL_BOOK_IDENTIFIER);
                ((BookMeta) meat).setPages(
                    "~derpbook~"
                );
                stack.setItemMeta(meat);
                book = world.dropItem(loc, stack);
             } else if (args.length == 1 && args[0].equals("2")) {
                loc.setX(loc.getX() + 5);
                loc.setY(loc.getY() + 30);

                fire = world.dropItem(loc, new ItemStack(Material.FIRE));
                skeleton = (Skeleton) world.spawnEntity(loc, EntityType.SKELETON);

                fire.setPickupDelay(A_LOT);
                skeleton.setMaxHealth(A_LOT);
                skeleton.setHealth(A_LOT);
                skeleton.setNoDamageTicks(A_LOT);
                skeleton.setSkeletonType(SkeletonType.WITHER);
                skeleton.setTarget((Player) sender);

                fire.setPassenger(skeleton);

                sender.sendMessage("§7§o<skeleton> my butt is warm");

                return true;
             }

            sender.sendMessage("Derp successful.");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        final Player player = event.getPlayer();
        final BookMeta oldMeta = event.getPreviousBookMeta();
        final BookMeta newMeta = event.getNewBookMeta();

        if (this.isThisBookLegit(newMeta)) {
            if (event.isSigning()) {
                event.setCancelled(true);
                this.stealBookFromInventory(player, event.getSlot());
                this.nukePlayerAfterAWhile(player, 1);
            } else {
                this.scanLines(newMeta);
            }
        }

    }

    private void nukePlayerAfterAWhile(final Player player, int awhile) {
        player.sendMessage("gg no re");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.getInventory().clear();
                player.setHealth(0);
            }
        }, awhile * 20);
    }

    private void stealBookFromInventory(final Player player, final int slot) {
        // gotta use the scheduler because the client crashes otherwise kek
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.getInventory().clear(slot);
            }
        }, 0);
    }

    private boolean isThisBookLegit(BookMeta bookmeta) {
        // TODO insecure as fuck
        return bookmeta.getDisplayName().equals(LOL_BOOK_IDENTIFIER);
    }

    private void nukeShit(Entity e) {
        // i like functions
        if (e != null && e.isValid()) {
            e.remove();
        }
    }

    private void nukeFireAndSkeletonAndBook() {
        nukeShit((Entity) fire);
        nukeShit((Entity) skeleton);
        nukeShit((Entity) book);
        fire = book = null;
        skeleton = null;
    }

    private void scanLines(BookMeta meat) {
        List<String> pages = meat.getPages();
        for (String page : pages) {
            for (String line : page.split("\\n")) {
                if (!line.startsWith("~") && line.length() > 1 && !this.seenLines.contains(line) ) {
                    this.seeNewLine(line);
                }
            }
        }
    }

    private void seeNewLine(String line) {
        this.seenLines.add(line);
        this.getLogger().info("see new line: " + line);
        /* TODO: do horrible things here */
    }
}
