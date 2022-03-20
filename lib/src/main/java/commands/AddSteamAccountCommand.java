package commands;

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
// $addSteamAccount [alias] [steam link]
public class AddSteamAccountCommand extends ListenerAdapter {
    MongoClient client = MongoClients.create("db");
    MongoDatabase db = client.getDatabase("DiscordDB");
    MongoCollection<Document> steamids = db.getCollection("steamids");
    
	@Override
	public void onMessageReceived( MessageReceivedEvent event ) {
        if ( event.getAuthor().isBot() ) return;

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if ( args[0].equalsIgnoreCase("$addsteamaccount")) {
            try {
                Document found = steamids.find(new Document("alias", args[1])).first();
                if ( found == null ) {
                    Document add = new Document("alias", args[1])
                        .append("steamid", args[2]);
                    steamids.insertOne(add);
                    event.getChannel().sendMessage("Successfully added steam id").queue();
                }
                else {
                    steamids.updateOne(Filters.eq("alias", args[1]), Updates.set("steamid", args[2]));
                    event.getChannel().sendMessage("Successfully updated steam id").queue();
                }
            }
            catch ( Exception e ) {
                event.getChannel().sendMessage("Error: " + e.toString() + "\nSyntax: $addsteamaccount [alias] [steam id]").queue();
            }
        }
	}
}