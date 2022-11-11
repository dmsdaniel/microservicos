package com.example.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.example.orderservice.dto.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderservice.dto.OrderItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItems;
import com.example.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderItems> list = orderRequest.getOrderItensDto().stream().map(this::mapToDto).toList();
        order.setOrderItens(list);

        List<String> skuCodes = order.getOrderItens().stream().map(OrderItems::getSkuCode).toList();

        log.info("Caling inventory servuce");
        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){
            //Faz chamada inventory server, and cria order caso produto
            //tenha em estoque
            InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
            assert inventoryResponses != null;
            boolean allProductsInStock;
            allProductsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
            if(allProductsInStock){
                orderRepository.save(order);
                return "Ordem createad!!!!";
            } else {
                throw new IllegalArgumentException("Produto não encontrado, por favor tente mais tarde!");
            }
        } finally {
            inventoryServiceLookup.end();
        }


    }
    private OrderItems mapToDto(OrderItemsDto ordemItemDto) {
        OrderItems orderItems = new OrderItems();
        orderItems.setPrice(ordemItemDto.getPrice());
        orderItems.setSkuCode(ordemItemDto.getSkuCode());
        orderItems.setQuantity(ordemItemDto.getQuantity());
        return orderItems;
    }
    
}
