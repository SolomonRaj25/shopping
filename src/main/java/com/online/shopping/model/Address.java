package com.online.shopping.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String addressId;
    private String fullName;
    private String phoneNo;
    private String buildingNo;
    private String StreetAreaName;
    private String city;
    private String state;
    private String pinCode;
}
