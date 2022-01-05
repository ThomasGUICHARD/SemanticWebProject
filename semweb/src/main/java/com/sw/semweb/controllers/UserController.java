package com.sw.semweb.controllers;

import java.io.IOException;

import com.github.andrewoma.dexx.collection.List;
import com.opencsv.exceptions.CsvValidationException;
import com.sw.semweb.backend.RDFConstructor;
import com.sw.semweb.backend.RDFSender;
import com.sw.semweb.backend.TtlFile;
import net.minidev.json.JSONObject;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;





@Controller
public class UserController {

   

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String accueil() throws CsvValidationException, IOException {
        RDFConstructor rdfConstr= new RDFConstructor();

        //RDFConstructor rdfConstr2= new RDFConstructor(0);

       TtlFile f=new TtlFile();
       RDFSender rdfs=new RDFSender(f);

       //RDFConstructor rdfc=new RDFConstructor();
       
        return "accueil";
    }

    @RequestMapping(value= {"/request"}, method = RequestMethod.GET)
    public void request(){
        String queryString=
 
        "PREFIX sc: <http://purl.org/science/owl/sciencecommons/>"+
        "PREFIX schema: <http://schema.org/>"+


        "SELECT ?subsubject ?subject ?date ?val"+
        "WHERE {"+
        "?subject sosa:madeBySensor ?subsubject."+
        "?subject ?predicate sosa:Observation."+
        "?subject sosa:resultTime ?date."+
        "?subject schema:value ?val."+
        
        "}"+
        "LIMIT 200";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/snorql", query);
        qexec.close();
        
    }



    
   
}
