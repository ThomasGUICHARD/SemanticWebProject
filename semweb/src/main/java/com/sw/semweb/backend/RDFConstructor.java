package com.sw.semweb.backend;
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
import java.sql.Timestamp;    
import java.util.Date; 
public class RDFConstructor {
    
    public RDFConstructor() throws CsvValidationException, IOException{
       // create an empty Model
       Model model = ModelFactory.createDefaultModel();
       

        model.setNsPrefix("sosa", "http://www.w3.org/ns/sosa/");
        model.setNsPrefix("salle","https://territoire.emse.fr/kg/emse/fayol/");
        model.setNsPrefix("obs","https://territoire.emse.fr/kg/emse/fayol/observation/");
       
        model.setNsPrefix("dul","http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#");

        model.setNsPrefix("schema","http://schema.org/");
       
        /*e4=ET4 et S431H=451H il faut modifier ça à la lecture */

        CSVReader reader = new CSVReader(new FileReader(".\\src\\main\\resources\\static\\data\\20211116-daily-sensor-measures.csv"));
        int obsNumber=0;
        String[] lineInArray;
        lineInArray = reader.readNext();
         //System.out.println(lineInArray);
      
       
       //on veut
       //name-0,time-1,TEMP-7,id-8,location-9,type-10
        while ((lineInArray = reader.readNext()) != null) {

            //System.out.println(this.formateRoom(lineInArray[9]));

            //We first define the sensor
            Resource sensor=model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+"sensor"+"-"+lineInArray[8].replace(" ", ""));
            //define type "sensor"
            sensor.addProperty(RDF.type, model.createResource("http://www.w3.org/ns/sosa/Sensor"));
            if(this.formateRoom(lineInArray[9])!=null){
            // define sensor location
                sensor.addProperty(model.createProperty("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#"+"hasLocation"), model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+this.formateRoom(lineInArray[9])));
            }
            if(lineInArray[7].length()!=0){
               
                obsNumber+=1;
                Resource observation=model.createResource("https://territoire.emse.fr/kg/emse/fayol/observation/"+"observation"+"-"+Integer.toString(obsNumber));

                //define type "observation"
                observation.addProperty(RDF.type, model.createResource("http://www.w3.org/ns/sosa/"+"Observation"));
                //define the sensor witch made the observation
                observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"madeBySensor"),sensor);
                //property observed
                observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"observedProperty"),model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+this.formateRoom(lineInArray[9])+"#temperature"));
                //time  "2017-06-06T12:36:12Z"^^xsd:dateTime
                observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"observedProperty"),model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+this.formateRoom(lineInArray[9])+"#temperature"));
                //Date observation
                observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"resultTime"),this.convertTimeStamp(lineInArray[1]),XSDGenericType.XSDdate);
                observation.addProperty(model.createProperty("http://schema.org/"+"value"),lineInArray[7],XSDGenericType.XSDfloat);
            
            }
            
        }

        RDFSender sender=new RDFSender(model);
       // model.write(System.out,"Turtle");

       // model.read("https://emse.fr/~zimmermann/antoine.ttl");
        //model.write(System.out,"Turtle");
        
    }


    //give the rigth adress format
    private String convertTimeStamp(String tss){
       
         String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (Long.parseLong(tss)/100000000))+"T"+new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (Long.parseLong(tss)/100000000))+"+00:00";

        //System.out.println(date);           
        return date.toString();

    }
    private String formateRoom(String salle){
                
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
        /*if(anchor==null){
            System.out.println(salle);
        }*/
        //mobile 1,2,3,4,5,6 ?
        return anchor;

    }



    //convert diverging names in use names
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
