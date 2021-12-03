package proj.sem;
import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.jena.datatypes.xsd.XSDDatatype.XSDGenericType;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class RDFConstructor {
    
    public RDFConstructor() throws CsvValidationException, IOException{
       // create an empty Model
       Model model = ModelFactory.createDefaultModel();
       //model.setNsPrefix("rdfs", RDFS.uri);
        model.setNsPrefix("name", "http://www.example.com/");
        model.setNsPrefix("time", "http://www.w3.org/2006/time#");
        model.setNsPrefix("location", "http://www.w3.org/2001/XMLSchema#");
        model.setNsPrefix("type","https://www.w3.org/TR/vocab-ssn/");
        
        //stops200.txt
        //J'ai des problème d'encodage de caractères au niveau du CSVReader mais sinon tout marche

        CSVReader reader = new CSVReader(new FileReader("C:/Users/meddy/Desktop/datas.txt"));
        String[] lineInArray;
        lineInArray = reader.readNext();
        while ((lineInArray = reader.readNext()) != null) {
            
            Resource root=model.createResource("http://www.example.com/"+lineInArray[0].replace(" ", ""));
            root.addProperty(model.createProperty("http://www.w3.org/2006/time#"), lineInArray[1]);
            //root.addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#"+"lat"), lineInArray[3] ,XSDGenericType.XSDdecimal);
            root.addProperty(model.createProperty("https://www.w3.org/TR/vocab-ssn/"), lineInArray[10]);
        
            model.add(root,RDF.type,model.createResource("http://www.w3.org/2003/01/geo/wgs84_pos#"+"SpatialThing"));
        }
        
        
        model.write(System.out, "Turtle");
        
        
    }

    public RDFConstructor(int i) throws CsvValidationException, IOException{
    
        Model model = ModelFactory.createDefaultModel();
        
        /*model.setNsPrefix("ex", "http://www.example.com/");
        model.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");go
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        
        
        CSVReader reader = new CSVReader(new FileReader(".\\data\\stops.txt"));
        String[] lineInArray;
        lineInArray = reader.readNext();
        while ((lineInArray = reader.readNext()) != null) {
            
            Resource root=model.createResource("http://www.example.com/"+lineInArray[0].replace(" ", "")).addProperty(RDFS.label, model.createLiteral(lineInArray[1], "fr"));
            root.addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#"+"lat"), lineInArray[3] ,XSDGenericType.XSDdecimal);
            root.addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#"+"long"), lineInArray[4] ,XSDGenericType.XSDdecimal);
        
            model.add(root,RDF.type,model.createResource("http://www.w3.org/2003/01/geo/wgs84_pos#"+"SpatialThing"));
        }*/


        // ... build the model
        String datasetURL = "http://localhost:3030/myDataset";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlUpdate = datasetURL + "/update";
        String graphStore = datasetURL + "/data";
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
        conneg.load(model); // add the content of model to the triplestore
        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
     }
}
