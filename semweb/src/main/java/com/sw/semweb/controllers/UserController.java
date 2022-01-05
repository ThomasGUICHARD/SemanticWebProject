package com.sw.semweb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvValidationException;
import com.sw.semweb.backend.RDFConstructor;
import com.sw.semweb.backend.RDFSender;
import com.sw.semweb.backend.TtlFile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

@Controller
public class UserController {

   

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String accueil(org.springframework.ui.Model model) throws CsvValidationException, IOException {
         
        String datasetURL = "http://localhost:3030/Data";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlQuery = datasetURL + "/query";
        String graphStore = datasetURL + "/data";
        ArrayList<String> listeRoom =new ArrayList<String>();
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlQuery,graphStore);

      

        QueryExecution qExec = conneg.query("prefix bot: <https://w3id.org/bot#> SELECT DISTINCT ?s { ?s a bot:Space . }") ;
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
    @RequestMapping(value = "/GetRoomList", method = RequestMethod.GET)
    public String getRoomList() {
        
        return "";
    }
   
}
