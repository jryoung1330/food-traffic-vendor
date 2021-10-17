package com.foodtraffic.vendor.controller.operation;

import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.service.operation.OperationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
@RestController
@Api(tags = "Operation")
@RequestMapping("/vendors/{vendorId}/operations")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @GetMapping
    public List<OperationItemDto> getOperationsForVendor(@PathVariable("vendorId") Long vendorId,
                                                         @RequestParam("search") String searchKey) {
        return operationService.getOperations(vendorId, searchKey);
    }

    @PostMapping
    public OperationDto createHoursOfOperation(@PathVariable("vendorId") Long vendorId) {
        return operationService.createWeek(vendorId);
    }

    @GetMapping("/{operationId}/operation-items")
    public List<OperationItemDto> getEvents(@PathVariable("vendorId") Long vendorId,
                                            @PathVariable("operationId") Long operationId,
                                            @RequestParam("search") String searchKey,
                                            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return operationService.getOperationItems(vendorId, operationId, searchKey, date);
    }

    @PostMapping("/{operationId}/operation-items")
    public OperationItemDto createEvent(@PathVariable("vendorId") Long vendorId,
                                    @PathVariable("operationId") Long operationId,
                                    @RequestBody OperationItem operationItem,
                                    @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
        return operationService.createEvent(vendorId, operationId, operationItem, accessToken);
    }

    @PutMapping("/{operationId}/operation-items/{operationItemId}")
    public OperationItemDto updateOperationItem(@PathVariable("vendorId") Long vendorId,
                                                @PathVariable("operationId") Long operationId,
                                                @PathVariable("operationItemId") Long operationItemId,
                                                @RequestBody OperationItem operationItem,
                                                @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
        return operationService.updateOperationItem(vendorId, operationId, operationItemId, operationItem, accessToken);
    }
}
