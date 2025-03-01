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
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository,
                        MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEvent event) {

        var entity = new OrderEntity();

        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(getOrderItems(event));
        entity.setTotal(getTotal(event));

        orderRepository.save(entity);

    }

    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
        var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);

        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("customerId").is(customerId)),
                Aggregation.group().sum("total").as("total")
        );

        AggregationResults<Document> response = mongoTemplate.aggregate(aggregation, "tb_orders", Document.class);

        Document result = response.getUniqueMappedResult();

        if (result != null && result.get("total") != null) {
            return new BigDecimal(result.get("total").toString());
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens()
                .stream()
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
        return event.itens().stream()
                .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                .toList();
    }
}