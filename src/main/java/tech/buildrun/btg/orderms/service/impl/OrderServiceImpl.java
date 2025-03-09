package tech.buildrun.btg.orderms.service.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.btg.orderms.controller.dto.OrderResponse;
import tech.buildrun.btg.orderms.entity.OrderEntity;
import tech.buildrun.btg.orderms.entity.OrderItem;
import tech.buildrun.btg.orderms.listener.OrderCreatedListener;
import tech.buildrun.btg.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btg.orderms.repository.OrderRepository;
import tech.buildrun.btg.orderms.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                        MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    @Transactional
    public void deletar(Long customerId) {
        orderRepository.deleteAllByCustomerId(customerId);
    }

    @Override
    public void save(OrderCreatedEvent event) {
        var entity = new OrderEntity();

        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(getOrderItems(event));
        entity.setTotal(getTotal(event));

        orderRepository.save(entity);

    }

    @Override
    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
        var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);

        return orders.map(OrderResponse::fromEntity);
    }

    @Override
    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
    }


    @Override
    public BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens()
                .stream()
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public List<OrderItem> getOrderItems(OrderCreatedEvent event) {
            return event.itens().stream()
                    .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                    .toList();
    }
}