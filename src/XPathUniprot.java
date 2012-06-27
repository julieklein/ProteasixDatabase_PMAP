/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author julieklein
 */
public class XPathUniprot {
     public NodeList getNodeListByXPath (String xpathQuery, Document xmlDoc){
          NodeList r = null;
        try {
           
            r = (NodeList) XPathFactory.newInstance().newXPath().evaluate(xpathQuery, xmlDoc, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XPathUniprot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
         
     }
    
}