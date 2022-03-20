package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
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

// @author Steve
// $commongames [name1] [name2] ... [name(n)]
public class CommonGamesCommand extends ListenerAdapter {
    MongoClient client = MongoClients.create("db");
    MongoDatabase db = client.getDatabase("DiscordDB");
    MongoCollection<Document> steamids = db.getCollection("steamids");
    MongoCollection<Document> appids = db.getCollection("appids");

    JSONParser parser = new JSONParser();

    @Override
    public void onMessageReceived( MessageReceivedEvent event ) {
        if ( event.getAuthor().isBot() ) return;
        
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if ( args[0].equalsIgnoreCase("$commongames")) {
            try {
                ArrayList<ArrayList<String>> allUsers = new ArrayList<ArrayList<String>>();
                for ( int i = 1; i < args.length; i++ ) {
                    allUsers.add(getOwnedGames(args[i]));
                }

                HashSet<String> commonGames = new HashSet<>();
                for ( ArrayList<String> list : allUsers ) {
                    commonGames.addAll(list);
                }
                for ( ArrayList<String> list : allUsers ) {
                    commonGames.retainAll(list);
                }

                event.getChannel().sendMessage(commonGames.toString()).queue();
            }
            catch ( Exception e ) {
                event.getChannel().sendMessage("Error: " + e.toString() + "\nSyntax: $commongames [name1] [name2] ... [name(n)]").queue();
            }
        }
    }

    private ArrayList<String> getOwnedGames( String name ) throws IOException, ParseException {
        Document doc = steamids.find(new Document("alias", name)).first();
        
        if ( doc == null ) {
            return new ArrayList<String>();
        }

        String line = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=7DF80ED8828D8B977724AB7F70771B72&steamid=" 
            + (String)doc.get("steamid") + "&format=json";
        JSONObject ownedGames = readJsonFromUrl(line);

        ArrayList<String> gameNames = new ArrayList<>();
        JSONObject response = (JSONObject)ownedGames.get("response");
        JSONArray games = (JSONArray)response.get("games");

        for ( int i = 0; i < games.size(); i++ ) {
            JSONObject iter = (JSONObject)games.get(i);
            long appid = (long)iter.get("appid");
            
            System.out.println("" + appid);
            
            Document documentIter = appids.find(new Document("appid", appid)).first();
            if ( documentIter != null ) {
                gameNames.add((String)documentIter.get("name"));
            }
        }
        return gameNames;
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
