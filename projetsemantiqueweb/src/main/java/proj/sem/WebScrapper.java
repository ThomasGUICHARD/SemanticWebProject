package proj.sem;



import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScrapper {
    public WebScrapper() throws CsvValidationException, IOException{
        Document doc = Jsoup.connect("https://www.meteociel.fr/temps-reel/obs_villes.php?code2=7475&jour2=16&mois2=10&annee2=2021").get();  
        ArrayList<String> temperature = new ArrayList<>();
        ArrayList<String> heureLocale = new ArrayList<>();
        Element table = doc.select("table").get(7); //select the first table.
        Elements rows = table.select("tr");

        for (int i = 1; i < rows.size(); i++) { //start at 1 to skip 
            Element row = rows.get(i);
            Elements cols = row.select("td");
            temperature.add(cols.get(0).text());
            temperature.add(cols.get(4).text());
            
            //System.out.println(cols.get(0).text()+" : "+ cols.get(4).text());
            
            //System.out.println(temperature);
        }
        
        Model model = ModelFactory.createDefaultModel();
       //model.setNsPrefix("rdfs", RDFS.uri);
        model.setNsPrefix("sosa", "http://www.w3.org/ns/sosa/");
        model.setNsPrefix("time", "http://www.w3.org/2006/time#");
        model.setNsPrefix("location", "http://www.w3.org/2001/XMLSchema#");

        String str = temperature.toString();
        //stops200.txt
        //J'ai des problème d'encodage de caractères au niveau du CSVReader mais sinon tout marche
        System.out.println(str);
        CSVReader reader = new CSVReader(new FileReader(""));

        String[] lineInArray;
        lineInArray = reader.readNext();
        while ((lineInArray = reader.readNext()) != null) {
            
            Resource root=model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+"observation");
            root.addProperty(model.createProperty("http://www.w3.org/2006/time#"), lineInArray[1]);
            //root.addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#"+"lat"), lineInArray[3] ,XSDGenericType.XSDdecimal);
            root.addProperty(model.createProperty("https://www.w3.org/TR/vocab-ssn/"), lineInArray[10]);
        
            model.add(root,RDF.type,model.createResource("http://www.w3.org/2003/01/geo/wgs84_pos#"+"SpatialThing"));
        }


    }
}
