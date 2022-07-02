package kitchenpos.application;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    /**
     * When : 저장된 주문 테이블 id 와 변경할 주문 테이블 정보를 입력하면
     * Then : 정상적으로 변경된다.
     */
    @DisplayName("테이블 상태 변경하기")
    @Test
    void changeEmptyTest() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(false);
        when(orderTableDao.findById(1L)).thenReturn(Optional.of(orderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(1L, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))).thenReturn(false);
        when(orderTableDao.save(any())).thenReturn(orderTable);

        OrderTable requestTable = new OrderTable();
        requestTable.setEmpty(true);
        tableService.changeEmpty(1L, requestTable);
        assertThat(orderTable.isEmpty()).isTrue();

        verify(orderTableDao, times(1)).findById(1L);
        verify(orderDao, times(1)).existsByOrderTableIdAndOrderStatusIn(1L, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()));
    }

    @DisplayName("주문 테이블이 저장되어 있지 않는 상태에서 상태 변경하기")
    @Test
    void changeEmptyTestWhenNoSavedTable() {
        when(orderTableDao.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableService.changeEmpty(1L, new OrderTable()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블이 저장되어 있지만 결재완료 상태가 아닌 경우에 상태 변경 하기")
    @Test
    void changeEmptyTestWhenOrderStateIsNoComplete() {
        when(orderTableDao.findById(any())).thenReturn(Optional.of(new OrderTable()));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), anyList())).thenReturn(true);

        assertThatThrownBy(() -> tableService.changeEmpty(1L, new OrderTable()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 테이블이 저장되어 있지만 단체 그룹이 설정되어 있는경우에 상태 변경하기")
    @Test
    void changeEmptyTestWhenTableHasTableGroup() {
        OrderTable savedTable = new OrderTable();
        savedTable.setTableGroupId(1L);
        when(orderTableDao.findById(any())).thenReturn(Optional.of(savedTable));

        assertThatThrownBy(() -> tableService.changeEmpty(1L, new OrderTable()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("고객수 변경 하기 ")
    @Test
    void changeNumberOfGuestsTest() {
        OrderTable requestTable = new OrderTable();
        requestTable.setNumberOfGuests(2);

        OrderTable savedTable = new OrderTable();
        savedTable.setEmpty(false);
        when(orderTableDao.findById(1L)).thenReturn(Optional.of(savedTable));
        when(orderTableDao.save(any())).thenReturn(savedTable);

        OrderTable orderTable = tableService.changeNumberOfGuests(1L, requestTable);
        assertThat(savedTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @DisplayName("테이블이 저장되어 있지 않고 고객수 변경하는 경우")
    @Test
    void changeNumberOfGuestsWhenNoSavedTable() {
        when(orderTableDao.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, new OrderTable()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("입력 값이 음수 인 경우에 고객수 변경하는 경우")
    @Test
    void changeNumberOfGuestsWhenInputIsInvalid() {
        OrderTable requestTable = new OrderTable();
        requestTable.setNumberOfGuests(-1);

        assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, requestTable))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("저장된 테이블이 비어 있는 상태에서 고객수 변경하는 경우")
    @Test
    void changeNumberOfGuestsWhenSavedTableIsEmpty() {
        OrderTable savedTable = new OrderTable();
        savedTable.setEmpty(true);
        when(orderTableDao.findById(1L)).thenReturn(Optional.of(savedTable));

        assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, new OrderTable()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}