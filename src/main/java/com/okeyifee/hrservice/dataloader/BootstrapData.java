package com.okeyifee.hrservice.dataloader;

import com.okeyifee.hrservice.scraper.impl.ScraperRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class BootstrapData{

    private final Logger logger = LoggerFactory.getLogger(BootstrapData.class);

    private final Environment environment;
    private final ScraperRun scraperRun;

    @Autowired
    public BootstrapData(Environment environment, ScraperRun scraperRun) {
        this.environment = environment;
        this.scraperRun = scraperRun;
    }

    @EventListener
    public void run(ContextRefreshedEvent event) throws Exception {
        logger.info("running in post construct");
        scraperRun.run();
    }
}