package com.example.kafka_streams.map;

import com.example.kafka_streams.model.Order;
import com.example.kafka_streams.model.StockInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ObjectMapper objectMapper;

    public StockInfo orderToStockInfo(Order order) {

        StockInfo info = StockInfo.builder()
                .id(order.getId())
                .name(order.getName())
                .quantity(order.getQuantity())
                .build();

        return info;
    }

    public <T> String objectToString(T object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
