package com.dobbinsoft.gus.distribution.client.gus.location.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(description = "Location information data transfer object")
public class LocationVO {
    @Schema(description = "Unique identifier of the location")
    private String id;

    @Schema(description = "Name of the location")
    private String name;

    @Schema(description = "Unique code for the location")
    private String code;

    @Schema(description = "Type of the location")
    private Type type;

    @Schema(description = "Current status of the location")
    private Status status;

    @Schema(description = "Additional remarks or notes about the location")
    private String remark;

    @Schema(description = "Latitude coordinate of the location")
    private BigDecimal latitude;

    @Schema(description = "Longitude coordinate of the location")
    private BigDecimal longitude;

    @Schema(description = "Country code where the location is situated")
    private String countryCode;

    @Schema(description = "Province/State where the location is situated")
    private String province;

    @Schema(description = "City where the location is situated")
    private String city;

    @Schema(description = "District/Area where the location is situated")
    private String district;

    @Schema(description = "Detailed street address of the location")
    private String address;

    @Schema(description = "Contact email address for the location")
    private String email;

    @Schema(description = "Postal/ZIP code of the location")
    private String postalCode;

    @Schema(description = "List of image URLs associated with the location")
    private List<String> images;

    @Schema(description = "List of features or amenities available at the location")
    private List<String> features;

    @Schema(description = "List of virtual locations associated with this physical location")
    private List<VirtualLocationVO> virtualLocations;

    @Schema(description = "Region code for the location")
    private String regionCode;

    @Schema(description = "Email address of the region manager")
    private String regionManagerEmail;

    @Schema(description = "Email address of the location manager")
    private String locationManagerEmail;

    @Schema(description = "WeCom department ID for association")
    private String wecomDeptId;

    @Schema(description = "Mobile phone number of the location")
    private String mobile;

    @Schema(description = "Telephone number of the location")
    private String telephone;

    @Getter
    @Setter
    @Schema(description = "Virtual location information")
    public static class VirtualLocationVO {
        @Schema(description = "Unique identifier of the virtual location")
        private String id;

        @Schema(description = "Name of the virtual location")
        private String name;

        @Schema(description = "location code")
        private String locationCode;
    }

    public enum Type {
        STORE,
        WAREHOUSE,
        /**
         * The definition of virtual location:
         * Virtual location is a combine of real location (store & warehouse). The product stocks in virtual location equals the sum of union locations
         */
        VIRTUAL,
    }

    public enum Status {
        ENABLED,
        DISABLED,
    }
}
