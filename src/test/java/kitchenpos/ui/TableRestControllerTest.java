package kitchenpos.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.TableService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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