package com.sw.semweb.backend;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

public class RDFSender {
    public RDFSender() {}
    public RDFSender(TtlFile f) {
        String datasetURL = "http://localhost:3030/DataSem";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlUpdate = datasetURL + "/update";
        String graphStore = datasetURL + "/data";
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);

        for(Model m : f.getListModel()){
            
        conneg.load(m); // add the content of model to the triplestore
        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
        }
       
        System.out.println("All is send");
    }
    public RDFSender(Model m) {
        String datasetURL = "http://localhost:3030/DataSem";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlUpdate = datasetURL + "/update";
        String graphStore = datasetURL + "/data";
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
        conneg.load(m); // add the content of model to the triplestore
        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
        
        System.out.println("All is send");
    }
    public void RDFGetter() {
        String datasetURL = "http://localhost:3030/DataSem";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlQuery = datasetURL + "/query";
        String graphStore = datasetURL + "/data";
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlQuery,graphStore);
        QueryExecution qExec = conneg.query("SELECT DISTINCT ?s { ?s ?p ?o }") ;
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            Resource subject = qs.getResource("s") ;
            System.out.println("Subject: "+subject) ;
        }
        qExec.close(); 
        
        
    }
}
