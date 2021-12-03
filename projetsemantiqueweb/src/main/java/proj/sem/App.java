package proj.sem;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, CsvValidationException
    {
        //RDFConstructor rdfConstr= new RDFConstructor();

       /* RDFConstructor rdfConstr2= new RDFConstructor(0);*/

       TtlFile f=new TtlFile();
       RDFSender rdfs=new RDFSender(f);


    }
}
