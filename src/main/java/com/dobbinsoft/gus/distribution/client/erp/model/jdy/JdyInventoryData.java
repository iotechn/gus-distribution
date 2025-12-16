package com.dobbinsoft.gus.distribution.client.erp.model.jdy;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JdyInventoryData {
	private int totalsize;
	private String msg;
	private int code;
	private int records;
	private int totalPages;
	private int page;
	private List<JdyInventoryItem> items;

}