package com.echofilter.commons.utils.text;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizer {
    private TextNormalizer() {}

    public static String normalize(String s) {
        if (s == null) return "";
        s = s.replace("\r\n", "\n").replace('\r', '\n');
        s = s.replaceAll("[\\u200B\\u200C\\u200D\\uFEFF]", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);
        s = s.trim().replaceAll("\\s+", " ");
        s = s.toLowerCase(Locale.ROOT);
        return s;
    }
}