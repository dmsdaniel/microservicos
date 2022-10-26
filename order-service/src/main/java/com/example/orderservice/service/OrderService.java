package com.example.orderservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderservice.dto.OrderItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItems;
import com.example.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    final OrderRepository orderRepository;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderItems> list = orderRequest.getOrderItensDto().stream().map(this::mapToDto).toList();
        order.setOrderItens(list);
        orderRepository.save(order);
    }
    private OrderItems mapToDto(OrderItemsDto ordemItemDto) {
        OrderItems orderItems = new OrderItems();
        orderItems.setPrice(ordemItemDto.getPrice());
        orderItems.setSkuCode(ordemItemDto.getSkuCode());
        orderItems.setQuantity(ordemItemDto.getQuantity());
        return orderItems;
    }
    
}
