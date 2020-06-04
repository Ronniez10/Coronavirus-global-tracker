package com.neelav.coronoavirustracker.services;

import com.neelav.coronoavirustracker.models.CoronaStats;
import com.sun.net.httpserver.Headers;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import java.util.List;

@Service
public class CoronaVirusDataService {


    private static String VIRUS_DATA_URL ="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<CoronaStats> coronaStatsList=  new ArrayList();

    public List<CoronaStats> getCoronaStatsList() {
        return coronaStatsList;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {


       List<CoronaStats> newStatsList = new ArrayList<>();

       HttpClient client = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();


        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //System.out.println(httpResponse.body());

        StringReader csvBodyReader = new StringReader(httpResponse.body());


        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            CoronaStats coronaStats = new CoronaStats();
            coronaStats.setState(record.get("Province/State"));
            coronaStats.setCountry(record.get("Country/Region"));
            //Getting the Last value of the Record
            coronaStats.setLatestTotal(Integer.parseInt(record.get(record.size()-1)));

            int latestCases = Integer.parseInt(record.get(record.size()-1));
            int previousDayCases = Integer.parseInt(record.get(record.size()-2));

            coronaStats.setDiffFromPreviousDay(latestCases-previousDayCases);


            newStatsList.add(coronaStats);
        }

        this.coronaStatsList=newStatsList;
    }
}
