package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.ColumnTypeRequest;
import com.group5.taskFlow.dto.ColumnTypeResponse;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.repository.ColumnTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ColumnTypeServiceTest {

    @Mock
    private ColumnTypeRepository columnTypeRepository;

    @InjectMocks
    private ColumnTypeService columnTypeService;

    private ColumnTypeModels columnTypeModels;
    private ColumnTypeRequest columnTypeRequest;
    private ColumnTypeResponse columnTypeResponse;

    @BeforeEach
    void setUp() {
        columnTypeModels = new ColumnTypeModels();
        columnTypeModels.setId(UUID.randomUUID());
        columnTypeModels.setName("To Do");
        columnTypeModels.setOrder(1);

        columnTypeRequest = new ColumnTypeRequest();
        columnTypeRequest.setName("To Do");
        columnTypeRequest.setOrder(1);

        columnTypeResponse = new ColumnTypeResponse();
        columnTypeResponse.setId(columnTypeModels.getId());
        columnTypeResponse.setName("To Do");
        columnTypeResponse.setOrder(1);
    }

    @Test
    public void save_shouldReturnColumnTypeResponse() {
        when(columnTypeRepository.save(any(ColumnTypeModels.class))).thenReturn(columnTypeModels);

        ColumnTypeResponse result = columnTypeService.save(columnTypeRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(columnTypeRequest.getName());
        assertThat(result.getOrder()).isEqualTo(columnTypeRequest.getOrder());
    }
}
