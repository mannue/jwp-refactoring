package kitchenpos.order.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.OrderTables;
import kitchenpos.tablegroup.domain.TableGroup;

class OrderTablesTest {

    @Test
    void given_CompletedOrder_when_Ungroup_then_UngroupTable() {
        // given
        final TableGroup tableGroup = mock(TableGroup.class);
        final OrderTable orderTable1 = mock(OrderTable.class);
        final OrderTable orderTable2 = mock(OrderTable.class);
        final OrderTables orderTables = new OrderTables(Arrays.asList(orderTable1, orderTable2));
        final Order order1 = new Order(orderTable1.getId(), OrderStatus.COMPLETION);
        final Order order2 = new Order(orderTable2.getId(), OrderStatus.COMPLETION);
        final Orders orders = new Orders(Arrays.asList(order1, order2));
        given(tableGroup.getId()).willReturn(1L);

        // when
        orderTables.ungroup(orders);

        // then
        verify(orderTable1).ungroup();
        verify(orderTable2).ungroup();
    }

    @Test
    void given_NotCompletedOrder_when_Ungroup_then_ThrowException() {
        // given
        final OrderTable orderTable1 = mock(OrderTable.class);
        final OrderTable orderTable2 = mock(OrderTable.class);
        final OrderTables orderTables = new OrderTables(Arrays.asList(orderTable1, orderTable2));
        final Order order1 = new Order(orderTable1.getId(), OrderStatus.COOKING);
        final Order order2 = new Order(orderTable2.getId(), OrderStatus.COMPLETION);
        final Orders orders = new Orders(Arrays.asList(order1, order2));
        given(orderTable1.getTableGroupId()).willReturn(1L);
        given(orderTable2.getTableGroupId()).willReturn(2L);

        // when
        final Throwable throwable = catchThrowable(() -> orderTables.ungroup(orders));

        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.changeEmpty(true);

        // when
        final Throwable throwable = catchThrowable(() -> orderTable.changeNumberOfGuests(1));

        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }
}
