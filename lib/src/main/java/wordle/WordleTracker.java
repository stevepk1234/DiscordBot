package wordle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// @author Steve
// Wordle 270 3/6
public class WordleTracker extends ListenerAdapter {
    MongoClient client = MongoClients.create("db");
    MongoDatabase db = client.getDatabase("DiscordDB");
    MongoCollection<Document> wordlePlayers = db.getCollection("wordletracker");

    @Override
    public void onMessageReceived( MessageReceivedEvent event ) {
        if ( event.getAuthor().isBot() ) return;

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        
        if ( args[0].equals("Wordle")) {
            try {
                String user = event.getAuthor().getName();
                Document found = wordlePlayers.find(new Document("user", user)).first();
                if ( found != null ) {
                    int count = Integer.parseInt((String)found.get("count"));
                    double average = Double.parseDouble((String)found.get("average"));

                    double total = (average * count) + Integer.parseInt(args[2].substring(0, 1));
                    double newAvg = total / (count + 1);

                    wordlePlayers.updateOne(Filters.eq("user", user), Updates.set("count", count + 1));
                    wordlePlayers.updateOne(Filters.eq("user", user), Updates.set("average", newAvg));
                }
                else {
                    Document newUser = new Document("user", user)
                        .append("count", "1")
                        .append("average", args[2].substring(0,1));
                    wordlePlayers.insertOne(newUser);
                }
                event.getChannel().sendMessage("Added results for day: " + args[1]).queue();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
