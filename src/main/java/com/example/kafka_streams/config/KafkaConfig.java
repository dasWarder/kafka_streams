package com.example.kafka_streams.config;

import com.example.kafka_streams.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile(value = "simple")
public class KafkaConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public Serde<User> userSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(User.class));
    }

    @Bean
    public KStream<String, User> kStream(StreamsBuilder builder) {

        log.info("Creating streams for input and output data");

        KStream<String, String> stream = builder
                .stream("src1", Consumed.with(Serdes.String(), Serdes.String()));

        KStream<String, User> usersStream = stream
                .mapValues(this::getUserFromString)
                .filter((key, value) -> value.getBalance() <= 0);
        usersStream.to("out", Produced.with(Serdes.String(), userSerde()));

        return usersStream;
    }

    private User getUserFromString(String userString) {

        User user = null;

        try {
            user = objectMapper.readValue(userString, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return user;
    }




}
