package com.pradeep.order_service.service;

import com.pradeep.order_service.client.InventoryClient;
import com.pradeep.order_service.dto.OrderRequest;
import com.pradeep.order_service.exception.ProductNotInStockException;
import com.pradeep.order_service.model.Order;
import com.pradeep.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    @Value("${spring.kafka.template.default-topic}")
    private String kafkaTopic;

    public void placeOrder(OrderRequest orderRequest) {
        var isInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
        } else {
            throw new ProductNotInStockException("Product with skuCode - " + orderRequest.skuCode() + " is not in stock");
        }
    }
}
