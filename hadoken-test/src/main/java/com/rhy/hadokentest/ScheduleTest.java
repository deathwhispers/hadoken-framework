package com.rhy.hadokentest;

import com.hadoken.framework.scheduler.annotation.EnhanceScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 20:48
 */
@Slf4j
@Component
public class ScheduleTest {

    //    @Scheduled(fixedRate = 5000)
//    @EnhanceScheduled(id = "critical-job2", fixedRate = 4000)
    public void doCriticalWork() {
        System.out.println("doCriticalWork 1111111 ...");
    }

//    @EnhanceScheduled(id = "critical-job", fixedRate = 5000)
    public void doCriticalWork2() {
        System.out.println("doCriticalWork 2222222 ...");
    }

    public void create_test() {
        System.out.println("create_test");
    }
}
