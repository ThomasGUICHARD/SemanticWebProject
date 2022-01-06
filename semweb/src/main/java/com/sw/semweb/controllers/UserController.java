package com.sw.semweb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvValidationException;
import com.sw.semweb.backend.RDFConstructor;
import com.sw.semweb.backend.RDFSender;
import com.sw.semweb.backend.TtlFile;
import com.sw.semweb.backend.WebScrapper;
import com.sw.semweb.model.Observation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.minidev.json.JSONObject;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

@Controller
public class UserController {

    ArrayList<String> listeRoom=new ArrayList<String>();
   // ArrayList<Observation> listeObservationInterne=new ArrayList<Observation>();
    String datasetURL = "http://localhost:3030/DataSem";
    String sparqlEndpoint = datasetURL + "/sparql";
    String sparqlQuery = datasetURL + "/query";
    String graphStore = datasetURL + "/data";
    



    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String init(org.springframework.ui.Model model) throws CsvValidationException, IOException {
        TtlFile ttlf=new TtlFile();
        RDFSender rdfs= new RDFSender(ttlf);
        System.out.println("Fin parsing plateform territoire\n");
        WebScrapper ws=new WebScrapper();
        System.out.println("Fin parsing site météo\n");
        RDFConstructor rdfc= new RDFConstructor();
        System.out.println("Fin parsing short csv\n");

        return "redirect:/accueil";
    }

    @RequestMapping(value = { "/accueil" }, method = RequestMethod.GET)
    public String accueil(org.springframework.ui.Model model) throws CsvValidationException, IOException {
         
        
        model.addAttribute("listRoom",listeRoom);
        //model.addAttribute("listeObservationInterne",listeObservationInterne);
        listeRoom.clear();
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlQuery,graphStore);
        QueryExecution qExec = conneg.query("prefix bot: <https://w3id.org/bot#>"+
        "prefix dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>"+
        "prefix sosa: <http://www.w3.org/ns/sosa/>"+
        " SELECT DISTINCT ?s { ?s a bot:Space . ?se a sosa:Sensor; dul:hasLocation ?s. }") ;
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            Resource subject = qs.getResource("s") ;
            listeRoom.add(subject.toString());
            //System.out.println("Subject: "+subject) ;
        }
        
        
        qExec.close();
        return "accueil";
    }
    
    @PostMapping("/getResult")
    
    public String getResult(@RequestParam(name = "room") String leNom ,org.springframework.ui.Model model) {
       // System.out.println(leNom);

        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlQuery,graphStore);
        QueryExecution qExec = conneg.query("prefix bot: <https://w3id.org/bot#>"+ 
        "prefix dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>"+
        "prefix sosa: <http://www.w3.org/ns/sosa/>"+ 
        "prefix schema:<http://schema.org/>"+
        "prefix salle:<https://territoire.emse.fr/kg/emse/fayol/>"+
        "SELECT ?s ?o ?t ?v  { ?s a sosa:Sensor; dul:hasLocation <"+leNom+">. ?o sosa:madeBySensor ?s; sosa:resultTime ?t; schema:value ?v."+
        "} LIMIT 200") ;
       /* "SELECT ?s ?o ?t ?v ?ve { ?s a sosa:Sensor; dul:hasLocation <"+leNom+">. ?o sosa:madeBySensor ?s; sosa:resultTime ?t; schema:value ?v."+
        " ?oe sosa:madeBySensor salle:sensor-ext; sosa:resultTime ?t; schema:value ?ve. }") ;*/
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            Resource sensor = qs.getResource("s") ;
            Resource observation = qs.getResource("o") ;
            Literal time = qs.getLiteral("t") ;
            Literal value = qs.getLiteral("v") ;
           /* QueryExecution qExec2 = conneg.query("prefix bot: <https://w3id.org/bot#>"+ 
                                "prefix dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>"+
                                "prefix sosa: <http://www.w3.org/ns/sosa/>"+ 
                                "prefix schema:<http://schema.org/>"+
                                "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
                                "prefix salle:<https://territoire.emse.fr/kg/emse/fayol/>"+
                                "SELECT ?oe {  ?oe sosa:madeBySensor <https://territoire.emse.fr/kg/emse/fayol/sensor-ext>; sosa:resultTime \""+time.getString()+"\"^^xsd:date.}") ;
            ResultSet rs2 = qExec2.execSelect() ;
            while(rs2.hasNext()){
                QuerySolution qs2 = rs.next() ;
                Resource observation2 = qs.getResource("oe") ;
                System.out.println("\n\n ###########################exterieur:\n "+observation2+"\n\n") ;
            }*/
            
           System.out.println("\n\n ###########################interieur:\n "+sensor+"\n"+observation+"\n"+time.getString()+"\n"+value.getString()+"\n\n") ;
           
        }
        
        return "redirect:/accueil";
    }
   /* observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"madeBySensor"),sensor);
    //property observed
    observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"observedProperty"),model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+this.formateRoom(lineInArray[9])+"#temperature"));
    //time  "2017-06-06T12:36:12Z"^^xsd:dateTime
    observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"observedProperty"),model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+this.formateRoom(lineInArray[9])+"#temperature"));
    //Date observation
    observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"resultTime"),this.convertTimeStamp(lineInArray[1]),XSDGenericType.XSDdate);
    observation.addProperty(model.createProperty("http://schema.org/"+"value"),lineInArray[7],XSDGenericType.XSDfloat);*/
}
