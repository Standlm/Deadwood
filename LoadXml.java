import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LoadXml {
   public List<BoardSpace> loadBoardSpaces() {
      ArrayList<BoardSpace> spaces = new ArrayList<>();
      try {
         File file = new File("xml/board.xml");
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document doc = builder.parse(file);
         doc.getDocumentElement().normalize();

         // Load all <set> nodes
         NodeList setNodes = doc.getElementsByTagName("set");
         for (int i = 0; i < setNodes.getLength(); ++i) {
            Element setElem = (Element) setNodes.item(i);
            String name = setElem.getAttribute("name");
            NodeList neighborNodes = setElem.getElementsByTagName("neighbor");
            String[] neighbors = new String[neighborNodes.getLength()];
            for (int j = 0; j < neighborNodes.getLength(); ++j) {
               neighbors[j] = ((Element) neighborNodes.item(j)).getAttribute("name");
            }
            NodeList partNodes = setElem.getElementsByTagName("part");
            Role[] extras = new Role[partNodes.getLength()];
            for (int k = 0; k < partNodes.getLength(); ++k) {
               Element partElem = (Element) partNodes.item(k);
               String roleName = partElem.getAttribute("name");
               int roleRank = Integer.parseInt(partElem.getAttribute("level"));
               extras[k] = new Role(roleName, roleRank, "Extra");
            }
            BoardSpace space = new BoardSpace(name, "set", neighbors, extras);
            spaces.add(space);
         }

         // Load <trailer>
         NodeList trailerNodes = doc.getElementsByTagName("trailer");
         if (trailerNodes.getLength() > 0) {
            Element trailerElem = (Element) trailerNodes.item(0);
            NodeList neighborNodes = trailerElem.getElementsByTagName("neighbor");
            String[] neighbors = new String[neighborNodes.getLength()];
            for (int j = 0; j < neighborNodes.getLength(); ++j) {
               neighbors[j] = ((Element) neighborNodes.item(j)).getAttribute("name");
            }
            BoardSpace trailer = new BoardSpace("trailer", "trailer", neighbors, new Role[0]);
            spaces.add(trailer);
         }

         // Load <office> (Casting Office)
         NodeList officeNodes = doc.getElementsByTagName("office");
         if (officeNodes.getLength() > 0) {
            Element officeElem = (Element) officeNodes.item(0);
            NodeList neighborNodes = officeElem.getElementsByTagName("neighbor");
            String[] neighbors = new String[neighborNodes.getLength()];
            for (int j = 0; j < neighborNodes.getLength(); ++j) {
               neighbors[j] = ((Element) neighborNodes.item(j)).getAttribute("name");
            }
            BoardSpace office = new BoardSpace("Casting Office", "office", neighbors, new Role[0]);
            spaces.add(office);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return spaces;
   }

   public List<Scene> loadScenes() {
      ArrayList var1 = new ArrayList();

      try {
         File var2 = new File("xml/cards.xml");
         DocumentBuilderFactory var3 = DocumentBuilderFactory.newInstance();
         DocumentBuilder var4 = var3.newDocumentBuilder();
         Document var5 = var4.parse(var2);
         var5.getDocumentElement().normalize();
         //everything is sorted by card, so we get all the cards and then sort through the parts and scenes to make the Scene objects
         NodeList var6 = var5.getElementsByTagName("card");

         for(int var7 = 0; var7 < var6.getLength(); ++var7) {
            Element var8 = (Element)var6.item(var7);
            String var9 = var8.getAttribute("name");
            int var10 = Integer.parseInt(var8.getAttribute("budget"));
            NodeList var11 = var8.getElementsByTagName("scene");
            //location Id
            String var12 = "";
            if (var11.getLength() > 0) {
               Element var13 = (Element)var11.item(0);
               var12 = var13.getAttribute("number");
            }

            NodeList var20 = var8.getElementsByTagName("part");
            Role[] role = new Role[var20.getLength()];

            for(int var15 = 0; var15 < var20.getLength(); ++var15) {
               Element var16 = (Element)var20.item(var15);
               String sceneN = var16.getAttribute("name");
               int level = Integer.parseInt(var16.getAttribute("level"));
               role[var15] = new Role(sceneN, level, "Featured");
            }

            Scene var21 = new Scene(var12, var9, var10, role, var10);
            var1.add(var21);
         }
      } catch (Exception var19) {
         var19.printStackTrace();
      }

      return var1;
   }

   public void loadGameData(GameBoard var1) {
      List var2 = this.loadBoardSpaces();
      List var3 = this.loadScenes();
      var1.boardSpaces.clear();
      var1.boardSpaces.addAll(var2);
      var1.sceneDeck.clear();
      var1.sceneDeck.addAll(var3);
   }
}




