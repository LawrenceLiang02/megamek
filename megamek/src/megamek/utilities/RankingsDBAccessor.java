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

        Element playerElement = getPlayerElementByName( playerName);

        if (playerElement != null) {
            updatePlayerData(playerElement, newElo, newRanking);
        } else {
            // Add player if not existing
            playerElement = addNewPlayer(playerName, newElo, newRanking);
        }
        updateRankings();
        saveXMLDocument(doc, xmlFile);
        System.out.println("Player '" + playerName + "' updated successfully.");
    }

    public static void deletePlayer(String playerName) throws Exception {

        Element playerElement = getPlayerElementByName( playerName);

        if (playerElement != null) {
            playerElement.getParentNode().removeChild(playerElement);
            updateRankings();
            saveXMLDocument(doc, xmlFile);
            System.out.println("Player '" + playerName + "' deleted successfully.");
        } else {
            System.out.println("Player '" + playerName + "' not found.");
        }
    }

    private static Document loadXMLDocument(File xmlFile) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(xmlFile);
    }

    public static Element getPlayerElementByName( String playerName) {
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

    public static Element addNewPlayer(String playerName, int newElo, int newRanking) throws Exception {

        if (getPlayerElementByName(playerName) != null) {
            // Player already exists, return -1
            return null;
        }

        Element newPlayer = doc.createElement("player");

        // Create and append name element
        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(playerName));
        newPlayer.appendChild(doc.createTextNode("\n\t\t"));
        newPlayer.appendChild(nameElement);

        // Append newline for each new element
        newPlayer.appendChild(doc.createTextNode("\n\t\t"));

        // Create and append elo element
        Element eloElement = doc.createElement("elo");
        eloElement.appendChild(doc.createTextNode(String.valueOf(newElo)));
        newPlayer.appendChild(eloElement);

        // Append newline for each new element
        newPlayer.appendChild(doc.createTextNode("\n\t\t"));

        // Create and append ranking element
        Element rankingElement = doc.createElement("ranking");
        rankingElement.appendChild(doc.createTextNode(String.valueOf(newRanking)));
        newPlayer.appendChild(rankingElement);

        // Append newline before closing player element
        newPlayer.appendChild(doc.createTextNode("\n\t"));
        doc.getDocumentElement().appendChild(doc.createTextNode("\t"));

        // Append the new player to the document element
        doc.getDocumentElement().appendChild(newPlayer);

        // Update rankings based on the newly added player
        doc.getDocumentElement().appendChild(doc.createTextNode("\n"));
        updateRankings();

        // Save the document to apply changes
        saveXMLDocument(doc, xmlFile);

        return newPlayer;
    }


    private static void updateRankings() throws Exception {
        NodeList playerList = doc.getElementsByTagName("player");

        // Create arrays to store player names and their Elo scores
        String[] playerNames = new String[playerList.getLength()];
        int[] elos = new int[playerList.getLength()];

        // Populate the arrays with player data
        for (int i = 0; i < playerList.getLength(); i++) {
            Element player = (Element) playerList.item(i);
            playerNames[i] = player.getElementsByTagName("name").item(0).getTextContent();
            elos[i] = Integer.parseInt(player.getElementsByTagName("elo").item(0).getTextContent());
        }

        // Sort the players based on Elo scores in descending order
        for (int i = 0; i < elos.length - 1; i++) {
            for (int j = 0; j < elos.length - i - 1; j++) {
                if (elos[j] < elos[j + 1]) {
                    // Swap Elo scores
                    int tempElo = elos[j];
                    elos[j] = elos[j + 1];
                    elos[j + 1] = tempElo;

                    // Swap player names accordingly
                    String tempName = playerNames[j];
                    playerNames[j] = playerNames[j + 1];
                    playerNames[j + 1] = tempName;
                }
            }
        }

        // Update the rankings based on the sorted Elo scores
        for (int i = 0; i < playerList.getLength(); i++) {
            Element player = getPlayerElementByName(playerNames[i]);
            player.getElementsByTagName("ranking").item(0).setTextContent(String.valueOf(i + 1));
        }
    }

    private static void swapPlayers(Element player1, Element player2) {
        Element parent = (Element) player1.getParentNode();
        parent.insertBefore(player2, player1);
    }

    private static void saveXMLDocument(Document doc, File xmlFile) throws Exception {
        updateRankings();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }
}
