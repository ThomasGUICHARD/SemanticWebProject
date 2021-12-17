package proj.sem;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
       /* model.setNsPrefix("ex", "http://www.example.com/");
        model.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");*/
        
        //stops200.txt
        //J'ai des problème d'encodage de caractères au niveau du CSVReader mais sinon tout marche
        /*sosa:resultTime
        
<TemperatureSensor/1> a ex:TemperatureSensor , ssn:System .

<Observation/1> a sosa:Observation ;
  sosa:madeBySensor <TemperatureSensor/1> .


        <Observation/1> a sosa:Observation ;
  sosa:observedProperty ex:Temperature ;
  owl:hasValue


  <Observation/346344> rdf:type sosa:Observation ;
  sosa:observedProperty <sensor/35-207306-844818-0/BMP282/atmosphericPressure> ;
  sosa:hasFeatureOfInterest  <earthAtmosphere> ;
  sosa:madeBySensor <sensor/35-207306-844818-0/BMP282> ;
  sosa:hasSimpleResult "1021.45 hPa"^^cdt:ucum ;
  sosa:resultTime "2017-06-06T12:36:12Z"^^xsd:dateTime .
        */

        model.setNsPrefix("sosa", "http://www.w3.org/ns/sosa/");
        model.setNsPrefix("salle","https://territoire.emse.fr/kg/emse/fayol/");
        /*e4=ET4 et S431H=451H il faut modifier ça à la lecture */

        CSVReader reader = new CSVReader(new FileReader(".\\Data\\20211116-daily-sensor-measures.csv"));
        String[] lineInArray;
        lineInArray = reader.readNext();
       // System.out.println(lineInArray);
      
       
        while ((lineInArray = reader.readNext()) != null) {

            System.out.println(this.formateSalle(lineInArray[9]));

         /*   Resource root=model.createResource("<https://territoire.emse.fr/kg/"+"sensor"+"/"+lineInArray[8].replace(" ", "")).addProperty(RDF.type, model.createResource("http://www.w3.org/ns/sosa/Sensor"));
            
            Resource root2=model.createResource("<https://territoire.emse.fr/kg/"+ lineInArray[8]+"-AT-"+lineInArray[1]).addProperty(RDF.type, model.createResource("http://www.w3.org/ns/sosa/Observation"));
           */ 
            
            /*if(){
                root.addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#"+"lat"), lineInArray[3] ,XSDGenericType.XSDdecimal);
                }*/
           /* root.addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#"+"lat"), lineInArray[3] ,XSDGenericType.XSDdecimal);
            root.addProperty(model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#"+"long"), lineInArray[4] ,XSDGenericType.XSDdecimal);
        */
          //  model.add(root,RDF.type,model.createResource("http://www.w3.org/2003/01/geo/wgs84_pos#"+"SpatialThing"));
        }


       // model.read("https://emse.fr/~zimmermann/antoine.ttl");
        //model.write(System.out,"Turtle");
        
    }

    private String formateSalle(String salle){
                
        String uriPattern = "emse/fayol/e([^<]*)/S([^<]*)";
        String uriOtherRoom ="emse/fayol/e([^<]*)/([^<]*)";
        String anchor = null;
        

        Pattern p = Pattern.compile(uriPattern);
        Pattern p2 = Pattern.compile(uriOtherRoom);
        Matcher m = p.matcher(salle);
        if (m.matches()) {
            anchor = m.group(1)+"ET"+"/"+m.group(2);

        }else{
            m = p2.matcher(salle);
            if (m.matches()) {
                anchor = m.group(1)+"ET"+"/"+replace(m.group(2));
            } 
        }
        return anchor;

    }
    private String replace(String specialRoom){

        switch(specialRoom){
   
            case "Hall4Nord": 
                return "north-corridor";
                
            case "Hall4Sud":
                return "south-corridor"; 

            default:
                return specialRoom;
                
        }

    
    }

}
