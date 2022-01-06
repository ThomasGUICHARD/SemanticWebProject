package com.sw.semweb.controllers;

import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.exceptions.CsvValidationException;
import com.sw.semweb.backend.RDFConstructor;
import com.sw.semweb.backend.WebScrapper;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    ArrayList<String> listeRoom=new ArrayList<String>();;

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String accueil(org.springframework.ui.Model model) throws CsvValidationException, IOException {
        String datasetURL = "http://localhost:3030/Data";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlQuery = datasetURL + "/query";
        String graphStore = datasetURL + "/data";
        int id=0;
        listeRoom.clear();
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlQuery,graphStore);
        model.addAttribute("id", id);
        QueryExecution qExec = conneg.query("PREFIX sosa: <http://www.w3.org/ns/sosa/>"+
        "PREFIX schema: <http://schema.org/>"+
        "SELECT ?s {?s a sosa:Observation . } LIMIT 200");
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            Resource subject = qs.getResource("s") ;
            listeRoom.add(subject.toString());
            //System.out.println("Subject: "+subject) ;
        }
        model.addAttribute("listRoom",listeRoom);
        
        qExec.close();
        return "accueil";
    }
    
    @PostMapping("/getResult")
    
    public String getResult(@RequestParam(name = "room") String leNom ,org.springframework.ui.Model model) {
        System.out.println(leNom);
        
        return "redirect:/";
    }
   
}