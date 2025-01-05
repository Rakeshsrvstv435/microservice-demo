package com.microservices.demo.twitter.to.kafka.service;/*
 *
 * Created By rakeshsrivastav On 04/01/25
 *
 */

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
//@Scope("request") //should be used for controller level where we need to get a new instance for each request
public class TwitterToKafkaServiceApplication
//implements ApplicationListener
    implements CommandLineRunner {

//    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final StreamRunner streamRunner;

    public TwitterToKafkaServiceApplication(TwitterToKafkaServiceConfigData configData, StreamRunner streamRunner) {
        this.twitterToKafkaServiceConfigData = configData;
        this.streamRunner = streamRunner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

   /* @PostConstruct
    public void init() {

    }*/

/*    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }*/

    @Override
    public void run(String... args) throws Exception {
        log.info("App Starts ....... ");
        log.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {})));
        log.info(twitterToKafkaServiceConfigData.getWelcomeMessage());
        streamRunner.start();
    }
}
