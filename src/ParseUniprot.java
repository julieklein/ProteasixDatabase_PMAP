import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author julieklein
 */
public class ParseUniprot {

    public Document getXML(String url) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            doc = parser.parse(url);
            



        } catch (SAXException ex) {
            Logger.getLogger(ParseUniprot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParseUniprot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ParseUniprot.class.getName()).log(Level.SEVERE, null, ex);
        }

        return doc;


    }
    
    public String getXMLasstring (String url) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        String r = null;
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            doc = parser.parse(url);
            StringWriter sw = new StringWriter();
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(sw));
            r = sw.toString();



        } catch (TransformerException ex) {
            Logger.getLogger(ParseUniprot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ParseUniprot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParseUniprot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ParseUniprot.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;


    }
}
