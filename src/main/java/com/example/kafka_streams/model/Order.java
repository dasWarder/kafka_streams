package com.example.kafka_streams.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("quantity")
    private Integer quantity;
}
