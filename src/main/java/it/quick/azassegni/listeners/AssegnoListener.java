package it.quick.azassegni.listeners;

import it.quick.azassegni.AzAssegni;
import it.quick.azassegni.utils.MoneyUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AssegnoListener implements Listener {
    private final Economy economy;

    public AssegnoListener(Economy economy) {
        this.economy = economy;
    }

    @EventHandler
    public void onPlayerUseAssegno(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = AzAssegni.getInstance().getConfig();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.PAPER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName() && meta.hasLore()) {
                String displayName = ChatColor.stripColor(meta.getDisplayName());

                if (displayName.startsWith("ASSEGNO ")) {
                    String amountString = displayName.replace("ASSEGNO ", "").replace("$", "").trim();
                    double amount = MoneyUtils.parse(amountString);

                    economy.depositPlayer(player, amount);
                    player.getInventory().setItemInMainHand(null);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.redeem").replace("{amount}", MoneyUtils.format(amount))));
                }
            }
        }
    }
}
