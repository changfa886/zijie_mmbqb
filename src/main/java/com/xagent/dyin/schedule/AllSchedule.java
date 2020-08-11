package com.xagent.dyin.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AllSchedule
{
    //@Scheduled(cron="0 0/1 * * * ? ")
    public void refreshDbToMemory()
    {}
}
