package com.sw.semweb.controllers;

import java.io.IOException;
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





@Controller
public class UserController {

   

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String accueil() throws CsvValidationException, IOException {
         //RDFConstructor rdfConstr= new RDFConstructor();

       /* RDFConstructor rdfConstr2= new RDFConstructor(0);*/

       /*TtlFile f=new TtlFile();
       RDFSender rdfs=new RDFSender(f);*/

      // RDFConstructor rdfc=new RDFConstructor();

       RDFSender rdfs=new RDFSender();
       rdfs.RDFGetter();
       
        return "accueil";
    }
   
   
}
