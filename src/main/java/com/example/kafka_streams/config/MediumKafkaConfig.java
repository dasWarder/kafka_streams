package com.example.kafka_streams.config;

import com.example.kafka_streams.map.OrderMapper;
import com.example.kafka_streams.model.Order;
import com.example.kafka_streams.model.StockInfo;
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
@Profile(value = "medium")
public class MediumKafkaConfig {

    private final OrderMapper orderMapper;

    private final ObjectMapper objectMapper;

    @Bean
    public Serde<Order> orderSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Order.class));
    }

    @Bean
    public Serde<StockInfo> stockInfoSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(StockInfo.class));
    }

    @Bean
    public KStream<String, Order> kStream(StreamsBuilder builder) {

        KStream<String, String> stream = builder.stream("orders", Consumed.with(Serdes.String(), Serdes.String()));

        KStream<String, Order> orders = stream.mapValues(this::getOrderFromString)
                .filter((k, v) -> v.getType().equals("book"))
                .filter((k, v) -> v.getPrice() < 1000);
        orders.to("basket", Produced.with(Serdes.String(), orderSerde()));

        KStream<String, StockInfo> stock = stream
                .mapValues(this::getOrderFromString)
                .mapValues(orderMapper::orderToStockInfo);
        stock.to("stock", Produced.with(Serdes.String(), stockInfoSerde()));

        return orders;
    }


    private Order getOrderFromString(String orderString) {

        Order order = null;

        try {
            order = objectMapper.readValue(orderString, Order.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return order;
    }


}
