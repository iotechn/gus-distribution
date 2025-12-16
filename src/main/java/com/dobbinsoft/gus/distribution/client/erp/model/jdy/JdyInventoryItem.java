package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JdyInventoryItem {
	private String locationNumber;
	private String locationName;
	private BigDecimal qty;
	private String productNumber;
	private String productName;

}
