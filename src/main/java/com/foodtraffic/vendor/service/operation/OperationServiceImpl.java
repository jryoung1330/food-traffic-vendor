package com.foodtraffic.vendor.service.operation;

import com.foodtraffic.client.UserClient;
import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.util.AppUtil;
import com.foodtraffic.vendor.entity.operation.Operation;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.repository.operation.OperationItemRepository;
import com.foodtraffic.vendor.repository.operation.OperationRepository;
import com.foodtraffic.vendor.service.employee.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OperationRepository operationRepo;

    @Autowired
    private OperationItemRepository operationItemRepo;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserClient userClient;

    @Override
    public OperationDto getOperations(final long vendorId, String searchKey) {
        Optional<Operation> operation;

        if("week".equals(searchKey)) {
            operation = operationRepo.getHoursOfOperation(vendorId);
            operation.ifPresent(value -> Collections.sort(value.getOperationItems()));
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

        return operation.map(value -> modelMapper.map(value, OperationDto.class)).orElse(null);
    }

    @Override
    public OperationDto createWeek(Long vendorId) {
        Optional<Operation> operation = operationRepo.findOneByVendorId(vendorId);
        if(operation.isEmpty()) {
            Operation op = modelMapper.map(createOperation(vendorId), Operation.class);
            List<OperationItem> opItems = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                OperationItem opItem = new OperationItem();
                opItem.setOperationId(op.getId());
                opItem.setDayOfWeek(DayOfWeek.of(i+1).name());
                opItem.setOpenTime("9:00");
                opItem.setCloseTime("17:00");
                opItems.add(opItem);
            }
            opItems = operationItemRepo.saveAll(opItems);
            op.setOperationItems(opItems);
            return modelMapper.map(op, OperationDto.class);
        } else {
            return modelMapper.map(operation.get(), OperationDto.class);
        }
    }

    @Override
    public OperationItemDto updateOperationItem(Long vendorId, Long operationId, Long operationItemId, OperationItem operationItem, String accessToken) {
        UserDto user = AppUtil.getUser(userClient, accessToken);

        if(!employeeService.isUserAnAdmin(vendorId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } else if(operationItemRepo.existsByOperationIdAndId(operationId, operationItemId)) {
            if(operationItem.isClosed()) {
                operationItem.setOpenTime(null);
                operationItem.setCloseTime(null);
            }
            operationItem = operationItemRepo.save(operationItem);
            return modelMapper.map(operationItem, OperationItemDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public OperationDto createOperation(Long vendorId) {
        Operation operation = new Operation();
        operation.setVendorId(vendorId);
        operation = operationRepo.saveAndFlush(operation);
        return modelMapper.map(operation, OperationDto.class);
    }
}
