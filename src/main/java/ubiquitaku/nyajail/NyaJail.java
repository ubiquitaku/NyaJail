package ubiquitaku.nyajail;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class NyaJail extends JavaPlugin implements Listener {
    FileConfiguration config;
    int x,y,z;
    String w;
    Location l;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();
        x = config.getInt("x",0);
        y = config.getInt("y",70);
        z = config.getInt("z",0);
        w = config.getString("w","world");
        saveConfig();
        l = new Location(getServer().getWorld(w),x,y,z);
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("njail")) {
            if (!(sender.isOp())) {
                sender.sendMessage("なに入力しとんねん整地すんぞ");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage("set : Jailの場所を設定します\njoin <プレイヤー名> 罪 : そのプレイヤーをJailに突っ込みます\nunjail <プレイヤー名> : そのプレイヤーを釈放します");
                return true;
            }
            if (args[0].equals("join")) {
                if (args.length != 3) {
                    sender.sendMessage("argsの数が間違っています");
                    return true;
                }
                Player s = null;
                s = getServer().getPlayer(args[1]);
                if (s == null) {
                    sender.sendMessage("プレイヤー名が間違っているか今サーバーに居ないプレイヤーです");
                    return true;
                }
                if (config.contains(args[1])) {
                    sender.sendMessage("そのプレイヤーは既にJailされています");
                    return true;
                }
                config.set(args[1],args[2]);
                Bukkit.broadcastMessage("§c§l"+args[1]+"は"+sender.getName()+"によりJAilされました§r\n罪: "+args[2]);
                saveConfig();
                return true;
            }
            if (args[0].equals("unjail")) {
                if (args.length != 2) {
                    sender.sendMessage("argsの数が間違っています");
                    return true;
                }
                Player s = null;
                s = getServer().getPlayer(args[1]);
                if (s == null) {
                    sender.sendMessage("プレイヤー名が間違っているか今サーバーに居ないプレイヤーです");
                    return true;
                }
                if (!(config.contains(args[1]))) {
                    sender.sendMessage("そのプレイヤーはJailされていません");
                    return true;
                }
                config.set(args[1],null);
                saveConfig();
                sender.sendMessage(args[1]+"を釈放しました");
            }
            if (args[0].equals("set")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("consoleからの実行はできません");
                    return true;
                }
                Player p = (Player) sender;
                config.set("x", p.getLocation().getX());
                config.set("y", p.getLocation().getY());
                config.set("z", p.getLocation().getZ());
                config.set("w", p.getLocation().getWorld().getName());
                saveConfig();
                l = p.getLocation();
                sender.sendMessage("座標を保存しました");
                return true;
            }
        }
        return true;
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (config.contains(e.getPlayer().getName())) {
            e.getPlayer().sendMessage("あなたはjailされているためjailに飛ばされます");
            e.getPlayer().teleport(l);
            return;
        }
    }

    @EventHandler
    public void res(PlayerRespawnEvent e) {
        if (config.contains(e.getPlayer().getName())) {
            e.getPlayer().sendMessage("あなたは死んだのでjailに戻されました");
            e.setRespawnLocation(l);
        }
    }
}
