package lib;

import javax.security.auth.login.LoginException;

import commands.AddSteamAccountCommand;
import commands.CoinFlipCommand;
import commands.CommonGamesCommand;
import commands.CommonPeopleCommand;
import commands.GetSteamIDCommand;
import music.PlayerControl;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import wordle.WordleLeaderboardCommand;
import wordle.WordleStatsCommand;
import wordle.WordleTracker;

// @author Steve
public class DiscordBot {
	
	public static void main( String[] args ) throws LoginException, InterruptedException {
		System.out.println(System.getProperty("user.dir"));
		JDA jda = JDABuilder
				.createDefault("token")
				.setActivity(Activity.playing("Overwatch"))
				.addEventListeners(new CoinFlipCommand())
				.addEventListeners(new PlayerControl())
				.addEventListeners(new AddSteamAccountCommand())
				.addEventListeners(new CommonGamesCommand())
				.addEventListeners(new GetSteamIDCommand())
				.addEventListeners(new WordleTracker())
				.addEventListeners(new WordleStatsCommand())
				.addEventListeners(new WordleLeaderboardCommand())
				.addEventListeners(new CommonPeopleCommand())
				.build();
		jda.awaitReady();
	}  
}
  