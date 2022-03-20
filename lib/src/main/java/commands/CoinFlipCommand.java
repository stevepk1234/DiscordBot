package commands;

import java.util.Random;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// @author Steve
public class CoinFlipCommand extends ListenerAdapter {
	@Override
	public void onMessageReceived( MessageReceivedEvent event ) {
		if ( event.getAuthor().isBot() ) return;
		
		if ( event.getMessage().getContentRaw().equals("$coinflip")) {
			Random r = new Random();
			String side = r.nextDouble() < .5 ? "heads" : "tails";
			
			event.getChannel().sendMessage(side).queue();
		}
	}
}
