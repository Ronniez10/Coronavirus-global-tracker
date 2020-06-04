package com.neelav.coronoavirustracker.controller;

import com.neelav.coronoavirustracker.models.CoronaStats;
import com.neelav.coronoavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model)
    {
        int sum=0;
        List<CoronaStats> getAllStats= coronaVirusDataService.getCoronaStatsList();
        int totalCases = getAllStats.stream().mapToInt(stat -> stat.getLatestTotal()).sum();
        int totalNewCases = getAllStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();
        model.addAttribute("totalReportedCases",totalCases);
        model.addAttribute("coronaStats",getAllStats);
        model.addAttribute("totalNewCases",totalNewCases);
        return "home";
    }
}
