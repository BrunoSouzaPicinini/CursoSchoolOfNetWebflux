package br.com.bspicinini.webflux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
public class WebfluxApplication {

    @Value("${spring.datasource.maximum-pool-size}")
    private int connectionPools;

    public static void main(String[] args) {
        SpringApplication.run(WebfluxApplication.class, args);
    }

    @Bean
    public Scheduler jdbcScheduler(){
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPools));
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplate(platformTransactionManager);
    }
}
