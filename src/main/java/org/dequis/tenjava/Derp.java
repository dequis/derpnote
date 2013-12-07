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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Item;
import java.util.HashSet;
import java.util.List;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;

public class Derp extends JavaPlugin implements Listener{
    final static String LOL_BOOK_IDENTIFIER = "Derp Note";
    final static int A_LOT = 2147483647;
    final static int DEFAULT_DEATH_TIMER = 10;
    final static int MAXIMUM_LAST_SEEN = 30 * 20;

    private Item book;
    private Player god;
    private HashSet<String> seenLines;
    private long lastTimeSeen;

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.seenLines = new HashSet<String>();
        this.getLogger().info("Enablified");
    }

    public void onDisable() {
        this.seenLines.clear();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("derp")){
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = null;
            if (args.length == 1) {
                player = this.getServer().getPlayer(args[0]);
                sender.sendMessage("§oSending the notebook to " + player.getName());
            }
            if (player == null) {
                player = (Player) sender;
            }
            this.createANewGod(player);
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
            if (this.god != event.getPlayer()) {
                this.setGod(event.getPlayer());
            }

            if (event.isSigning()) {
                event.setCancelled(true);
                player.sendMessage("You wrote your own name...");
                player.sendMessage("§oYou will die in " + DEFAULT_DEATH_TIMER +" seconds");
                this.stealBookFromInventory(player, event.getSlot());
                this.nukePlayerAfterAWhile(player, DEFAULT_DEATH_TIMER);
            } else {
                this.scanLines(newMeta);
            }
        }

    }

    @EventHandler
    public void onPickup(final PlayerPickupItemEvent event) {
        if (event.getItem().equals(this.book)) {
            event.getPlayer().sendMessage("§oYou feel a strange power coming from this notepad.");
            this.setGod(event.getPlayer());
        }
    }

    private void nukePlayerAfterAWhile(final Player player, int aWhile) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.getInventory().clear();
                Derp.this.getServer().broadcastMessage( player.getName() + " died mysteriously");
                player.setHealth(0);
            }
        }, aWhile * 20);
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
        // security? what's that?
        return bookmeta.getDisplayName().equals(LOL_BOOK_IDENTIFIER);
    }

    private void scanLines(BookMeta meat) {
        List<String> pages = meat.getPages();
        int pageNumber = 0;
        int numKilled = 0;
        for (String page : pages) {
            pageNumber++;
            if (pageNumber == 1) {
                continue;
            }
            for (String line : page.split("\\n")) {
                if (line.length() > 1 && !this.seenLines.contains(line) ) {
                    if (this.seeNewLine(line)) {
                        numKilled++;
                    }
                }
            }
        }
        if (numKilled == 0) {
            this.god.sendMessage("§7§o(Nothing seems to happen...)");
        }
    }

    private boolean seeNewLine(String line) {
        Player player = this.getServer().getPlayer(line);
        if (player != null) {
            if (this.getLastSeen(player) < MAXIMUM_LAST_SEEN) {
                this.god.sendMessage("§o" + player.getName() + " will die in " + DEFAULT_DEATH_TIMER + " seconds");
                player.sendMessage("§oYou start feeling weird...");
                this.nukePlayerAfterAWhile(player, DEFAULT_DEATH_TIMER);
                this.seenLines.add(line);
                return true;
            } else {
                this.god.sendMessage("You haven't seen " + player.getName() + " recently");
            }
        }
        return false;
    }

    private long getLastSeen(Entity entity) {
        List<MetadataValue> values = entity.getMetadata("derp_last_seen");
        for(MetadataValue value : values){
            if (value.getOwningPlugin() == this) {
                return entity.getWorld().getFullTime() - value.asLong();
            }
        }
        return A_LOT;
    }

    private void setLastSeen(Entity entity) {
        entity.setMetadata("derp_last_seen", new FixedMetadataValue(this, entity.getWorld().getFullTime()));
    }

    private void createANewGod(final Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();

        loc.setY(loc.getY() + 64);

        this.book = world.dropItem(loc, this.createBookItemStack());
    }

    private void setGod(final Player player) {
        this.god = player;

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for (Entity e : player.getNearbyEntities(32, 64, 32)) {
                    if (player.hasLineOfSight(e)) {
                        Derp.this.setLastSeen(e);
                    }
                }
            }
        }, 0, 20);
    }

    private ItemStack createBookItemStack() {
        ItemStack stack = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta meat = stack.getItemMeta();
        meat.setDisplayName(LOL_BOOK_IDENTIFIER);
        ((BookMeta) meat).setPages(
            "       DERP NOTE\n" +
            "1. The human whose name is written in this note shall die\n" +
            "2. This note will not take effect unless the writter has seen " +
            "the person in the last 30 seconds\n" +
            "3. The human who uses the notebook can neither go to Heaven nor Hell"
        );
        stack.setItemMeta(meat);
        return stack;
    }

}
