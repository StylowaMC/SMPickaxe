package pl.smpickaxe;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Events implements Listener {
    public static HashMap<String, Integer> blockFace = new HashMap<>();
    ItemStack pickaxe1 = CreatePickaxe.newPickaxe("1x2", CustomDiamond.createCustomDiamond(), new ItemStack(Material.STICK), CustomDiamond.createCustomDiamond(), new ItemStack(Material.AIR), new ItemStack(Material.DIAMOND_PICKAXE));
    ItemStack pickaxe2 = CreatePickaxe.newPickaxe("2x2", pickaxe1, pickaxe1, pickaxe1, new ItemStack(Material.AIR),  new ItemStack(Material.DIAMOND_PICKAXE));
    ItemStack pickaxe3 = CreatePickaxe.newPickaxe("2x3", pickaxe2, pickaxe2, pickaxe2, new ItemStack(Material.AIR),  new ItemStack(Material.DIAMOND_PICKAXE));
    ItemStack pickaxe4 = CreatePickaxe.newPickaxe("3x3", pickaxe3, pickaxe3, pickaxe3, new ItemStack(Material.AIR),  new ItemStack(Material.DIAMOND_PICKAXE));
    ItemStack axe = CreatePickaxe.newPickaxe(null, CustomDiamond.createCustomDiamond(), new ItemStack(Material.STICK), new ItemStack(Material.AIR), CustomDiamond.createCustomDiamond(),  new ItemStack(Material.DIAMOND_AXE));

    private void getNearbyBlock(Player p, int uno, int dos, int tres, BlockBreakEvent e) {
        Location a;
        if (EASTorWEST(p) || blockFace.get(p.getName()) == 1) {
            a = e.getBlock().getLocation().add(uno, dos, tres);
        } else {
            a = e.getBlock().getLocation().add(tres, dos, uno);
        }
        Material mat = a.getBlock().getType();
        //p.sendMessage("Sprawdzam blok obok Ciebie:"+mat.toString()+"Lokalizacja:"+loc.toString());
        if (mat.getHardness() < 50 && mat.getHardness() > 0) {
            //p.sendMessage("Hardness"+String.valueOf(loc.getBlock().getType().getHardness()));
            //p.sendMessage("Nie jest twardy");
            if (mat.equals(Material.CHEST) || mat.equals(Material.ENDER_CHEST) || mat.equals(Material.BEACON)) {
                p.sendMessage("Wokół Ciebie są nielegalne bloki!");
            } else {
                //p.sendMessage("Nie jest z czarnej lsity");
                if (e instanceof MojaZajebistaKlasa) {
                    return;
                }
                MojaZajebistaKlasa fakeEvent = new MojaZajebistaKlasa(a.getBlock(), p);
                Bukkit.getPluginManager().callEvent(fakeEvent);

                if (!fakeEvent.isCancelled()) {
                    //Bukkit.broadcastMessage("nie anulowany");
                    a.getBlock().breakNaturally();
                }
                //Bukkit.broadcastMessage("anulowany");


            }
        }
    }

    @EventHandler
    public void BreakDiamondEvent(BlockBreakEvent e) {
        if(!e.isCancelled()) {
            ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
            Block block = e.getBlock();
            if (!block.getDrops(hand).isEmpty()) {
                if (!hand.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                    if (block.getType() == Material.DIAMOND_ORE) {
                        int rand = (int) (Math.random() * (230 - 1 + 1) + 1);
                        if (rand == 151) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "essentials:broadcast "+ ChatColor.BLUE + " " + ChatColor.BOLD + "Gracz " + e.getPlayer().getName() + " wykopał rzadki rubin! Gratulacje!");
                            //Bukkit.broadcastMessage(ChatColor.BLUE + " " + ChatColor.BOLD + "Gracz " + e.getPlayer().getName() + " wykopał rzadki rubin! Gratulacje!");
                            block.getWorld().dropItemNaturally(block.getLocation(), CustomDiamond.createCustomDiamond());
                        }
                    }
                }


            }
        }
    }

    @EventHandler
    public void getBlockFace(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Material itemInHand = p.getInventory().getItemInMainHand().getType();
        BlockFace bFace = e.getBlockFace();
        if (itemInHand.equals(Material.DIAMOND_PICKAXE)) {
            if (bFace == BlockFace.UP || bFace == BlockFace.DOWN) {
                blockFace.put(p.getName(), 1);
            } else {
                blockFace.put(p.getName(), 0);
            }

        }
    }


    @EventHandler
    public void BreakUsingPickaxe(BlockBreakEvent e) {
        if (e instanceof MojaZajebistaKlasa) {
            return;
        }
        Player p = e.getPlayer();

        if (e.isCancelled()) return;
        try {
            int s = getpickaxe(p);
            // Kopanie w dol i gore


            if (blockFace.get(p.getName()) == 1) {
                if (s >= 1) {
                    getNearbyBlock(p, -1, 0, 0, e);
                    if (s >= 2) {
                        getNearbyBlock(p, 0, 0, -1, e);
                        getNearbyBlock(p, -1, 0, -1, e);
                        if (s >= 3) {
                            getNearbyBlock(p, 0, 0, 1, e);
                            getNearbyBlock(p, -1, 0, 1, e);
                            if (s == 4) {
                                getNearbyBlock(p, 1, 0, -1, e);
                                getNearbyBlock(p, 1, 0, 0, e);
                                getNearbyBlock(p, 1, 0, 1, e);

                            }
                        }
                    }
                }
            } else {
                //kopanie na boki
                if (s >= 1) {
                    getNearbyBlock(p, 0, -1, 0, e);
                    if (s >= 2) {
                        getNearbyBlock(p, 0, 0, 1, e);
                        getNearbyBlock(p, 0, -1, 1, e);
                        if (s >= 3) {
                            getNearbyBlock(p, 0, 1, 0, e);
                            getNearbyBlock(p, 0, 1, 1, e);
                            if (s == 4) {
                                getNearbyBlock(p, 0, 1, -1, e);
                                getNearbyBlock(p, 0, 0, -1, e);
                                getNearbyBlock(p, 0, -1, -1, e);

                            }
                        }
                    }
                }
            }

        } catch (NullPointerException ex) {
            return;
        }

    }



    public int getpickaxe(Player p) {
        String itemname = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        if (itemname.equals(pickaxe1.getItemMeta().getDisplayName())) {
            return 1;
        } else if (itemname.equals(pickaxe2.getItemMeta().getDisplayName())) {
            return 2;
        } else if (itemname.equals(pickaxe3.getItemMeta().getDisplayName())) {
            return 3;
        } else if (itemname.equals(pickaxe4.getItemMeta().getDisplayName())) {
            return 4;
        }
        return 0;
    }

    public boolean EASTorWEST(Player p) {
        return p.getFacing() == BlockFace.EAST || p.getFacing() == BlockFace.WEST;
    }

    @EventHandler
    public void BrakingUseAxe(BlockBreakEvent e) {
        try{

            if(!e.isCancelled() && e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(axe.getItemMeta().getDisplayName())){
            Material type = e.getBlock().getType();
            if (type == Material.ACACIA_LOG || type == Material.BIRCH_LOG || type == Material.DARK_OAK_LOG || type == Material.JUNGLE_LOG || type == Material.OAK_LOG || type == Material.SPRUCE_LOG){
                droptree(e.getBlock().getLocation());
                Location saploc = e.getBlock().getLocation().add(0, -1, 0);
                Material sapling = saploc.getBlock().getType();
                if(sapling == Material.DIRT){
                    //sadzenie saplingu
                    Bukkit.getWorld(e.getBlock().getWorld().getName()).getBlockAt(e.getBlock().getLocation()).setType(Material.OAK_SAPLING);
                }
            }
        } else {
            return; }
        } catch (NullPointerException ex){
            return;
        }

    }
    public void droptree(Location l){
        List<Block> blocks = new LinkedList<>();
        for (int i = l.getBlockY(); i < l.getWorld().getHighestBlockYAt(l.getBlockX(), l.getBlockZ()); i++ ) {
            Block loc = Bukkit.getWorld(l.getWorld().getName()).getBlockAt(l.getBlockX(), i, l.getBlockZ());
            Material type = loc.getType();
            if (type == Material.ACACIA_LOG || type == Material.BIRCH_LOG || type == Material.DARK_OAK_LOG || type == Material.JUNGLE_LOG || type == Material.OAK_LOG || type == Material.SPRUCE_LOG) {
                blocks.add(loc);
            } else {
                break;
            }

        }
        for (Block block : blocks){ block.breakNaturally(axe);}
        blocks.clear();
    }

    @EventHandler
    public void anvilname(PrepareAnvilEvent e){
        if(e.getResult() != null) {
            if (e.getResult().getType() == Material.WHITE_DYE) {
                e.setResult(null);
            }
        }
    }

}
