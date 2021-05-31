package com.orderbook.model;

import java.math.BigDecimal;

import com.orderbook.Level2View.Side;

public class Order {

	private Side side;
	private BigDecimal price;
	private long quantity;
	private long orderId;

	public Side getSide() {
		return side;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public long getQuantity() {
		return quantity;
	}

	public long getOrderId() {
		return orderId;
	}

	public Order(Side side, BigDecimal price, long quantity, long orderId) {
		this.side = side;
		this.price = price;
		this.quantity = quantity;
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "Order [side=" + side + ", price=" + price + ", quantity=" + quantity + ", orderId=" + orderId + "]";
	}
}
