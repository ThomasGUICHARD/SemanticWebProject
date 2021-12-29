package proj.sem;



import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.crypto.Data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScrapper {
    public WebScrapper() throws CsvValidationException, IOException{
        int nbJour = 4;
        int nbMois = 11;
        int nbAnnee = 2021; 
        Document doc = Jsoup.connect("https://www.meteociel.fr/temps-reel/obs_villes.php?code2=7475&jour2="+nbJour+"&mois2="+nbMois+"&annee2="+nbAnnee).get();  
        ArrayList<String> temperature = new ArrayList<>();
        ArrayList<String> heureLocale = new ArrayList<>();
        Element table = doc.select("table").get(7); //select the 7th table.
        Elements rows = table.select("tr");

        for (int i = 1; i < rows.size(); i++) { //start at 1 to skip 
            Element row = rows.get(i);
            Elements cols = row.select("td");
            temperature.add(cols.get(0).text());
            System.out.println(temperature);
            temperature.add(cols.get(4).text());
            
            //System.out.println(temperature);
            //System.out.println(cols.get(0).text()+" : "+ cols.get(4).text());
            //System.out.println(temperature);
        }
                
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("sosa", "http://www.w3.org/ns/sosa/");
        model.setNsPrefix("obs","https://territoire.emse.fr/kg/emse/fayol/observation/");
        //model.setNsPrefix("temperature","");

        String inputtemp = temperature.toString();

        
        inputtemp = inputtemp.substring(1,inputtemp.length()-1);
        String[] splittemp = inputtemp.split("°C,");  
        FileWriter writer = new FileWriter("C:/Users/Meddy/Desktop/M2/semweb/SemanticWebProject/sto1.csv");
        for(String s : splittemp  ) {
            String[] split2 = s.split(",");
            
            writer.write(Arrays.asList(split2).stream().collect(Collectors.joining(",")));
            writer.write("\n");
            
        }


        writer.close();
        CSVReader reader = new CSVReader(new FileReader("C:/Users/Meddy/Desktop/M2/semweb/SemanticWebProject/sto1.csv"));

        String[] lineInArray;
        lineInArray = reader.readNext();
        while ((lineInArray = reader.readNext()) != null) {
            
            Resource observation=model.createResource("https://territoire.emse.fr/kg/emse/fayol/observation/"+"observation"+"-");
            observation.addProperty(RDF.type, model.createResource("http://www.w3.org/ns/sosa/"+"Observation"));
            //heures
            observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"observedProperty"),model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+lineInArray[0]));
            //temperature
            observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"ObservableProperty"),model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+lineInArray[1]));
        }

        model.write(System.out, "Turtle");
        String datasetURL = "http://localhost:3030/Data";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlUpdate = datasetURL + "/update";
        String graphStore = datasetURL + "/data";
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
        
        conneg.load(model); // add the content of model to the triplestore
        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore

    }
}
