package proj.sem;



import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScrapper {
    public WebScrapper() throws IOException{
        Document doc = Jsoup.connect("https://www.meteociel.fr/temps-reel/obs_villes.php?code2=7475&jour2=16&mois2=10&annee2=2021").get();  
        ArrayList<String> temperature = new ArrayList<>();
        Element table = doc.select("table").get(7); //select the first table.
        Elements rows = table.select("tr");

        for (int i = 1; i < rows.size(); i++) { //start at 1 to skip 
            Element row = rows.get(i);
            Elements cols = row.select("td");
            temperature.add(cols.get(4).text());
            System.out.println(cols.get(4).text());
            //System.out.println(temperature);
        }
    }
}
