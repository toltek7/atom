package com.atom.json;


import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by toltek7 on 27.12.2014.
 */

public class JsonProcessor {

    private final File dir;
    
    private static JsonProcessor instance;

    public static JsonProcessor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("JsonProcessor is not initialized");
        }
        return instance;
    }

    public static synchronized JsonProcessor createInstance(File dir) {
        instance = new JsonProcessor(dir);
        return instance;
    }

    private JsonProcessor(File dir) {
        if (dir == null) {
            throw new IllegalStateException("TranslatedData manager can't find data folder");
        }        
        this.dir = dir;
    }

    public Map getData(String lang) {

        File[] files = getJsonFiles(lang);

        return jsonToHashMap(parseJsonFiles(files));
    }

    public File[] getJsonFiles(String lang) {

        File langDir = new File(dir.getAbsolutePath() + "/" + lang);

        File[] dirFiles = dir.listFiles((FileFilter) new WildcardFileFilter("*.json"));
        File[] langFiles = langDir.listFiles((FileFilter) new WildcardFileFilter("*.json"));

        return ArrayUtils.addAll(dirFiles, langFiles);
    }

    protected List<JSONObject> parseJsonFiles(File[] files) {

        JSONParser parser = new JSONParser();

        List<JSONObject> result = new ArrayList<JSONObject>(files.length);

        for (File file : files) {
            try {
                result.add((JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8")));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    protected Map jsonToHashMap(List<JSONObject> objects) {

        Map map = new HashMap();

        for (JSONObject object : objects) map.putAll(object);

        return map;

    }
}
