package commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// @author Steve
//$getsteamid [alias]
public class GetSteamIDCommand extends ListenerAdapter {
    MongoClient client = MongoClients.create("db");
    MongoDatabase db = client.getDatabase("DiscordDB");
    MongoCollection<Document> steamids = db.getCollection("steamids");

    @Override
    public void onMessageReceived( MessageReceivedEvent event ) {
        if ( event.getAuthor().isBot() ) return;

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if ( args[0].equalsIgnoreCase("$getsteamid")) {
            try {
                Document found = steamids.find(new Document("alias", args[1])).first();
                if ( found != null ) {
                    String id = (String)found.get("steamid");
                    event.getChannel().sendMessage(id).queue();
                }
                else {
                    event.getChannel().sendMessage("No such alias exists");
                }
            }
            catch ( Exception e ) {
                event.getChannel().sendMessage("Error: " + e.toString() + "\nProper syntax: $getsteamid [alias]").queue();
            }
        }
    }

    
}
