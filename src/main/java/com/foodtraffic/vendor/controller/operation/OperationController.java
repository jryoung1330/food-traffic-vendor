package com.foodtraffic.vendor.controller.operation;

import com.foodtraffic.model.dto.OperationDto;
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
}
