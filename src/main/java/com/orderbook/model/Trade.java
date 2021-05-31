package com.orderbook.model;

import java.math.BigDecimal;

public class Trade {

	private BigDecimal price;
	private long quantity;
	private long orderId;

	public BigDecimal getPrice() {
		return price;
	}

	public long getQuantity() {
		return quantity;
	}

	public long getOrderId() {
		return orderId;
	}

	public Trade(BigDecimal price, long quantity, long orderId) {
		this.price = price;
		this.quantity = quantity;
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "Trade [price=" + price + ", quantity=" + quantity + ", orderId=" + orderId + "]";
	}

}
