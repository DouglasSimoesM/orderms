package tech.buildrun.btg.orderms.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.btg.orderms.controller.dto.ApiResponse;
import tech.buildrun.btg.orderms.controller.dto.OrderResponse;
import tech.buildrun.btg.orderms.controller.dto.PaginationResponse;


import java.util.Map;

@RestController
@RequestMapping("/customers")
public class OrderController {

    private final tech.buildrun.btg.orderms.service.impl.OrderServiceImpl orderService;

    public OrderController(tech.buildrun.btg.orderms.service.impl.OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(@PathVariable("customerId") Long customerId,
                                                                 @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){

        var pageResponse = orderService.findAllByCustomerId(customerId, PageRequest.of(page, pageSize));
        var totalOnOrders = orderService.findTotalOnOrdersByCustomerId(customerId);

        return ResponseEntity.ok(new ApiResponse<>(
                Map.of("totalOnOrders", totalOnOrders),
                pageResponse.getContent(),
                PaginationResponse.fromPage(pageResponse)
        ));
    }

    @DeleteMapping("/{codigoPedido}")
    public ResponseEntity<Void> delete(@PathVariable Long codigoPedido){
        orderService.deletar(codigoPedido);
        return ResponseEntity.ok().build();
    }
}
