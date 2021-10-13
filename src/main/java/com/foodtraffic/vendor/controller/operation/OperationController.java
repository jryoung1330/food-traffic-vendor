package com.foodtraffic.vendor.controller.operation;

import com.foodtraffic.model.dto.OperationDto;
import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.service.operation.OperationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
@RestController
@Api(tags = "Operation")
@RequestMapping("/vendors/{vendorId}/operations")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @GetMapping
    public OperationDto getOperationsForVendor(@PathVariable("vendorId") Long vendorId,
                                               @RequestParam("search") String searchKey) {
        return operationService.getOperations(vendorId, searchKey);
    }

    @PostMapping
    public OperationDto createHoursOfOperation(@PathVariable("vendorId") Long vendorId) {
        return operationService.createWeek(vendorId);
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
