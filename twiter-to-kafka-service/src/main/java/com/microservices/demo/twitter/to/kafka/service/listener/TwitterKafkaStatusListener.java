package com.microservices.demo.twitter.to.kafka.service.listener;/*
 *
 * Created By rakeshsrivastav On 04/01/25
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Slf4j
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    @Override
    public void onStatus(Status status) {
        log.info("Twitter Status with text {}", status.getText());
    }
}
