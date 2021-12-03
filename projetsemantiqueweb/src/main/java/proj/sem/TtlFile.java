package proj.sem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class TtlFile {

    List<Model> listModel = new ArrayList<Model>();
    List<String> listModelAdress = new ArrayList<String>();
    
    public TtlFile() throws IOException{
       
        retrievAdress("https://territoire.emse.fr/kg/");
        
        for (String s : listModelAdress) {
            System.out.printf(s+"\n");
        }
        //(".*\\.req\\.copied")
        listModelAdress.add("https://territoire.emse.fr/kg/ontology.ttl");
        listModelAdress.add("https://territoire.emse.fr/kg/emse/index.ttl");

       /* for (String s : listModelAdress) {
            listModel.add(ModelFactory.createDefaultModel().read(s));
        }

        for(Model m: listModel){
            m.write(System.out,"Turtle");
        }*/
    }

    public void retrievAdress(String adress) throws IOException{
        Document doc = Jsoup.connect(adress).get();
        System.out.printf(doc.title()+"\n");
        Elements newsHeadlines = doc.select("a");
        
        
        for (Element headline : newsHeadlines) {
            if(headline.absUrl("href").matches(adress +".*\\.ttl")){
                listModelAdress.add(headline.absUrl("href"));
            }
            if(headline.absUrl("href").matches(adress +".*\\/")){
                System.out.printf("%s \n", headline.absUrl("href"));
                retrievAdress(headline.absUrl("href"));
                }
        }

    }

  
}
