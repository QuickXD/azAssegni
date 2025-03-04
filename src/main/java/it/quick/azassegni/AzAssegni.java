package it.quick.azassegni;

import it.quick.azassegni.commands.AssegnoCommand;
import it.quick.azassegni.database.DatabaseManager;
import it.quick.azassegni.listeners.AssegnoListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AzAssegni extends JavaPlugin {
    private Economy economy;
    private static AzAssegni instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        DatabaseManager.connect(); // Avvia il database

        if (!setupEconomy()) {
            getLogger().severe("Vault non trovato! Disabilito il plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("assegno").setExecutor(new AssegnoCommand(economy));
        getServer().getPluginManager().registerEvents(new AssegnoListener(economy), this);
    }
    public static AzAssegni getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        DatabaseManager.close(); // Chiude il database
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }
}
