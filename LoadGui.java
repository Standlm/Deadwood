import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.awt.*;
import java.io.File;

public class LoadGui {
    private LoadGui() {
        
    }

    public static GuiAssets load(String[] playerColors) {
        GuiAssets assets = new GuiAssets();
        loadImages(assets, playerColors);
        loadRoleCoordinates(assets);
        return assets;
    }

    private static void loadImages(GuiAssets assets, String[] playerColors) {
        try {
            // Load shot counter image
            File shotFile = new File("gui/shot.png");
            if (shotFile.exists()) {
                assets.shotImage = ImageIO.read(shotFile);
            }
            
            // Load the card back image uploaded by the user
            File cardBackFile = new File("gui/Cardback.png");
            if (cardBackFile.exists()) {
                assets.cardBackImage = ImageIO.read(cardBackFile);
            }
            
            // Pre-load dice images
            for (String color : playerColors) {
                for (int rank = 1; rank <= 6; rank++) {
                    String key = color + rank;
                    File diceFile = new File("gui/Dice/" + key + ".png");
                    if (diceFile.exists()) {
                        assets.diceImages.put(key, ImageIO.read(diceFile));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private static void loadRoleCoordinates(GuiAssets assets) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document boardDoc = builder.parse(new File("xml/board.xml"));
            boardDoc.getDocumentElement().normalize();
            NodeList setNodes = boardDoc.getElementsByTagName("set");
            for (int i = 0; i < setNodes.getLength(); i++) {
                Element setElem = (Element) setNodes.item(i);
                String setName = setElem.getAttribute("name");
                NodeList partNodes = setElem.getElementsByTagName("part");
                for (int j = 0; j < partNodes.getLength(); j++) {
                    Element partElem = (Element) partNodes.item(j);
                    String roleName = partElem.getAttribute("name");
                    NodeList areaNodes = partElem.getElementsByTagName("area");
                    if (areaNodes.getLength() > 0) {
                        Element areaElem = (Element) areaNodes.item(0);
                        int x = Integer.parseInt(areaElem.getAttribute("x"));
                        int y = Integer.parseInt(areaElem.getAttribute("y"));
                        int w = Integer.parseInt(areaElem.getAttribute("w"));
                        int h = Integer.parseInt(areaElem.getAttribute("h"));
                        assets.offCardRoleCoords.put(setName + "|" + roleName, new Rectangle(x, y, w, h));
                    }
                }
            }

            Document cardsDoc = builder.parse(new File("xml/cards.xml"));
            cardsDoc.getDocumentElement().normalize();
            NodeList cardNodes = cardsDoc.getElementsByTagName("card");
            for (int i = 0; i < cardNodes.getLength(); i++) {
                Element cardElem = (Element) cardNodes.item(i);
                String sceneName = cardElem.getAttribute("name");
                NodeList partNodes = cardElem.getElementsByTagName("part");
                for (int j = 0; j < partNodes.getLength(); j++) {
                    Element partElem = (Element) partNodes.item(j);
                    String roleName = partElem.getAttribute("name");
                    NodeList areaNodes = partElem.getElementsByTagName("area");
                    if (areaNodes.getLength() > 0) {
                        Element areaElem = (Element) areaNodes.item(0);
                        int x = Integer.parseInt(areaElem.getAttribute("x"));
                        int y = Integer.parseInt(areaElem.getAttribute("y"));
                        int w = Integer.parseInt(areaElem.getAttribute("w"));
                        int h = Integer.parseInt(areaElem.getAttribute("h"));
                        assets.onCardRoleCoords.put(sceneName + "|" + roleName, new Rectangle(x, y, w, h));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading role coordinates: " + e.getMessage());
        }
    }
}