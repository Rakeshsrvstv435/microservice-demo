package com.microservices.demo.twitter.to.kafka.service.runner.impl;/*
 *
 * Created By rakeshsrivastav On 05/01/25
 *
 */

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.exceptions.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;


@Slf4j
@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;

    private static final Random RANDOM = new Random();

    private static final String[] WORDS = new String[] {
            "Lorem",
            "ipsum",
            "dolor",
            "sit amet",
            "consectetur",
            "adipiscing",
            "elit",
            "Integer",
            "sit",
            "amet",
            "dui",
            "magna",
            "Proin",
            "interdum"
    };

    private static final String twitterAsRawJson = "{\n" +
            "    \"created_at\":\"{0}\",\n" +
            "    \"id\":\"{1}\",\n" +
            "    \"text\": \"{2}\",\n" +
            "    \"user\":{\"id\":\"{3}\"}\n" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData configData,
                                 TwitterKafkaStatusListener twitterListener) {
        this.twitterToKafkaServiceConfigData = configData;
        this.twitterKafkaStatusListener = twitterListener;
    }

    @Override
    public void start() throws TwitterException {
        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        int minTweetLength = twitterToKafkaServiceConfigData.getMockMinTweetLength();
        int maxTweetLength = twitterToKafkaServiceConfigData.getMockMaxTweetLength();
        long sleepTimeMs = twitterToKafkaServiceConfigData.getMockSleepMs();

        log.info("We are starting to mock twitter streams for keywords {}", Arrays.toString(keywords));

        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) throws TwitterException {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while(true) {
                    String formatedTweetAsRawJson = getFormatedTweet(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formatedTweetAsRawJson);
                    twitterKafkaStatusListener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            } catch (TwitterException ex) {
                log.error("error creating twitter status! ", ex);
            }
        });

    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException ex) {
            throw new TwitterToKafkaServiceException("Error while sleeping for waiting for new status to create !!");
        }
    }

    private String getFormatedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
        String[] param = new String[] {
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return formatTweetAsJsonWithParams(param);
    }

    private static String formatTweetAsJsonWithParams(String[] param) {
        String tweet = twitterAsRawJson;

        for (int i = 0; i < param.length; i++) {
            tweet = tweet.replace("{" + i + "}", param[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetlength = RANDOM.nextInt(maxTweetLength - minTweetLength +  1) + minTweetLength;
        return constructRandomTweets(keywords, tweetlength, tweet);
    }

    private static String constructRandomTweets(String[] keywords, int tweetlength, StringBuilder tweet) {
        for (int i = 0; i < tweetlength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetlength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }


}
