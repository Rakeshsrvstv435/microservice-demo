package com.microservices.demo.twitter.to.kafka.service.runner;/*
 *
 * Created By rakeshsrivastav On 04/01/25
 *
 */

import twitter4j.TwitterException;

public interface StreamRunner {

    void start() throws TwitterException;
}
