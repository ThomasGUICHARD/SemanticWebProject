package com.sw.semweb.backend;

import java.io.IOException;

import java.util.ArrayList;

import java.util.Date;


import com.opencsv.exceptions.CsvValidationException;

import org.apache.jena.datatypes.xsd.XSDDatatype.XSDGenericType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

import org.apache.jena.vocabulary.RDF;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScrapper {




    public  WebScrapper() throws CsvValidationException, IOException{
        int nbJour;
        int nbMois;
        int moisMin=10;
        int moisMax=11;
        int nbAnnee = 2021; 
        Document doc;  
      
        Element table; //select the 7th table.
        Elements rows;
        int idObs=0;
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("sosa", "http://www.w3.org/ns/sosa/");
        model.setNsPrefix("obs","https://territoire.emse.fr/kg/emse/fayol/observation/");
        model.setNsPrefix("schema","http://schema.org/");
        model.setNsPrefix("salle","https://territoire.emse.fr/kg/emse/fayol/");
        model.setNsPrefix("dul","http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#");
        Resource observation;

        //external sensor
        Resource sensor=model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+"sensor-ext");
        sensor.addProperty(RDF.type, model.createResource("http://www.w3.org/ns/sosa/Sensor"));
        sensor.addProperty(model.createProperty("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#"+"hasLocation"), model.createResource("https://territoire.emse.fr/kg/emse/fayol/"+"ext"));
        
        for(nbMois=moisMin-1; nbMois<moisMax && nbMois<12;nbMois++){
            for(nbJour=1;nbJour<=31;nbJour++){

                
                if(nbMois==1 && ((nbAnnee % 4)==0 ||(nbAnnee % 100)==0 ) && nbJour>29){}
                else if(nbMois==1 && nbJour>28){}
                else if((nbMois==3 || nbMois==5 || nbMois==8 || nbMois==10) && nbJour>30){}
                else{

                    doc = Jsoup.connect("https://www.meteociel.fr/temps-reel/obs_villes.php?code2=7475&jour2="+nbJour+"&mois2="+nbMois+"&annee2="+nbAnnee).get();  
                    table = doc.select("table").get(7); //select the 7th table.
                    rows = table.select("tr");
                    for (int i = 1; i < rows.size(); i++) { //start at 1 to skip 
                        Element row = rows.get(i);
                        Elements cols = row.select("td");
                        /*System.out.println(cols.get(0).text()+" : "+ cols.get(4).text());
                        System.out.println(cols.get(0).text().replaceAll(" h", "")+" : "+ cols.get(4).text().replaceAll(" °C", ""));*/

                        //System.out.println(this.designDate(nbAnnee, nbMois, nbJour, cols.get(0).text().replaceAll(" h", "")));
                        
                        idObs +=1;
                        observation=model.createResource("https://territoire.emse.fr/kg/emse/fayol/observation/ObservationExt-"+nbAnnee+"-"+idObs);
                        observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"madeBySensor"),sensor);
                        observation.addProperty(RDF.type, model.createResource("http://www.w3.org/ns/sosa/"+"Observation"));
                        //heures
                        observation.addProperty(model.createProperty("http://www.w3.org/ns/sosa/"+"resultTime"),this.designDate(nbAnnee-1, nbMois+1, nbJour, cols.get(0).text().replaceAll(" h", "")),XSDGenericType.XSDdate);
                        //temperature
                        observation.addProperty(model.createProperty("http://schema.org/"+"value"),cols.get(4).text().replaceAll(" °C", ""),XSDGenericType.XSDfloat);

               
                    }
                }

            }
        }
        //suite code
        this.temporarySend(model);
        System.out.println("endScraping");

    }

    public String designDate(int y, int m,int d,String h){
        Date date = new Date(y-1900, m, d, Integer.valueOf(h), 0);
        String sdate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date)+"T"+new java.text.SimpleDateFormat("HH:mm:ss").format(date)+"+00:00";
        return sdate;
    }

    

    public void temporarySend(Model m){

        String datasetURL = "http://localhost:3030/DataSem";
        String sparqlEndpoint = datasetURL + "/sparql";
        String sparqlUpdate = datasetURL + "/update";
        String graphStore = datasetURL + "/data";
        RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
        
        conneg.load(m); // add the content of model to the triplestore
        conneg.update("INSERT DATA { <test> a <TestClass> }"); // add the triple to the triplestore
    }
}

