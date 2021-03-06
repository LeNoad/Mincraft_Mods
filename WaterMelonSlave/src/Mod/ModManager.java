package Mod;

import java.sql.SQLException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import DB.ConnectManager;
import DTO.HomeDTO;
import Discord.BotChatEvent;
import Main.WaterMelonSlave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModManager extends ListenerAdapter implements Listener {
	private static String token = "ODU2ODM2NjI5MDI0MDc5ODgy.G0l4_-.e8zfOHeEmtByQDCYfBEGd5Yx26MFDqhmu5OIU4";
	private JDABuilder builder;
	private JDA jda;
	private WaterMelonSlave melonSlave;
	private TextChannel minecraft_tc;
	private ConnectManager cm;

	public ModManager(WaterMelonSlave watermelonSlave) {
		this.melonSlave = watermelonSlave;
		startBot();
		melonSlave.getServer().getPluginManager().registerEvents(this, melonSlave);
		jda.addEventListener(this);
	}

	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent e) {
		minecraft_tc = jda.getTextChannelsByName("minecraft_chat", true).get(0);
		if (e.getMessage().charAt(0) == '#') {
			String[] str = e.getMessage().substring(1).split(" ");
			if (str[0].equals("home")) {
				new BukkitRunnable() {
					@Override
					public void run() {
						Player player = e.getPlayer();
						cm = new ConnectManager();
						try {
							HomeDTO home = cm.goHome(e.getPlayer().getUniqueId().toString(), str[1]);
							WorldCreator wc = new WorldCreator(home.getWorld());
							Location loc = new Location(Bukkit.createWorld(wc), home.getX().intValue(),
									home.getY().intValue(), home.getZ().intValue());
							player.teleport(loc);
							e.getPlayer().sendMessage(ChatColor.GREEN + "????????? ????????? ???????????????.");
						} catch (NullPointerException e2) {
							e.getPlayer().sendMessage(ChatColor.RED + "????????? ????????? ????????????.");
						} catch (StringIndexOutOfBoundsException e2) {
							e.getPlayer().sendMessage(ChatColor.RED + "????????? ????????? ????????????.");
						} catch (Exception e2) {
							e.getPlayer().sendMessage(ChatColor.RED + "????????? ????????? ????????????.");
						}
					}
				}.runTask(melonSlave);
			} else if (str[0].equals("set")) {
				try {
					Player player = e.getPlayer();
					HomeDTO home = new HomeDTO();
					cm = new ConnectManager();
					home.setUUID(e.getPlayer().getUniqueId().toString());
					home.setCommand(str[1]);
					home.setX(player.getLocation().getX());
					home.setY(player.getLocation().getY());
					home.setZ(player.getLocation().getZ());
					home.setWorld(player.getWorld().getName());
					if (cm.saveHome(home)) {
						e.getPlayer().sendMessage(ChatColor.GREEN + "?????? ????????? ???????????????.");
					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "?????????????????? ???????????? ????????? ???????????????.");
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (Exception e2) {
					e.getPlayer().sendMessage(ChatColor.RED + "????????? ????????? ????????????.");
				}

			} else if (str[0].equals("list")) {
				ConnectManager cm = new ConnectManager();
				List<HomeDTO> list = cm.homeList(e.getPlayer().getUniqueId().toString());
				if (!list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						e.getPlayer().sendMessage(ChatColor.GOLD + "????????? : " + list.get(i).getCommand() + ChatColor.GREEN
								+ " [ World : "+list.get(i).getWorld()+ " X : " + list.get(i).getX().intValue() + " Y : " + list.get(i).getY().intValue()
								+ " Z : " + list.get(i).getZ().intValue() + " ]");
					}
				} else {
					e.getPlayer().sendMessage(ChatColor.RED + "?????????????????? ????????? ????????????.");
				}
			} else if(str[0].equals("delete")) {
				ConnectManager cm = new ConnectManager();
				
				try {
					if(cm.deleteHome(e.getPlayer().getUniqueId().toString(), str[1])) {
						e.getPlayer().sendMessage(ChatColor.GREEN + "????????? ????????? ???????????????.");
					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "?????????????????? ????????? ????????????.");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e.getPlayer().sendMessage(ChatColor.RED + "?????????????????? ????????? ????????????.");
				}
				
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + "?????? ???????????? ????????????.");
			}
		} else {
			minecraft_tc.sendMessage(e.getPlayer().getName() + " : " + e.getMessage()).queue();
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event) {
		event.setJoinMessage(ChatColor.AQUA + "[Discord:????????????] ?????????????????? v1.19 ????????????\n" + ChatColor.GOLD
				+ event.getPlayer().getName() + " ?????? ?????????????????????.");
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		minecraft_tc = jda.getTextChannelsByName("minecraft_chat", true).get(0);
		int x = event.getEntity().getLocation().getBlockX();
		int y = event.getEntity().getLocation().getBlockY();
		int z = event.getEntity().getLocation().getBlockZ();
		event.getEntity().sendMessage(ChatColor.GREEN + "????????????\nX: " + x + " Y: " + y + " Z: " + z);
		minecraft_tc.sendMessage(event.getEntity().getPlayer().getName()+"?????? ????????????\nX: " + x + " Y: " + y + " Z: " + z).queue();
	}

	public void startBot() {
		builder = JDABuilder.createDefault(token);
		builder.setAutoReconnect(true);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.addEventListeners(new BotChatEvent());

		try {
			jda = builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
}
