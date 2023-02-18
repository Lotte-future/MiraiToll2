package github.zimoyin.application.server.thesaurus;

import java.util.ArrayList;
import java.util.HashMap;

public class ThesaurusCenter implements Thesaurus{
    private volatile static ThesaurusCenter INSTANCE;
    private final HashMap<String, ArrayList<String>> cache = new HashMap<String, ArrayList<String>>();

    private ThesaurusCenter() {
    }

    public static ThesaurusCenter getInstance() {
        if (INSTANCE == null) synchronized (ThesaurusCenter.class) {
            if (INSTANCE == null) INSTANCE = new ThesaurusCenter();
        }
        return INSTANCE;
    }


    @Override
    public void addEntries(String key, String value) {

    }

    @Override
    public void removeEntries(String key, String value) {

    }

    @Override
    public void removeEntries(String key) {

    }

    @Override
    public String getEntries(String key) {
        return null;
    }

    @Override
    public void addCache(HashMap<String, ArrayList<String>> cache) {

    }

    @Override
    public void clearCache() {

    }

    @Override
    public int size() {
        return 0;
    }
}
