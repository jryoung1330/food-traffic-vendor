package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.vendor.entity.operation.Operation;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.repository.operation.OperationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OperationRepository operationRepo;

    @Override
    public OperationDto getOperations(final long vendorId, String searchKey) {
        Optional<Operation> operation = null;

        if("week".equals(searchKey)) {
            operation = operationRepo.getHoursOfOperation(vendorId);
        } else if("3-day".equals(searchKey)) {
            operation = operationRepo.getHoursOfOperation(vendorId);

            if(operation.isPresent()) {
                Operation op = operation.get();
                List<OperationItem> opItems = new ArrayList<>();

                for(int i=0; i<3; i++) {
                    int day = LocalDateTime.now().plusDays(i).getDayOfWeek().getValue();
                    for(OperationItem item : operation.get().getOperationItems()) {
                        if(DayOfWeek.valueOf(item.getDayOfWeek()).getValue() == day) {
                            opItems.add(item);
                        }
                    }
                }
                op.setOperationItems(opItems);
            }
        } else {
            operation = operationRepo.findOneByVendorId(vendorId);
        }

        if(operation.isPresent()) {
            return modelMapper.map(operation.get(), OperationDto.class);
        } else {
           return null;
        }
    }
}
