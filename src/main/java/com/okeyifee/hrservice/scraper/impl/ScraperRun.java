package com.okeyifee.hrservice.scraper.impl;

import com.okeyifee.hrservice.scraper.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ScraperRun {

    private final Logger logger = LoggerFactory.getLogger(ScraperRun.class);

    AlikayNaturalsScrapper alikayNaturalsScrapper;
    AubreyorganicsScrapper aubreyorganicsScrapper;
    BriogeohairScrapper briogeohairScrapper;
    UncleFunkyDaughterScrapper uncleFunkyDaughterScrapper;
    CurlSmithMainScrapper curlSmithMainScrapper;
    GreenCollectionScraperService greenCollectionScraper;
    EdenScraperService edenScraper;
    GirlAndHairScraperService girlAndHairScraper;
    JaneCarterScraperService janeCarterScraper;
    MelaninHairCareScrapper melaninHairCareScrapper;
    MielliOrganicsScrapper mielliOrganicsScrapper;
    TheDouxScraperService theDouxScraper;
    PacificaScraperService pacificaScraper;
    NaturalHairScrapper naturalHairScrapper;
    MauimoistureScrapper mauimoistureScrapper;
    HoneysScrapper honeysScrapper;

    @Autowired
    public ScraperRun(AlikayNaturalsScrapper alikayNaturalsScrapper,
                      AubreyorganicsScrapper aubreyorganicsScrapper,
                      BriogeohairScrapper briogeohairScrapper,
                      UncleFunkyDaughterScrapper uncleFunkyDaughterScrapper,
                      CurlSmithMainScrapper curlSmithMainScrapper,
                      GreenCollectionScraperService greenCollectionScraper,
                      EdenScraperService edenScraper,
                      GirlAndHairScraperService girlAndHairScraper,
                      JaneCarterScraperService janeCarterScraper,
                      MelaninHairCareScrapper melaninHairCareScrapper,
                      MielliOrganicsScrapper mielliOrganicsScrapper,
                      TheDouxScraperService theDouxScraper,
                      PacificaScraperService pacificaScraper,
                      NaturalHairScrapper naturalHairScrapper,
                      MauimoistureScrapper mauimoistureScrapper,
                      HoneysScrapper honeysScrapper) {

        this.alikayNaturalsScrapper = alikayNaturalsScrapper;
        this.aubreyorganicsScrapper = aubreyorganicsScrapper;
        this.briogeohairScrapper = briogeohairScrapper;
        this.uncleFunkyDaughterScrapper = uncleFunkyDaughterScrapper;
        this.curlSmithMainScrapper = curlSmithMainScrapper;
        this.edenScraper = edenScraper;
        this.girlAndHairScraper = girlAndHairScraper;
        this.greenCollectionScraper = greenCollectionScraper;
        this.janeCarterScraper = janeCarterScraper;
        this.melaninHairCareScrapper = melaninHairCareScrapper;
        this.mielliOrganicsScrapper = mielliOrganicsScrapper;
        this.theDouxScraper = theDouxScraper;
        this.pacificaScraper = pacificaScraper;
        this.naturalHairScrapper = naturalHairScrapper;
        this.mauimoistureScrapper = mauimoistureScrapper;
        this.honeysScrapper = honeysScrapper;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone="Europe/Rome")
    public void run() throws Exception{

        int MY_THREADS = 10;
        ExecutorService executor = null;
        try {
            executor = Executors.newFixedThreadPool(MY_THREADS);
            List<ScraperService> scrappers = new ArrayList<>();

			scrappers.add(alikayNaturalsScrapper);
			scrappers.add(aubreyorganicsScrapper);
			scrappers.add(briogeohairScrapper);
            scrappers.add(uncleFunkyDaughterScrapper);
            scrappers.add(curlSmithMainScrapper);
            scrappers.add(edenScraper);
            scrappers.add(girlAndHairScraper);
            scrappers.add(greenCollectionScraper);
            scrappers.add(honeysScrapper);
            scrappers.add(janeCarterScraper);
            scrappers.add(mauimoistureScrapper);
            scrappers.add(melaninHairCareScrapper);
            scrappers.add(mielliOrganicsScrapper);
            scrappers.add(naturalHairScrapper);
            scrappers.add(pacificaScraper);
            scrappers.add(theDouxScraper);


            for (ScraperService scrapper : scrappers) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        scrapper.scrape();
                    }
                });
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (executor != null) {
                executor.shutdown();
                logger.info("\nFinished running all threads");
            }
        }
    }
}
