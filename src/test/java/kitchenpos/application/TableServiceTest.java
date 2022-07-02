package kitchenpos.application;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    private TableService tableService;

    @BeforeEach
    void setUp() {
        this.tableService = new TableService(orderDao, orderTableDao);
    }

    /**
     * When : 주문 테이블 생성 요청 시
     * Then : 테이블 그룹은 null 이고 id 는 할당 된다.
     */
    @DisplayName("생성 테스트")
    @Test
    void createTest() {
        OrderTable orderTable = new OrderTable();
        orderTable.setTableGroupId(1L);
        orderTable.setId(1L);
        when(orderTableDao.save(any())).thenReturn(orderTable);

        OrderTable savedOrderTable = tableService.create(orderTable);

        assertThat(savedOrderTable.getId()).isEqualTo(orderTable.getId());
        assertThat(savedOrderTable.getTableGroupId()).isNull();

        verify(orderTableDao, times(1)).save(any());
    }
}