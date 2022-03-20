package wordle;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import org.bson.Document;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// $wordlelb
public class WordleLeaderboardCommand extends ListenerAdapter {
    MongoClient client = MongoClients.create("db");
    MongoDatabase db = client.getDatabase("DiscordDB");
    MongoCollection<Document> wordlePlayers = db.getCollection("wordletracker");

    @Override
    public void onMessageReceived( MessageReceivedEvent event ) {
        if ( event.getAuthor().isBot() ) return;

        if ( event.getMessage().getContentRaw().equalsIgnoreCase("$wordlelb")) {
            String average = byAverage();
            String count = byCount();

            StringBuilder sb = new StringBuilder();
            sb.append(average)
                .append("------------------------\n")
                .append(count);
            
            event.getChannel().sendMessage(sb.toString()).queue();
        }
    }

    private String byAverage() {
        StringBuilder sb = new StringBuilder();
        sb.append("By average: \n");

        List<Document> results = new ArrayList<>();
        wordlePlayers.find().sort(Sorts.ascending("average")).into(results);

        for ( Document user : results ) {
            sb.append((String)user.get("user"))
                .append(": ")
                .append((double)user.get("average"))
                .append("\n");
        }

        return sb.toString();
    }

    private String byCount() {
        StringBuilder sb = new StringBuilder();
        sb.append("By games won: \n");

        List<Document> results = new ArrayList<>();
        wordlePlayers.find().sort(Sorts.ascending("count")).into(results);

        for ( Document user : results ) {
            sb.append((String)user.get("user"))
                .append(": ")
                .append((int)user.get("count"))
                .append("\n");
        }

        return sb.toString();
    }
}
