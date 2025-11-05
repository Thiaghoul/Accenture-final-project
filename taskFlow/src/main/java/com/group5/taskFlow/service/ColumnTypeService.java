package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.ColumnTypeRequest;
import com.group5.taskFlow.dto.ColumnTypeResponse;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.repository.ColumnTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ColumnTypeService {

    private final ColumnTypeRepository columnTypeRepository;

    public ColumnTypeService(ColumnTypeRepository columnTypeRepository) {
        this.columnTypeRepository = columnTypeRepository;
    }

    public ColumnTypeResponse save(ColumnTypeRequest columnTypeRequest) {
        ColumnTypeModels columnTypeModels = new ColumnTypeModels();
        columnTypeModels.setName(columnTypeRequest.getName());
        columnTypeModels.setOrder(columnTypeRequest.getOrder());

        ColumnTypeModels savedColumnType = columnTypeRepository.save(columnTypeModels);

        return toColumnTypeResponse(savedColumnType);
    }

    private ColumnTypeResponse toColumnTypeResponse(ColumnTypeModels columnTypeModels) {
        ColumnTypeResponse columnTypeResponse = new ColumnTypeResponse();
        columnTypeResponse.setId(columnTypeModels.getId());
        columnTypeResponse.setName(columnTypeModels.getName());
        columnTypeResponse.setOrder(columnTypeModels.getOrder());
        return columnTypeResponse;
    }
}
