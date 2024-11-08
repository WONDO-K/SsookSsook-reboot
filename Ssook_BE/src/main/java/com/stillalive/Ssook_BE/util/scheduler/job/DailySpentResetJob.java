package com.stillalive.Ssook_BE.util.scheduler.job;

import com.stillalive.Ssook_BE.domain.Balance;
import com.stillalive.Ssook_BE.pay.repository.BalanceRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailySpentResetJob implements Job {

    @Autowired
    private BalanceRepository balanceRepository;

    private static final Logger log = LoggerFactory.getLogger(DailySpentResetJob.class);


    @Override
    public void execute(JobExecutionContext context) {
        log.info("일일 지출 금액 초기화 시작");

        List<Balance> balances = balanceRepository.findAll();
        balances.forEach(balance -> balance.setDailySpentAmount(0));

        balanceRepository.saveAll(balances);
        log.info("일일 지출 금액 초기화 완료 - 총 {}개의 잔액이 초기화되었습니다.", balances.size());
    }
}