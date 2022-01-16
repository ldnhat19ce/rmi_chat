package com.ldnhat.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexYT {

    private static final String youtubeRegex =
            "http(?:s)?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?(?:feature=youtu.be&)?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\\n]+)";
    public static final Pattern youtubePattern =
            Pattern.compile(youtubeRegex, Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        Matcher matcher = youtubePattern.matcher("https://stackoverflow.com/questions/24048308/how-to-get-the-video-id-from-a-youtube-url-with-regex-in-java");
        if (matcher.matches()){
            System.out.println("match");
        }else{
            System.out.println("not match");
        }
    }
}
