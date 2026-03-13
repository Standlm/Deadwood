import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SceneCardCatalog {
    private final Map<String, String> sceneToCardNumber = new HashMap<>();

    public SceneCardCatalog(String cardsXmlPath) {
        load(cardsXmlPath);
    }

    private void load(String cardsXmlPath) {
        try {
            File file = new File(cardsXmlPath);
            if (!file.exists()) return;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList cardNodes = document.getElementsByTagName("card");
            for (int i = 0; i < cardNodes.getLength(); i++) {
                Element cardElement = (Element) cardNodes.item(i);
                String sceneName = cardElement.getAttribute("name");
                String img = cardElement.getAttribute("img");
                if (sceneName == null || sceneName.isEmpty() || img == null || img.isEmpty()) continue;

                String cardNumber = img.replace(".png", "").trim();
                sceneToCardNumber.put(sceneName, cardNumber);
            }
        } catch (Exception e) {
            // Keep empty mapping if XML cannot be read
        }
    }

    public String getCardNumberForScene(String sceneName) {
        if (sceneName == null || sceneName.isEmpty()) return "01";
        return sceneToCardNumber.getOrDefault(sceneName, "01");
    }
}
