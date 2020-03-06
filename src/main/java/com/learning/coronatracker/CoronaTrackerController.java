package com.learning.coronatracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoronaTrackerController {


    @Autowired
    CoronaTrackerService coronaTrackerService;


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("locationStats", coronaTrackerService.getCoronaDetails());
        Integer totalNewCases = coronaTrackerService.getCoronaDetails().stream().mapToInt(s -> s.getVariation()).sum();
        Integer tatalCases = coronaTrackerService.getCoronaDetails().stream().mapToInt(s -> Integer.parseInt(s.getConfirmedCases())).sum();
        model.addAttribute("TotalNewCases", totalNewCases);
        model.addAttribute("TotalCases", tatalCases);
        return "home";
    }


}
