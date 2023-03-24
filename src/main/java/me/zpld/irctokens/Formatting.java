package me.zpld.irctokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Formatting {
	private static String escapeTag(String tag) {
		for (int i = 0; i < Constants.TAG_UNESCAPED.length; i++)
			tag = tag.replaceAll(Constants.TAG_UNESCAPED[i], Constants.TAG_ESCAPED[i]);
		return tag;
	}
	
	public static String format(HashMap<String, String> tags, String source, String command, List<String> params) throws Exception {
		List<String> outs = new ArrayList<>();
		if (tags != null && tags.size() > 0) {
			List<String> sTags = new ArrayList<>();
			
			List<String> tagKeys = Arrays.asList((String[])tags.keySet().toArray());
			Collections.sort(tagKeys);
			for (String key : tagKeys) {
				String value = tags.get(key);
				if (value != null)
					sTags.add(String.format("%s=%s", key, escapeTag(value)));
				else
					sTags.add(key);
			}
			outs.add("@" + String.join(";", sTags));
		}
		
		if (source != null)
			outs.add(":"+source);
		outs.add(command);
		
		if (params.size() > 0) {
			String last = params.get(params.size()-1);
			for (String param : params.subList(0, params.size()-1)) {
				if (param.indexOf(" ") >= 0)
					throw new Exception("Non-last params cannot have spaces");
				else if (param.startsWith(":"))
					throw new Exception("Non-last params cannot start with colon");
			}
			outs.addAll(params.subList(0, params.size()-1));
			if (last == null)
				last = ":";
			else if (last.indexOf(" ") >= 0 || last.startsWith(":"))
				last = ":" + last;
			outs.add(last);
		}
		return String.join(" ", outs);
	}
}
