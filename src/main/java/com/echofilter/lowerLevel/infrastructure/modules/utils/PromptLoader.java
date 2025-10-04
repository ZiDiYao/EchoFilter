package com.echofilter.lowerLevel.infrastructure.modules.utils;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;

public class PromptLoader {
    public static String loadTemplate(String path) {
        try (var in = new ClassPathResource(path).getInputStream()) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            Node node = doc.getElementsByTagName("template").item(0);
            return node.getTextContent().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load template: " + path, e);
        }
    }
}