package com.example.kafka_streams.map;

import com.example.kafka_streams.model.Order;
import com.example.kafka_streams.model.StockInfo;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public StockInfo orderToStockInfo(Order order) {

        StockInfo info = StockInfo.builder()
                .id(order.getId())
                .name(order.getName())
                .quantity(order.getQuantity())
                .build();

        return info;
    }
}
