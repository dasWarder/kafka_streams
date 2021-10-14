package com.example.kafka_streams.config;

import com.example.kafka_streams.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@Slf4j
@EnableKafka
@Configuration
@EnableKafkaStreams
public class KafkaConfig {

    private final static String BOOTSTRAP_SERVER = "localhost:9092";

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration streamsConfiguration() {

        log.info("Populate properties for streams configuration");

        Map<String, Object> props = new HashMap<>();

        props.put(APPLICATION_ID_CONFIG, "id");
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public Serde<User> userSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(User.class));
    }

    @Bean
    public KStream<String, User> kStream(StreamsBuilder builder) {

        log.info("Creating streams for input and output data");

        KStream<String, String> stream = builder
                .stream("src1", Consumed.with(Serdes.String(), Serdes.String()));
        log.info("Src stream: {}", stream.toString());

        KStream<String, User> usersStream = stream
                .mapValues(this::getUserFromString)
                .filter((key, value) -> value.getBalance() <= 0);
        usersStream.to("out", Produced.with(Serdes.String(), userSerde()));

        return usersStream;
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    private User getUserFromString(String userString) {
        User user = null;

        try {
            user = objectMapper().readValue(userString, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return user;
    }


}
