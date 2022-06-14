package Main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import Mod.ModManager;

public class WaterMelonSlave extends JavaPlugin {
    private ConsoleCommandSender console;
    @Override
    public void onEnable() {
        console = Bukkit.getServer().getConsoleSender();
        console.sendMessage("WaterMelonSlave_Plugin on");
        new ModManager(this);
        
    }

    @Override
    public void onDisable() {
        console.sendMessage("WaterMelonSlave_Plugin off");
    }
}
