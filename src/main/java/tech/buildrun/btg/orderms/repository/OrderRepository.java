package tech.buildrun.btg.orderms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.buildrun.btg.orderms.entity.OrderEntity;

@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, Long> {

    void deleteAllByCustomerId(Long customerId);
    Page<OrderEntity> findAllByCustomerId(Long customerId, PageRequest pageRequest);
}
