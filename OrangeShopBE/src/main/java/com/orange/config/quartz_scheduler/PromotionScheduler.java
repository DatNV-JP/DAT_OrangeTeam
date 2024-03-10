package com.orange.config.quartz_scheduler;

import com.orange.services.IPromotionService;
import com.orange.services.impl.WebsocketNotificationService;
import com.orange.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class PromotionScheduler {
    private ScheduledExecutorService scheduler;
    private final IPromotionService promotionService;
    private final WebsocketNotificationService websocketNotificationService;


    @PreDestroy
    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    //    0 0 0 * * ? mỗi ngày 00h
//    0 * * ? * *
    @Scheduled(cron = "0 0 0 * * ?")
    public void cronJobSch() {
        promotionService.stopWorkingPromotion(Constants.IS_DATE.IS_DATE);
        promotionService.stopWorkingPromotionInActive(Constants.IS_DATE.IS_DATE);
        promotionService.startPromotion(Constants.IS_DATE.IS_DATE);
    }

    @Scheduled(cron = "0 0 * ? * *")
    public void cronJobSchIsHours() {
        promotionService.stopWorkingPromotion(Constants.IS_DATE.IS_HOURS);
        promotionService.stopWorkingPromotionInActive(Constants.IS_DATE.IS_HOURS);
        promotionService.startPromotion(Constants.IS_DATE.IS_HOURS);
    }

    @PostConstruct
    public void init() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::cronJobSch, 5, TimeUnit.SECONDS); //chạy sau khi khởi động 5s
        scheduler.schedule(this::cronJobSchIsHours, 10, TimeUnit.SECONDS);
    }
}
