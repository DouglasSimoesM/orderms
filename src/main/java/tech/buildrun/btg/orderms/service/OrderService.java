package tech.buildrun.btg.orderms.service;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import tech.buildrun.btg.orderms.controller.dto.OrderResponse;
import tech.buildrun.btg.orderms.entity.OrderEntity;
import tech.buildrun.btg.orderms.entity.OrderItem;
import tech.buildrun.btg.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btg.orderms.repository.OrderRepository;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.math.BigDecimal;
import java.util.List;


@Service
public interface OrderService {

    void deletar(Long customerId);

    void save(OrderCreatedEvent event);

    Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest);

    BigDecimal findTotalOnOrdersByCustomerId(Long customerId);

    BigDecimal getTotal(OrderCreatedEvent event);

    List<OrderItem> getOrderItems(OrderCreatedEvent event);
}