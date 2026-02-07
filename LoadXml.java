//
 
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//board.xml
//cards.xml
public class LoadXml   {
    public static void main(String argv[]) {
    
    
    }

    /* outside call to load board xml  */
    public  void LoadBoard(){
        try {
    File boardXmlFile = new File("/xml/board.xml");

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(boardXmlFile);
    doc.getDocumentElement().normalize();
    // each board instance is a set
        NodeList nList = doc.getElementsByTagName("set");
        for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                System.out.println(node.getTextContent());
            }
    
    
        } catch (Exception e) {

        }

    }
    /* Outside call to load card.xml */
    public   void LoadCard(){
        try {
    File cardXmlFile = new File("/xml/cards.xml");

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(cardXmlFile);
    doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("cards");
          for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                System.out.println(node.getTextContent());
            }
    
    
        } catch (Exception e) {
            
        }

    }


}