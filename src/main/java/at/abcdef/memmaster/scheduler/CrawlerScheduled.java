package at.abcdef.memmaster.scheduler;

import at.abcdef.memmaster.config.ApplicationProperties;
import at.abcdef.memmaster.config.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CrawlerScheduled {
    private static final Logger log = LoggerFactory.getLogger(CrawlerScheduled.class);

    private final ApplicationProperties applicationProperties;

    public CrawlerScheduled(@Autowired ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Scheduled(fixedRate = Constants.PURGE_ITEMS_PERIOD)
    public void purgeOldMails() {
        
    }
}
