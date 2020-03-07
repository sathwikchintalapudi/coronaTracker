package com.learning.coronatracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoronaTrackerController {


    @Autowired
    CoronaTrackerService coronaTrackerService;


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("locationStats", coronaTrackerService.getCoronaDetails());
        Integer totalNewCases = coronaTrackerService.getCoronaDetails().stream().filter(s -> s.getVariation() != null).mapToInt(s -> s.getVariation()).sum();
        Integer totalCases = coronaTrackerService.getCoronaDetails().stream().filter(s -> !StringUtils.isEmpty(s.getConfirmedCases())).mapToInt(s -> Integer.parseInt(s.getConfirmedCases())).sum();
        model.addAttribute("TotalNewCases", totalNewCases);
        model.addAttribute("TotalCases", totalCases);
        return "home";
    }


}
