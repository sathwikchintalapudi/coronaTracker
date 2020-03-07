package com.learning.coronatracker;

import domain.RegionDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CoronaTrackerService {
    private static final String CORONA_STAT_FEEDER = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private final static Logger LOGGER = Logger.getLogger(CoronaTrackerService.class.getName());

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public List<RegionDetails> getCoronaDetails() {

        List<RegionDetails> consolidatedStats = new ArrayList<>();
        try {

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(CORONA_STAT_FEEDER)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            StringReader stringReader = new StringReader(httpResponse.body());
            List<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader).getRecords();
            records.stream().forEach(record -> {
                RegionDetails regionDetails = new RegionDetails();
                regionDetails.setState(record.get("Province/State"));
                regionDetails.setCountry(record.get("Country/Region"));
                regionDetails.setConfirmedCases(record.get(record.size() - 1));
                if (!StringUtils.isEmpty(record.get(record.size() - 1)) && !StringUtils.isEmpty(record.get(record.size() - 2))) {
                    regionDetails.setVariation(((Integer.parseInt(record.get(record.size() - 1)) - Integer.parseInt(record.get(record.size() - 2)))));
                }
                consolidatedStats.add(regionDetails);
            });
        } catch (Exception e) {
            LOGGER.info("Exception occurred while consolidating statistics");
        }
        return consolidatedStats;
    }
}
