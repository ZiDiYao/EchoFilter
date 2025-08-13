package com.echofilter.commons.templates;

import com.echofilter.modules.utils.PromptLoader;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplates {

    private final String commentAnalysisTemplate =
            PromptLoader.loadTemplate("prompts/PromptV01.xml");

    /**
     * context 可以为 null
     */
    public String buildCommentAnalysis(String platform, String language, String comment, String context) {
        System.out.println("buildCommentAnalysis has been called" );
        System.out.println("platform"+platform);
        System.out.println("language:"+language);
        System.out.println("comment:"+ comment);
        System.out.println("context:"+ context);
        return String.format(
                commentAnalysisTemplate,
                platform,
                comment,
                context
        );
    }



    private static String nn(String s) {
        return s == null ? "" : s;
    }
}
