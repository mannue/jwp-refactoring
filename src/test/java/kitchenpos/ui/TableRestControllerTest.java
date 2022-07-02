package kitchenpos.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.TableService;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
class TableRestControllerTest {
    private static final int ZERO = 0;

    @PostMapping("/api/tables")
    public ResponseEntity<OrderTable> create(@RequestBody final OrderTable orderTable) {
        final OrderTable created = tableService.create(orderTable);
        final URI uri = URI.create("/api/tables/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TableService tableService;

    /**
     * When : 주문 테이블 요청시
     * Then :  방문한 손님 수는 0 이며 빈테이블 상태 이다.
     */
    @DisplayName("주문 테이블 생성")
    @Test
    void createTableTest() throws Exception {
        when(tableService.create(any())).thenReturn(new OrderTable());
        // When
        final MockHttpServletResponse 생성_요청_결과 = 주문_테이블_생성_요청(new OrderTable());

        // Then
        OrderTable table = 테이블_생성(생성_요청_결과);
        방문한_손님_수(table, ZERO);
    }

    /**
     * Given : 저장된 주문 테이블이 있고
     * When : 저장된 주문 테이블 id 와 업데이트할 주문 테이블을 입력시
     * Then : 정상적으로 변경된다.
     */
    @DisplayName("빈테이블로 변경하기")
    @Test
    void changeEmptyTableTest() throws Exception {
        // Given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        when(tableService.create(any())).thenReturn(orderTable);
        final MockHttpServletResponse 생성_요청_결과 = 주문_테이블_생성_요청(new OrderTable());
        OrderTable table = 테이블_생성(생성_요청_결과);

        // When
        OrderTable changeTable = new OrderTable();
        changeTable.setId(table.getId());
        changeTable.setEmpty(true);
        when(tableService.changeEmpty(eq(table.getId()), any())).thenReturn(changeTable);
        final MockHttpServletResponse 빈_테이블_로_상태_변경_결과 = 빈_테이블_로_상태_변경(table.getId(), new OrderTable());

        // Then
        빈_테이블_로_변경_완료(빈_테이블_로_상태_변경_결과);
    }

    @PutMapping("/api/tables/{orderTableId}/number-of-guests")
    public ResponseEntity<OrderTable> changeNumberOfGuests(
            @PathVariable final Long orderTableId,
            @RequestBody final OrderTable orderTable
    ) {
        return ResponseEntity.ok()
                .body(tableService.changeNumberOfGuests(orderTableId, orderTable))
                ;
    }

    /**
     *  When : 인원수를 변경하면
     *  Then : 저장적으로 변경된다.
     */
    @DisplayName("인원 변경 테스트")
    @Test
    void changeNumberOfGuestsTest() throws Exception {
        // Given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        when(tableService.create(any())).thenReturn(orderTable);
        final MockHttpServletResponse 생성_요청_결과 = 주문_테이블_생성_요청(new OrderTable());
        OrderTable table = 테이블_생성(생성_요청_결과);

        // When
        OrderTable changeTable = new OrderTable();
        changeTable.setId(table.getId());
        changeTable.setNumberOfGuests(4);
        when(tableService.changeNumberOfGuests(eq(table.getId()),any())).thenReturn(changeTable);
        final MockHttpServletResponse 인원_수_변경_결과 = 테이블_인원_변경(orderTable.getId(), changeTable);

        인원_변경_완료(인원_수_변경_결과,4);
    }

    private void 인원_변경_완료(MockHttpServletResponse response, int expectedResult) throws UnsupportedEncodingException, JsonProcessingException {
        assertThat(HttpStatus.valueOf(response.getStatus())).isEqualTo(HttpStatus.OK);
        OrderTable orderTable = objectMapper.readValue(response.getContentAsString(), OrderTable.class);
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(expectedResult);
    }

    private MockHttpServletResponse 테이블_인원_변경(Long id, OrderTable changeTable) throws Exception {
        final String body = objectMapper.writeValueAsString(changeTable);
        MvcResult mvcResult = this.mockMvc.perform(put(String.format("/api/tables/%d/number-of-guests",id))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andReturn();
        return  mvcResult.getResponse();
    }

    private void 빈_테이블_로_변경_완료(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        assertThat(HttpStatus.valueOf(response.getStatus())).isEqualTo(HttpStatus.OK);
        OrderTable orderTable = objectMapper.readValue(response.getContentAsString(), OrderTable.class);
        assertThat(orderTable.isEmpty()).isTrue();
    }

    private MockHttpServletResponse 빈_테이블_로_상태_변경(Long id, OrderTable orderTable) throws Exception {
        final String body = objectMapper.writeValueAsString(orderTable);
        MvcResult mvcResult = this.mockMvc.perform(put(String.format("/api/tables/%d/empty",id))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andReturn();
        return  mvcResult.getResponse();
    }

    private void 방문한_손님_수(OrderTable table, int expectedResult)  {
        assertThat(table.getNumberOfGuests()).isEqualTo(expectedResult);
    }

    private OrderTable  테이블_생성(final MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        assertThat(HttpStatus.valueOf(response.getStatus())).isEqualTo(HttpStatus.CREATED);
        return objectMapper.readValue(response.getContentAsString(), OrderTable.class);
    }

    public MockHttpServletResponse  주문_테이블_생성_요청(final OrderTable orderTable) throws Exception {
        final String body = objectMapper.writeValueAsString(orderTable);
        MvcResult mvcResult = this.mockMvc.perform(post("/api/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andReturn();
        return  mvcResult.getResponse();
    }

}