package com.lendingkart.Utility;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.lendingkart.Exception.XmlToJsonConversionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlToJsonConverter {
    private static final Logger LOGGER = Logger.getLogger(XmlToJsonConverter.class.getName());

    public static String convertXmlToJson(String xmlString) throws XmlToJsonConversionException {
        try {
            // Parse XML to Document
            Document document = parseXmlString(xmlString);

            // Create the base JSON structure
            JSONObject rootJson = new JSONObject();
            JSONObject resultBlockJson = new JSONObject();
            rootJson.put("ResultBlock", resultBlockJson);

            // Add MatchSummary with TotalMatchScore first (new custom field)
            JSONObject matchSummary = new JSONObject();
            long totalScore = calculateTotalScore(document);
            matchSummary.put("TotalMatchScore", String.valueOf(totalScore));
            resultBlockJson.put("MatchSummary", matchSummary);

            // Add API section
            processAPI(document, resultBlockJson);

            // Add MatchDetails section with proper array structure
            processMatchDetails(document, resultBlockJson);

            // Add ErrorWarnings section
            processErrorWarnings(document, resultBlockJson);

            return rootJson.toString(2);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to convert XML to JSON", e);
            throw new XmlToJsonConversionException("Error converting XML to JSON: " + e.getMessage(), e);
        }
    }
    private static Document parseXmlString(String xmlString) throws XmlToJsonConversionException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to parse XML string", e);
            throw new XmlToJsonConversionException("Error parsing XML: " + e.getMessage(), e);
        }
    }

    private static long calculateTotalScore(Document document) throws XmlToJsonConversionException {
        try {
            NodeList scoreNodes = document.getElementsByTagName("Score");
            long totalScore = 0;

            for (int i = 0; i < scoreNodes.getLength(); i++) {
                Node scoreNode = scoreNodes.item(i);
                // Verify this Score node is under MatchDetails > Match path
                if (isScoreNodeUnderMatchPath(scoreNode)) {
                    String scoreValue = scoreNode.getTextContent().trim();
                    if (!scoreValue.isEmpty()) {
                        try {
                            long score = Long.parseLong(scoreValue);
                            totalScore += score;
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid score value: " + scoreValue, e);
                        }
                    }
                }
            }

            return totalScore;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total score", e);
            throw new XmlToJsonConversionException("Error calculating total score: " + e.getMessage(), e);
        }
    }

    private static boolean isScoreNodeUnderMatchPath(Node scoreNode) {
        try {
            Node parent = scoreNode.getParentNode();
            if (parent != null && "Match".equals(parent.getNodeName())) {
                Node grandparent = parent.getParentNode();
                return grandparent != null && "MatchDetails".equals(grandparent.getNodeName());
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error checking score node path", e);
            return false;
        }
    }

    private static void processAPI(Document document, JSONObject resultBlockJson) {
        try {
            NodeList apiNodes = document.getElementsByTagName("API");
            if (apiNodes.getLength() > 0) {
                Element apiElement = (Element) apiNodes.item(0);
                JSONObject apiJson = new JSONObject();

                // Process RetStatus
                NodeList retStatusNodes = apiElement.getElementsByTagName("RetStatus");
                if (retStatusNodes.getLength() > 0) {
                    apiJson.put("RetStatus", retStatusNodes.item(0).getTextContent());
                }

                // Process empty elements as null
                String[] emptyElements = {"SysErrorCode", "SysErrorMessage", "ErrorMessage"};
                for (String elementName : emptyElements) {
                    NodeList nodes = apiElement.getElementsByTagName(elementName);
                    if (nodes.getLength() > 0) {
                        String content = nodes.item(0).getTextContent().trim();
                        apiJson.put(elementName, content.isEmpty() ? JSONObject.NULL : content);
                    } else {
                        apiJson.put(elementName, JSONObject.NULL);
                    }
                }

                resultBlockJson.put("API", apiJson);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing API section", e);
        }
    }

    private static void processMatchDetails(Document document, JSONObject resultBlockJson) {
        try {
            NodeList matchDetailsNodes = document.getElementsByTagName("MatchDetails");
            if (matchDetailsNodes.getLength() > 0) {
                JSONArray matchDetailsArray = new JSONArray();
                Element matchDetailsElement = (Element) matchDetailsNodes.item(0);

                // Process Match nodes
                NodeList matchNodes = matchDetailsElement.getElementsByTagName("Match");

                for (int i = 0; i < matchNodes.getLength(); i++) {
                    Element matchElement = (Element) matchNodes.item(i);
                    JSONObject matchJson = new JSONObject();

                    // Process Entity
                    NodeList entityNodes = matchElement.getElementsByTagName("Entity");
                    if (entityNodes.getLength() > 0) {
                        matchJson.put("Entity", entityNodes.item(0).getTextContent());
                    }

                    // Process Score
                    NodeList scoreNodes = matchElement.getElementsByTagName("Score");
                    if (scoreNodes.getLength() > 0) {
                        matchJson.put("Score", scoreNodes.item(0).getTextContent());
                    }

                    // Process MatchType
                    NodeList matchTypeNodes = matchElement.getElementsByTagName("MatchType");
                    if (matchTypeNodes.getLength() > 0) {
                        matchJson.put("MatchType", matchTypeNodes.item(0).getTextContent());
                    }

                    matchDetailsArray.put(matchJson);
                }

                resultBlockJson.put("MatchDetails", matchDetailsArray);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing MatchDetails section", e);
        }
    }

    private static void processErrorWarnings(Document document, JSONObject resultBlockJson) {
        try {
            NodeList errorWarningsNodes = document.getElementsByTagName("ErrorWarnings");
            if (errorWarningsNodes.getLength() > 0) {
                JSONObject errorWarningsJson = new JSONObject();
                Element errorWarningsElement = (Element) errorWarningsNodes.item(0);

                // Process Errors
                NodeList errorsNodes = errorWarningsElement.getElementsByTagName("Errors");
                if (errorsNodes.getLength() > 0) {
                    Element errorsElement = (Element) errorsNodes.item(0);
                    String errorCount = errorsElement.getAttribute("errorCount");

                    // Always create an Errors object with errorCount property
                    JSONObject errorsJson = new JSONObject();
                    errorsJson.put("errorCount", errorCount);

                    // Process Error objects if needed (when errorCount > 0)
                    if (!"0".equals(errorCount)) {
                        // Add code here to process error elements if needed
                    }

                    errorWarningsJson.put("Errors", errorsJson);
                }

                // Process Warnings
                NodeList warningsNodes = errorWarningsElement.getElementsByTagName("Warnings");
                if (warningsNodes.getLength() > 0) {
                    Element warningsElement = (Element) warningsNodes.item(0);
                    String warningCount = warningsElement.getAttribute("warningCount");

                    JSONObject warningsJson = new JSONObject();
                    warningsJson.put("warningCount", warningCount);

                    // Process Warning objects
                    NodeList warningNodes = warningsElement.getElementsByTagName("Warning");
                    if (warningNodes.getLength() > 0) {
                        if (warningNodes.getLength() == 1) {
                            // Single warning
                            Element warningElement = (Element) warningNodes.item(0);
                            JSONObject warningJson = processWarningElement(warningElement);
                            warningsJson.put("Warning", warningJson);
                        } else {
                            // Multiple warnings
                            JSONArray warningArray = new JSONArray();
                            for (int i = 0; i < warningNodes.getLength(); i++) {
                                Element warningElement = (Element) warningNodes.item(i);
                                JSONObject warningJson = processWarningElement(warningElement);
                                warningArray.put(warningJson);
                            }
                            warningsJson.put("Warning", warningArray);
                        }
                    }

                    errorWarningsJson.put("Warnings", warningsJson);
                }

                resultBlockJson.put("ErrorWarnings", errorWarningsJson);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing ErrorWarnings section", e);
        }
    }

    private static JSONObject processWarningElement(Element warningElement) {
        JSONObject warningJson = new JSONObject();

        try {
            // Process Number
            NodeList numberNodes = warningElement.getElementsByTagName("Number");
            if (numberNodes.getLength() > 0) {
                warningJson.put("Number", numberNodes.item(0).getTextContent());
            }

            // Process Message
            NodeList messageNodes = warningElement.getElementsByTagName("Message");
            if (messageNodes.getLength() > 0) {
                warningJson.put("Message", messageNodes.item(0).getTextContent());
            }

            // Process Values
            NodeList valuesNodes = warningElement.getElementsByTagName("Values");
            if (valuesNodes.getLength() > 0) {
                Element valuesElement = (Element) valuesNodes.item(0);
                JSONObject valuesJson = new JSONObject();

                // Process Value elements
                NodeList valueNodes = valuesElement.getElementsByTagName("Value");
                if (valueNodes.getLength() > 1) {
                    // Multiple values as array
                    JSONArray valueArray = new JSONArray();
                    for (int i = 0; i < valueNodes.getLength(); i++) {
                        valueArray.put(valueNodes.item(i).getTextContent());
                    }
                    valuesJson.put("Value", valueArray);
                } else if (valueNodes.getLength() == 1) {
                    // Single value
                    valuesJson.put("Value", valueNodes.item(0).getTextContent());
                }

                warningJson.put("Values", valuesJson);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing Warning element", e);
        }

        return warningJson;
    }


}