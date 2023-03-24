package me.zpld.irctokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Line {
    private HashMap<String, String> tags;
    private String source;
    private String command;
    private List<String> params;

    public Line(HashMap<String, String> tags, String source, String command, List<String> params) {
        this.tags = tags;
        this.source = source;
        this.command = command;
        this.params = params;
    }
    
    public Line(String command, String[] params) {
    	this(null, null, command, Arrays.asList(params));
    }
    
    @Override
    public String toString() {
    	return String.format(
    			"Line(tags=%s, source=%s, command=%s, params=%s)",
    			tags, source, command, params
    	);
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public String getSource() {
        return source;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getParams() {
        return params;
    }

    public String getFormatted() {
        try {
			return Formatting.format(tags, source, command, params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    private static String unescapeTag(String tag) {
        String unescaped = "";
        char[] escaped = tag.toCharArray();
        int i = 0;
        while (i < escaped.length) {
            char current = escaped[i];
            if (current == '\\') {
                if (i < escaped.length - 1) {
                    char next = escaped[i + 1];
                    String duo = "" + current + next;
                    if (Arrays.asList(Constants.TAG_ESCAPED).contains(duo)) {
                        int index = Arrays.asList(Constants.TAG_ESCAPED).indexOf(duo);
                        unescaped += Constants.TAG_UNESCAPED[index];
                        i += 2;
                    } else {
                        unescaped += next;
                        i += 2;
                    }
                } else {
                    i++;
                }
            } else {
                unescaped += current;
                i++;
            }
        }
        return unescaped;
    }

    public static Line tokenise(String line) throws Exception {
        HashMap<String, String> tags = new HashMap<>();
        if (line.charAt(0) == '@') {
            String[] tagParts = line.split(" ", 2);
            String tags_s = tagParts[0];
            line = tagParts.length > 1 ? tagParts[1] : "";

            for (String part : tags_s.substring(1).split(";")) {
                String[] kv = part.split("=", 2);
                String key = kv[0];
                String value = unescapeTag(kv.length > 1 ? kv[1] : "");
                tags.put(key, value);
            }
        }

        String[] lineParts = line.split(" :", 2);
        line = lineParts[0];
        String trailing = lineParts.length > 1 ? lineParts[1] : "";
        List<String> params = new ArrayList<>(Arrays.asList(line.split(" ")));

        String source = null;
        if (params.size() > 0 && params.get(0).charAt(0) == ':') {
            source = params.remove(0).substring(1);
        }

        if (params.size() < 1)
            throw new Exception("Cannot tokenise command-less line");
        String command = params.remove(0);

        if (trailing.length() > 0)
            params.add(trailing);

        return new Line(tags, source, command, params);
    }

}
