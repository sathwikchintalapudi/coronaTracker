package com.learning.coronatracker;

import domain.RegionDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CoronaTrackerService {

    private static final String CORONA_STAT_FEEDER = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/02-29-2020.csv";

    @PostConstruct
   // @Scheduled(cron = "* * 1 * * *")
    public void getCoronaDetails() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(CORONA_STAT_FEEDER)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            StringReader stringReader = new StringReader(httpResponse.body());
            List<CSVRecord> data = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader).getRecords();
            List<RegionDetails> consolidatedStats = new ArrayList<>();
            for (CSVRecord record : data) {
                RegionDetails regionDetails = new RegionDetails();
                regionDetails.setState(record.get("Province/State"));
                regionDetails.setCountry(record.get("Country/Region"));
                regionDetails.setConfirmedCases(record.get("Confirmed"));
                regionDetails.setDeaths(record.get("Deaths"));
                regionDetails.setRecovered(record.get("Recovered"));
                consolidatedStats.add(regionDetails);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while consolidating statistics");
        }
    }
}