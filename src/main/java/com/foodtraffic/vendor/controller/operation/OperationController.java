package com.foodtraffic.vendor.controller.operation;

import com.foodtraffic.model.dto.OperationItemDto;
import com.foodtraffic.vendor.entity.operation.OperationItem;
import com.foodtraffic.vendor.service.operation.OperationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
@RestController
@Api(tags = "Operation")
@RequestMapping("/vendors/{vendorId}")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @GetMapping("/operations")
    public List<OperationItemDto> getOperationsForVendor(@PathVariable("vendorId") Long vendorId,
                                                         @RequestParam("search") String searchKey) {
        return operationService.getOperations(vendorId, searchKey);
    }

    @GetMapping("/events")
    public List<OperationItemDto> getEvents(@PathVariable("vendorId") Long vendorId,
                                            @RequestParam("search") String searchKey,
                                            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return operationService.getOperationItems(vendorId, searchKey, date);
    }

    @PostMapping("/operations")
    public List<OperationItemDto> createHoursOfOperation(@PathVariable("vendorId") Long vendorId) {
        return operationService.createWeek(vendorId);
    }

    @PostMapping("/events")
    public OperationItemDto createEvent(@PathVariable("vendorId") Long vendorId,
                                        @RequestBody OperationItem operationItem,
                                        @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
        return operationService.createEvent(vendorId, operationItem, accessToken);
    }

    @PutMapping("/operations/{operationItemId}")
    public OperationItemDto updateOperationItem(@PathVariable("vendorId") Long vendorId,
                                                @PathVariable("operationItemId") Long operationItemId,
                                                @RequestBody OperationItem operationItem,
                                                @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
        return operationService.updateOperationItem(vendorId, operationItemId, operationItem, accessToken);
    }

    @DeleteMapping("/events/{operationItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable("vendorId") Long vendorId,
                                    @PathVariable("operationItemId") Long operationItemId,
                                    @CookieValue(value = "_gid", defaultValue = "") String accessToken) {
        operationService.deleteEvent(vendorId, operationItemId, accessToken);
    }
}
