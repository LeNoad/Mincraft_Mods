package Discord;

import org.bukkit.Bukkit;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotChatEvent extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        TextChannel tc = event.getTextChannel();
        Message msg = event.getMessage();

        if (user.isBot())
            return;
        if(tc.equals(tc.getJDA().getTextChannelsByName("minecraft_chat", true).get(0))){
            tc.sendMessage(msg).queue();
            String MsgContentRaw = msg.getContentRaw();
            String userName = user.getName();
            Bukkit.broadcastMessage(String.format("§9§l[Discord] §f<%s> %s", userName, MsgContentRaw));
        }
    }
}
