package com.rhy.hadokentest;

import com.hadoken.framework.scheduling.annotation.ManagedScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 20:48
 */
@Slf4j
@Component
public class ScheduleTest {

    @Scheduled(fixedRate = 10000)
    @ManagedScheduled(id = "critical-job", fixedRateString = "10000")
    public void doCriticalWork() {
        System.out.println("just for test...");
    }
}
