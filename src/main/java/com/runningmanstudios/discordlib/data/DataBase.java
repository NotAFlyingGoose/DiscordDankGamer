package com.runningmanstudios.discordlib.data;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DataBase {
    protected JSONObject content;
    private final File path;

    public DataBase(String relativePath) {
        this.path = new File(System.getProperty("user.home") + relativePath);
        this.content = getSections();
    }

    public void addSection(String id, Data... information) {
        JSONObject userDetails = new JSONObject();
        for (Data info : information) {
            userDetails.put(info.getName(), info.getValue());
        }
        content.put(id, userDetails);
    }

    public JSONObject getSections()
    {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(path))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            return (JSONObject) obj;
        } catch (ParseException | IOException e) {
            return new JSONObject();
        }
    }

    public JSONObject getSection(String id)
    {
        return (JSONObject) content.get(id);
    }

    public void writeContent() {
        try (FileWriter file = new FileWriter(path)) {

            file.write(content.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearContent() {
        try (FileWriter file = new FileWriter(path)) {

            file.write("");
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsSection(String id) {
        return content.containsKey(id);
    }

    public static JSONObject getFromUrl(String url) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
            return (JSONObject) obj;
        } catch (ParseException | IOException e) {
            System.err.println("There was an error while trying to get data from that url: " + e.getMessage());
        }
        return null;
    }
}
