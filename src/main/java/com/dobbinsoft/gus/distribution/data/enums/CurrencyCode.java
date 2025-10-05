package com.dobbinsoft.gus.distribution.data.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Currency codes for major world currencies
 */
@Getter
@Schema(description = "Currency codes for major world currencies")
public enum CurrencyCode {

    @Schema(description = "US Dollar")
    USD("$"),

    @Schema(description = "Euro")
    EUR("€"),

    @Schema(description = "Japanese Yen")
    JPY("¥"),

    @Schema(description = "British Pound Sterling")
    GBP("£"),

    @Schema(description = "Chinese Yuan")
    CNY("¥"),

    @Schema(description = "Canadian Dollar")
    CAD("C$"),

    @Schema(description = "Swiss Franc")
    CHF("CHF"),

    @Schema(description = "Australian Dollar")
    AUD("A$"),

    @Schema(description = "Hong Kong Dollar")
    HKD("HK$"),

    @Schema(description = "Singapore Dollar")
    SGD("S$");

    private final String symbol;

    CurrencyCode(String symbol) {
        this.symbol = symbol;
    }

}
