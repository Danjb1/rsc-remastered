package org.openrsc.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INI is a file extension for an initialization file format, as the name
 * implies. INI files are plain text (ASCII) and are used to set parameters for
 * the operating system and some programs.
 */
public class IniFile {

    private final Pattern _section = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    private final Pattern _keyValue = Pattern.compile("\\s*([^=]*)=(.*)");
    private Map<String, Map<String, String>> _entries = new HashMap<>();

    public IniFile(String path) {
        try {
            load(path);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error loading ini file: " + path, e);
        }
    }

    private void load(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            String section = null;
            while ((line = br.readLine()) != null) {
                Matcher m = _section.matcher(line);
                if (m.matches()) {
                    section = m.group(1).trim();
                } else if (section != null) {
                    m = _keyValue.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1).trim();
                        String value = m.group(2).trim();
                        Map<String, String> kv = _entries.get(section);
                        if (kv == null) {
                            _entries.put(section, kv = new HashMap<>());
                        }
                        kv.put(key, value);
                    }
                }
            }
        }
    }

    public boolean getBoolean(String section, String key, boolean defaultvalue) {
        Map<String, String> kv = _entries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return kv.get(key).toLowerCase().equals("true");
    }

    public String getString(String section, String key, String defaultvalue) {
        Map<String, String> kv = _entries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return kv.get(key);
    }

    public int getInt(String section, String key, int defaultvalue) {
        Map<String, String> kv = _entries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return Integer.parseInt(kv.get(key));
    }

    public float getFloat(String section, String key, float defaultvalue) {
        Map<String, String> kv = _entries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return Float.parseFloat(kv.get(key));
    }

    public double getDouble(String section, String key, double defaultvalue) {
        Map<String, String> kv = _entries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return Double.parseDouble(kv.get(key));
    }
    
}