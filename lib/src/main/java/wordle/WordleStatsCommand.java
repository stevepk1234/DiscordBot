package wordle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// @author Steve
// $wordlestats
public class WordleStatsCommand extends ListenerAdapter {
    MongoClient client = MongoClients.create("db");
    MongoDatabase db = client.getDatabase("DiscordDB");
    MongoCollection<Document> wordlePlayers = db.getCollection("wordletracker");

    @Override
    public void onMessageReceived( MessageReceivedEvent event ) {
        if ( event.getAuthor().isBot() ) return;

        if ( event.getMessage().getContentRaw().equalsIgnoreCase("$wordlestats")) {
            try {
                String user = event.getAuthor().getName();
                Document found = wordlePlayers.find(new Document("user", user)).first();

                if ( found == null ) {
                    event.getChannel().sendMessage("Cannot find stats for " + user).queue();
                }
                else {
                    double avg = (double)found.get("average");
                    String average = "" + avg;
                    
                    if ( average.length() >= 4 ) {
                        average = average.substring(0, 3);
                    }

                    int count = (int)found.get("count");
                    StringBuilder sb = new StringBuilder();
                    sb.append("Stats for ")
                        .append(user)
                        .append(": \nAverage line completed: ")
                        .append(average)
                        .append("\nNumber of games completed: ")
                        .append(count);
                    event.getChannel().sendMessage(sb.toString()).queue();
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
