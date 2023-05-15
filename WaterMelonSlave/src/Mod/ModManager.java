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
import DTO.BackDTO;
import DTO.HomeDTO;
import Discord.BotChatEvent;
import Main.WaterMelonSlave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModManager extends ListenerAdapter implements Listener {
	private static String token = "ODU2ODM2NjI5MDI0MDc5ODgy.G8NA82.fEd7gva_2oQWD3Sr6IZ6iAZjGTDfuTfG60JxMQ";
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
							e.getPlayer().sendMessage(ChatColor.GREEN + "지정된 장소로 이동합니다.");
						} catch (NullPointerException e2) {
							e.getPlayer().sendMessage(ChatColor.RED + "저장된 장소가 없습니다.");
						} catch (StringIndexOutOfBoundsException e2) {
							e.getPlayer().sendMessage(ChatColor.RED + "저장된 장소가 없습니다.");
						} catch (Exception e2) {
							e.getPlayer().sendMessage(ChatColor.RED + "저장된 장소가 없습니다.");
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
						e.getPlayer().sendMessage(ChatColor.GREEN + "현재 장소를 저장합니다.");
					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "저장되어있는 장소명과 동일한 이름입니다.");
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (Exception e2) {
					e.getPlayer().sendMessage(ChatColor.RED + "저장된 장소가 없습니다.");
				}

			} else if (str[0].equals("list")) {
				ConnectManager cm = new ConnectManager();
				List<HomeDTO> list = cm.homeList(e.getPlayer().getUniqueId().toString());
				if (!list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						e.getPlayer().sendMessage(ChatColor.GOLD + "장소명 : " + list.get(i).getCommand() + ChatColor.GREEN
								+ " [ World : "+list.get(i).getWorld()+ " X : " + list.get(i).getX().intValue() + " Y : " + list.get(i).getY().intValue()
								+ " Z : " + list.get(i).getZ().intValue() + " ]");
					}
				} else {
					e.getPlayer().sendMessage(ChatColor.RED + "저장되어있는 장소가 없습니다.");
				}
			} else if(str[0].equals("delete")) {
				ConnectManager cm = new ConnectManager();
				
				try {
					if(cm.deleteHome(e.getPlayer().getUniqueId().toString(), str[1])) {
						e.getPlayer().sendMessage(ChatColor.GREEN + "지정한 장소를 삭제합니다.");
					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "저장되어있는 장소가 없습니다.");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e.getPlayer().sendMessage(ChatColor.RED + "저장되어있는 장소가 없습니다.");
				}
				
			} else if(str[0].equals("back")){
				new BukkitRunnable() {
					@Override
					public void run() {
						cm = new ConnectManager();
						Player player = e.getPlayer();
						BackDTO back = cm.goBack(e.getPlayer().getUniqueId().toString());
						WorldCreator wc = new WorldCreator(back.getWorld());
						Location loc = new Location(Bukkit.createWorld(wc), back.getX().intValue(),
								back.getY().intValue(), back.getZ().intValue());
						player.teleport(loc);
						e.getPlayer().sendMessage(ChatColor.GREEN + "마지막으로 죽은 장소로 이동합니다.");
					}
					
				}.runTask(melonSlave);
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + "그런 명령어는 없습니다.");
			}
		} else {
			minecraft_tc.sendMessage(e.getPlayer().getName() + " : " + e.getMessage()).queue();
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event) {
		cm = new ConnectManager();
		if(!cm.selectBack(event.getPlayer().getUniqueId().toString())) {
			event.setJoinMessage(ChatColor.AQUA + "[Discord:븝미월드] 마인크래프트 v1.19.4 플러그인\n" + ChatColor.GOLD
					+ event.getPlayer().getName() + " 님이 입장하셨습니다.");
		} else {
			event.setJoinMessage(ChatColor.BLUE + "[ 첫 접속자 입니다 해당 유저의 권한을 승인합니다 ]");
			new BukkitRunnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					BackDTO backDto = new BackDTO();
					Player player = event.getPlayer();
					backDto.setUUID(player.getUniqueId().toString());
					backDto.setX(player.getLocation().getX());
					backDto.setY(player.getLocation().getY());
					backDto.setZ(player.getLocation().getZ());
					backDto.setWorld(player.getWorld().getName());
					try {
						cm.insertBack(backDto);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						event.setJoinMessage(ChatColor.RED + "[ 유저 권한 승인 실패 ]");
					}
				}
			}.runTask(melonSlave);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
		minecraft_tc = jda.getTextChannelsByName("minecraft_chat", true).get(0);
		BackDTO backDTO = new BackDTO();
		cm = new ConnectManager();
		int x = event.getEntity().getLocation().getBlockX();
		int y = event.getEntity().getLocation().getBlockY();
		int z = event.getEntity().getLocation().getBlockZ();
		backDTO.setX((double) x);
		backDTO.setY((double) y);
		backDTO.setZ((double) z);
		backDTO.setUUID(event.getEntity().getUniqueId().toString());
		backDTO.setWorld(event.getEntity().getWorld().getName().toString());
		if(cm.saveBack(backDTO)) {
			event.getEntity().sendMessage(ChatColor.GREEN + "마지막으로 죽은곳이 저장되었습니다");;
		}
		event.getEntity().sendMessage(ChatColor.GREEN + "사망좌표\nX: " + x + " Y: " + y + " Z: " + z);
		minecraft_tc.sendMessage(event.getEntity().getPlayer().getName()+"님의 사망좌표\nX: " + x + " Y: " + y + " Z: " + z).queue();
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
