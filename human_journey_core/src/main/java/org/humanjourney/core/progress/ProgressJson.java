package org.humanjourney.core.progress;
import com.google.gson.*;
/** Bound nesting before invoking a JSON parser; text in quoted strings does not count. */
public final class ProgressJson {
 public static void bounded(String text,int max){if(text.length()>max)throw new IllegalArgumentException("progress JSON exceeds character budget");int depth=0;boolean quoted=false,escaped=false;for(int i=0;i<text.length();i++){char c=text.charAt(i);if(quoted){if(escaped)escaped=false;else if(c=='\\')escaped=true;else if(c=='"')quoted=false;}else if(c=='"')quoted=true;else if(c=='['||c=='{'){if(++depth>32)throw new IllegalArgumentException("progress JSON nesting exceeds32");}else if(c==']'||c=='}')depth--;}}
 public static JsonElement read(String text){bounded(text,ProgressGraph.MAX_JSON);return JsonParser.parseString(text);}
 private ProgressJson(){}
}
