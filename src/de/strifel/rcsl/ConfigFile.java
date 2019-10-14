package de.strifel.rcsl;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class ConfigFile {

    private HashMap values;
    private File file;

    public ConfigFile(File file, HashMap standard) throws IOException, ParseException {
        this.file = file;
        if (!file.exists()) {
            values = (HashMap) standard.clone();
            save();
        }
        load();
    }

    private void load() throws IOException, ParseException {
        values = (HashMap) new JSONParser().parse(new String(Files.readAllBytes(file.toPath())));
    }

    public void save() throws IOException {
        String content = new JSONObject(values).toJSONString();
        file.delete();
        Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
    }

    public HashMap getValues() {
        return values;
    }
}
