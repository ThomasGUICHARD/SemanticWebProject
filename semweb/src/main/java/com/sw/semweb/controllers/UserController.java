package com.sw.semweb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.jena.sparql.function.library.print;

@Controller
public class UserController {

    ArrayList<String> listeRoom=new ArrayList<String>();
    ArrayList<Observation> listeObservationInterne=new ArrayList<Observation>();
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
         
        System.out.println("enter accueil");
        model.addAttribute("listRoom",listeRoom);
        model.addAttribute("listeObservationInterne",listeObservationInterne);
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
        System.out.println(leNom);
        listeObservationInterne.clear();
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlQuery,graphStore);
        QueryExecution qExec = conneg.query("prefix bot: <https://w3id.org/bot#>"+ 
        "prefix dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>"+
        "prefix sosa: <http://www.w3.org/ns/sosa/>"+ 
        "prefix schema:<http://schema.org/>"+
        "prefix salle:<https://territoire.emse.fr/kg/emse/fayol/>"+
        "SELECT ?s ?o ?t ?v { ?s a sosa:Sensor; dul:hasLocation <"+leNom+">. ?o sosa:madeBySensor ?s; sosa:resultTime ?t; schema:value ?v.} LIMIT 10") ;
       /* "SELECT ?s ?o ?t ?v ?ve { ?s a sosa:Sensor; dul:hasLocation <"+leNom+">. ?o sosa:madeBySensor ?s; sosa:resultTime ?t; schema:value ?v."+
        " ?oe sosa:madeBySensor salle:sensor-ext; sosa:resultTime ?t; schema:value ?ve. }") ;*/
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            Resource sensor = qs.getResource("s") ;
            Resource observation = qs.getResource("o") ;
            Literal time = qs.getLiteral("t") ;
            Literal value = qs.getLiteral("v") ;
           System.out.println("\n\n ###########################interieur:\n "+sensor+"\n"+observation+"\n"+time.getString()+"\n"+value.getString()+"\n\n") ;
            QueryExecution qExec2 = conneg.query("prefix bot: <https://w3id.org/bot#>"+ 
                                "prefix dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>"+
                                "prefix sosa: <http://www.w3.org/ns/sosa/>"+ 
                                "prefix schema:<http://schema.org/>"+
                                "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
                                "prefix salle:<https://territoire.emse.fr/kg/emse/fayol/>"+
                                "SELECT ?oe ?v{  ?oe sosa:madeBySensor <https://territoire.emse.fr/kg/emse/fayol/sensor-ext>; sosa:resultTime ?t2; schema:value ?v."+
                                " FILTER ("+
                                "HOURS(?t2) = "+String.valueOf(getHours(time.getString()))+" &&"+
                                "DAY(?t2) = "+String.valueOf(getDay(time.getString()))+" &&"+
                                "MONTH(?t2) = "+String.valueOf(getMonth(time.getString()))+" &&"+
                                "YEAR(?t2) = "+String.valueOf(getYear(time.getString()))+""+")}") ;
                                /**/
                                
            ResultSet rs2 = qExec2.execSelect() ;
            Literal value2;
            while(rs2.hasNext()){
                QuerySolution qs2 = rs2.next() ;
                value2 = qs2.getLiteral("v") ;
                listeObservationInterne.add(new Observation(leNom,observation.toString(), time.toString(), sensor.toString(), value.getFloat(),value2.getFloat()));
               // System.out.println("\n\n ###########################exterieur:\n "+observation2.toString()+"\n\n") ;
            }
            
           qExec2.close();
           
        }
        qExec.close();
        System.out.println(listeObservationInterne.size());
        return "redirect:/accueil";
    }
 
    public int getHours(String s){
        String dateP = "/^(\\d{1,4})-(\\d{1,2})-(\\d{1,2})T(\\d{1,2}):([^<]*)$/";
        Pattern p = Pattern.compile(dateP);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            return Integer.getInteger( m.group(4));

        }


        return 8;
    }
    public int getDay(String s){
        String dateP = "/^(\\d{1,4})-(\\d{1,2})-(\\d{1,2})T(\\d{1,2}):([^<]*)$/";
        Pattern p = Pattern.compile(dateP);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            return Integer.getInteger( m.group(3));

        }
        return 1;
    }
    public int getMonth(String s){
        String dateP = "/^(\\d{1,4})-(\\d{1,2})-(\\d{1,2})T(\\d{1,2}):([^<]*)$/";
        Pattern p = Pattern.compile(dateP);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            return Integer.getInteger( m.group(4));

        }
        return 11;
    }
    public int getYear(String s){
        String dateP = "/^(\\d{1,4})-(\\d{1,2})-(\\d{1,2})T(\\d{1,2}):([^<]*)$/";
        Pattern p = Pattern.compile(dateP);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            return Integer.getInteger( m.group(5));

        }
        return 2021;
    }
}
