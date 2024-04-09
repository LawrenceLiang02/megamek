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
    private static String BotFileName = "data/ranking/botRanking.xml";
    private  File xmlFile = new File(playerFileName);

    public static void updatePlayer( String playerName, int newElo, int newRanking) throws Exception {
        File xmlFile = new File(playerFileName);
        Document doc = loadXMLDocument(xmlFile);
        Element playerElement = getPlayerElementByName(doc, playerName);

        if (playerElement != null) {
            updatePlayerData(playerElement, newElo, newRanking);
            saveXMLDocument(doc, xmlFile);
            System.out.println("Player '" + playerName + "' updated successfully.");
        } else {
            System.out.println("Player '" + playerName + "' not found.");
        }
    }

    private static Document loadXMLDocument(File xmlFile) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(xmlFile);
    }

    private static Element getPlayerElementByName(Document doc, String playerName) {
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

    private static void saveXMLDocument(Document doc, File xmlFile) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }
}
