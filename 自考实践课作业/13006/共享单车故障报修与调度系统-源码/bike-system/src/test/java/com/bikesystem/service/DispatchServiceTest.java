package com.bikesystem.service;

import com.bikesystem.model.DispatchRoute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private DispatchService service;

    @Test
    void 返回有序路径_长度正确() {
        when(jdbc.query(anyString(), any(Object[].class), ArgumentMatchers.<RowMapper<Long>>any()))
                .thenReturn(List.of(1L, 3L, 2L, 5L, 4L));

        List<Long> path = service.calcTspRoute(List.of(1L, 2L, 3L, 4L, 5L));
        assertThat(path).hasSize(5);
        assertThat(path).containsExactly(1L, 3L, 2L, 5L, 4L);
    }

    @Test
    void 空节点返回空路径() {
        List<Long> path = service.calcTspRoute(List.of());
        assertThat(path).isEmpty();
    }

    @Test
    void 生成路线包含路径与成本() {
        when(jdbc.query(anyString(), any(Object[].class), ArgumentMatchers.<RowMapper<Long>>any()))
                .thenReturn(List.of(1L, 2L, 3L));

        DispatchRoute route = service.generateRoute();
        assertThat(route.getTspPath()).hasSize(3);
        assertThat(route.getTotalCost()).isGreaterThan(0);
    }

    @Test
    void 月度故障率计算_无车辆返回零() {
        when(jdbc.queryForObject("SELECT COUNT(*) FROM bike_info WHERE status<>'SCRAPPED'", Integer.class))
                .thenReturn(0);
        double rate = service.calcMonthlyFaultRate(2026, 11);
        assertThat(rate).isEqualTo(0.0);
    }
}
