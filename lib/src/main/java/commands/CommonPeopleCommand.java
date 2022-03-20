package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

//$commonpeople [game]
public class CommonPeopleCommand extends ListenerAdapter {
    MongoClient client = MongoClients.create("db");
    MongoDatabase db = client.getDatabase("DiscordDB");
    MongoCollection<Document> steamids = db.getCollection("steamids");
    MongoCollection<Document> appids = db.getCollection("appids");

    JSONParser parser = new JSONParser();

    @Override
    public void onMessageReceived( MessageReceivedEvent event ) {
        if ( event.getAuthor().isBot() ) return;

        String message = event.getMessage().getContentRaw();
        if ( message.startsWith("$commonpeople ")) {
            try {
                String game = message.substring(14);
                Document doc = appids.find(new Document("name", game)).first();
                LinkedList<String> owners = new LinkedList<>();

                FindIterable<Document> iterDoc = steamids.find();
                Iterator<Document> it = iterDoc.iterator();
                while ( it.hasNext() ) {
                    Document iterable = it.next();
                    if ( this.ownsGame((String)iterable.get("steamid"), (long)doc.get("appid"))) {
                        owners.add((String)iterable.get("alias"));
                    }
                }
            }
            catch ( Exception e ) {
                event.getChannel().sendMessage("Error: " + e.toString() + "\nSyntax: $commonpeople [game]").queue();
            }
        }
    }

    private boolean ownsGame( String steamid, long id ) throws IOException, ParseException {

        String line = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=7DF80ED8828D8B977724AB7F70771B72&steamid=" 
            + steamid + "&format=json";
        JSONObject ownedGames = readJsonFromUrl(line);

        JSONObject response = (JSONObject)ownedGames.get("response");
        JSONArray games = (JSONArray)response.get("games");

        for ( int i = 0; i < games.size(); i++ ) {
            JSONObject iter = (JSONObject)games.get(i);
            long appid = (long)iter.get("appid");

            if ( id == appid ) {
                return true;
            }
        }
        return false;
    }

    private JSONObject readJsonFromUrl( String line ) throws IOException, ParseException {
        StringBuilder sb = new StringBuilder();

        URL url = new URL(line);
        BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));

        String i;
        while ( (i = read.readLine()) != null ) {
            sb.append(i);
        }
        read.close();

        Object obj = parser.parse(sb.toString());
        return (JSONObject)obj;
    }
}
