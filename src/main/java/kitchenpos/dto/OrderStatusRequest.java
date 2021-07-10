package kitchenpos.dto;

import kitchenpos.domain.OrderStatus;

public class OrderStatusRequest {
    private OrderStatus orderStatus;

    public OrderStatusRequest() {
    }

    public OrderStatusRequest(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
