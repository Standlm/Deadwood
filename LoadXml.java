//
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
//board.xml
//cards.xml
public class LoadXml {
    public static void main(String argv[]) {
    
    
    }

    /* outside call to load board xml  */
    public void LoadBoard{
        try {
    File boardXmlFile = new File("/xml/board.xml");

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(boardXmlFile);
    doc.getDocumentElement().normalize();
    // each board instance is a set
        NodeList nList = doc.getElementsByTagName("set");
    
    
        }

    }
    /* Outside call to load card.xml */
    public void LoadCard{
        try {
    File cardXmlFile = new File("/xml/cards.xml");

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(cardXmlFile);
    doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("cards");
    
    
        }

    }


}