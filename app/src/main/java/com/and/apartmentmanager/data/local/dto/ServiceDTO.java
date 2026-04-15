package com.and.apartmentmanager.data.local.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceDTO {
    private String name;
    private String pricingType;
    private String description;
}
