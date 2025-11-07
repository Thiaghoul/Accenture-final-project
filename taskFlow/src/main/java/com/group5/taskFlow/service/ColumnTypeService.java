package com.group5.taskFlow.service;

import com.group5.taskFlow.dto.ColumnTypeRequest;
import com.group5.taskFlow.dto.ColumnTypeResponse;
import com.group5.taskFlow.model.ColumnTypeModels;
import com.group5.taskFlow.repository.ColumnTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public List<ColumnTypeResponse> findAll() {
        return columnTypeRepository.findAll().stream().map(this::toColumnTypeResponse).toList();
    }

    public ColumnTypeResponse findById(UUID id) {
        return columnTypeRepository.findById(id).map(this::toColumnTypeResponse).orElseThrow(() -> new EntityNotFoundException("ColumnType not found with id: " + id));
    }

    public ColumnTypeResponse update(UUID id, ColumnTypeRequest columnTypeRequest) {
        ColumnTypeModels existingColumnType = columnTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ColumnType not found with id: " + id));

        existingColumnType.setName(columnTypeRequest.getName());
        existingColumnType.setOrder(columnTypeRequest.getOrder());

        ColumnTypeModels updatedColumnType = columnTypeRepository.save(existingColumnType);
        return toColumnTypeResponse(updatedColumnType);
    }

    public void deleteById(UUID id) {
        if (!columnTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("ColumnType not found with id: " + id);
        }
        columnTypeRepository.deleteById(id);
    }

    private ColumnTypeResponse toColumnTypeResponse(ColumnTypeModels columnTypeModels) {
        ColumnTypeResponse columnTypeResponse = new ColumnTypeResponse();
        columnTypeResponse.setId(columnTypeModels.getId());
        columnTypeResponse.setName(columnTypeModels.getName());
        columnTypeResponse.setOrder(columnTypeModels.getOrder());
        return columnTypeResponse;
    }
}
