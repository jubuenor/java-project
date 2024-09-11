package com.globant.project.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.globant.project.domain.dto.OrderDTO;
import com.globant.project.domain.entities.ClientEntity;
import com.globant.project.domain.entities.OrderEntity;
import com.globant.project.domain.entities.ProductEntity;
import com.globant.project.error.ErrorConstants;
import com.globant.project.error.exceptions.NotFoundException;
import com.globant.project.mappers.OrderMapper;
import com.globant.project.repositories.OrderRepository;
import com.globant.project.services.ClientService;
import com.globant.project.services.OrderService;
import com.globant.project.services.ProductService;
import com.globant.project.utils.CalculationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OrderServiceImpl
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final BigDecimal tax = new BigDecimal("0.19");

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDto) {

        OrderEntity orderEntity = orderMapper.DtoToEntity(orderDto);
        ClientEntity client = clientService.getClientEntity(orderEntity.getClientDocument().getDocument());
        ProductEntity product = productService.getProductEntity(orderEntity.getProductUuid().getUuid());

        orderEntity.setClientDocument(client);
        orderEntity.setProductUuid(product);
        orderEntity.setSubTotal(CalculationUtils.calculateSubTotal(product.getPrice(), orderEntity.getQuantity()));
        orderEntity.setTax(CalculationUtils.calculateTax(orderEntity.getSubTotal(), tax));
        orderEntity
                .setGrandTotal(CalculationUtils.calculateGrandTotal(orderEntity.getSubTotal(), orderEntity.getTax()));
        orderEntity.setDelivered(false);

        OrderEntity orderSaved = orderRepository.save(orderEntity);
        log.info("Order created with uuid: {}", orderSaved.getUuid());
        return orderMapper.EntityToDto(orderSaved);

    }

    @Transactional(readOnly = true)
    @Override
    public OrderDTO getOrder(String uuid) {
        UUID orderUuid = UUID.fromString(uuid);
        return orderRepository.findById(orderUuid).map(orderMapper::EntityToDto)
                .orElseThrow(() -> new NotFoundException(ErrorConstants.ORDER_NOT_FOUND + orderUuid));
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> getOrders() {
        return orderRepository.findAll().stream().map(orderMapper::EntityToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean orderExists(String uuid) {
        return orderRepository.existsById(UUID.fromString(uuid));
    }

    @Transactional
    @Override
    public void updateOrder(String uuid, OrderDTO orderEntity) {
        if (!orderExists(uuid)) {
            throw new NotFoundException(ErrorConstants.ORDER_NOT_FOUND + uuid);
        }
        orderRepository.save(orderMapper.DtoToEntity(orderEntity));
        log.info("Order updated with uuid: {}", uuid);
    }

    @Transactional
    @Override
    public void deleteOrder(String uuid) {
        if (!orderExists(uuid.toString())) {
            throw new NotFoundException(ErrorConstants.ORDER_NOT_FOUND + uuid);
        }
        orderRepository.deleteById(UUID.fromString(uuid));
    }

}