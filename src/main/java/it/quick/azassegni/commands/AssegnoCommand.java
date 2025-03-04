package it.quick.azassegni.commands;

import it.quick.azassegni.AzAssegni;
import it.quick.azassegni.database.DatabaseManager;
import it.quick.azassegni.utils.MoneyUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class AssegnoCommand implements CommandExecutor {
    private final Economy economy;

    public AssegnoCommand(Economy economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo i giocatori possono usare questo comando.");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = AzAssegni.getInstance().getConfig();


        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Uso corretto: /assegno <soldi>");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.invalid_amount")));
            return true;
        }

        if (!economy.has(player, amount)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no_money")));
            return true;
        }

        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (!response.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "Errore nella transazione.");
            return true;
        }

        DatabaseManager.saveAssegno(player.getName(), amount); // Salva nel database

        ItemStack assegno = new ItemStack(Material.PAPER);
        ItemMeta meta = assegno.getItemMeta();
        if (meta != null) {
            String formattedAmount = MoneyUtils.format(amount);
            String displayName = config.getString("assegno.nome").replace("{amount}", formattedAmount);
            List<String> lore = config.getStringList("assegno.lore").stream()
                    .map(line -> line.replace("{player}", player.getName()))
                    .collect(Collectors.toList());

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setLore(lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));

            assegno.setItemMeta(meta);
        }

        player.getInventory().addItem(assegno);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.success").replace("{amount}", MoneyUtils.format(amount))));

        return true;
    }
}
