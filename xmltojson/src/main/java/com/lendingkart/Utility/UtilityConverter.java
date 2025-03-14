package com.lendingkart.Utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lendingkart.Exception.XmlToJsonConversionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;




public class UtilityConverter {

    private static final Logger logger = Logger.getLogger(UtilityConverter.class.getName());
    public static String convertXmlToJsonString(String xml) throws XmlToJsonConversionException {
        XmlMapper xmlMapper = new XmlMapper();
        ObjectMapper jsonMapper = new ObjectMapper();

        try {
            // Parse XML into a JsonNode
            JsonNode xmlNode = xmlMapper.readTree(xml);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            // Calculate the total match score
            int totalMatchScore = calculateTotalMatchScore(document);

            // Add the total match score to the JSON object
            addTotalMatchScoreToJson(xmlNode, totalMatchScore);

            // Convert the modified map to JSON string
            return jsonMapper.writeValueAsString(xmlNode);
        } catch (Exception e) {
            logger.severe("Error converting XML to JSON: " + e.getMessage());
            throw new XmlToJsonConversionException("Error converting XML to JSON", e);
        }
    }


    private static int calculateTotalMatchScore(Document document) {
        int totalScore = 0;

        // Get all <Score> elements under <MatchDetails><Match>
        NodeList scoreNodes = document.getElementsByTagName("Score");

        // Iterate through the <Score> nodes
        for (int i = 0; i < scoreNodes.getLength(); i++) {
            Node scoreNode = scoreNodes.item(i);

            // Ensure the node is an element
            if (scoreNode.getNodeType() == Node.ELEMENT_NODE) {
                Element scoreElement = (Element) scoreNode;

                String scoreText = scoreElement.getTextContent();
                try {
                    int scoreValue = Integer.parseInt(scoreText);
                    totalScore += scoreValue;
                } catch (NumberFormatException e) {
                    logger.warning("Invalid score value: " + scoreText);
                }
            }
        }

        return totalScore;
    }

    private static void addTotalMatchScoreToJson(JsonNode jsonNode, int totalMatchScore) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;

            // Add custom field MatchSummary.TotalMatchScore
            ObjectNode matchSummaryNode = objectNode.with("MatchSummary");
            matchSummaryNode.put("TotalMatchScore", totalMatchScore);
        }
    }

}