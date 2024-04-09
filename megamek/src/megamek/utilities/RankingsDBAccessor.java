package megamek.utilities;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RankingsDBAccessor {

    private static String playerFileName = "data/ranking/playerRanking.xml";
    private static File xmlFile = new File(playerFileName);
    private static Document doc;

    static {
        try {
            doc = loadXMLDocument(xmlFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updatePlayer(String playerName, int newElo, int newRanking) throws Exception {
        File xmlFile = new File(playerFileName);
        Document doc = loadXMLDocument(xmlFile);
        Element playerElement = getPlayerFromDb(playerName);

        if (playerElement != null) {
            updatePlayerData(playerElement, newElo, newRanking);
        } else {
            // Add player if not existing
            playerElement = addNewPlayer( playerName, newElo, newRanking);
        }

        saveXMLDocument(doc, xmlFile);
        System.out.println("Player '" + playerName + "' updated successfully.");
    }

    private static Document loadXMLDocument(File xmlFile) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(xmlFile);
    }

    public static Element getPlayerFromDb( String playerName) {
        NodeList playerList = doc.getElementsByTagName("player");
        for (int i = 0; i < playerList.getLength(); i++) {
            Element player = (Element) playerList.item(i);
            String name = player.getElementsByTagName("name").item(0).getTextContent();
            if (name.equals(playerName)) {
                return player;
            }
        }
        return null;
    }

    private static void updatePlayerData(Element playerElement, int newElo, int newRanking) {
        playerElement.getElementsByTagName("elo").item(0).setTextContent(String.valueOf(newElo));
        playerElement.getElementsByTagName("ranking").item(0).setTextContent(String.valueOf(newRanking));
    }

    public static Element addNewPlayer(String playerName, int newElo, int newRanking) {
        Element newPlayer = doc.createElement("player");
        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(playerName));
        newPlayer.appendChild(nameElement);
        Element eloElement = doc.createElement("elo");
        eloElement.appendChild(doc.createTextNode(String.valueOf(newElo)));
        newPlayer.appendChild(eloElement);
        Element rankingElement = doc.createElement("ranking");
        rankingElement.appendChild(doc.createTextNode(String.valueOf(newRanking)));
        newPlayer.appendChild(rankingElement);
        doc.getDocumentElement().appendChild(newPlayer);
        return newPlayer;
    }

    public static void deletePlayer(String playerName) throws Exception {
        File xmlFile = new File(playerFileName);
        Document doc = loadXMLDocument(xmlFile);
        Element playerElement = getPlayerFromDb(playerName);

        if (playerElement != null) {
            playerElement.getParentNode().removeChild(playerElement);
            saveXMLDocument(doc, xmlFile);
            System.out.println("Player '" + playerName + "' deleted successfully.");
        } else {
            System.out.println("Player '" + playerName + "' not found.");
        }
    }


    private static void saveXMLDocument(Document doc, File xmlFile) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }
}
