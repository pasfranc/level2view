package com.orderbook;

import java.math.BigDecimal;

public class Level2ViewDecorator implements Level2View {

	private Level2View level2View;

	public Level2ViewDecorator(Level2View level2View) {
		this.level2View = level2View;
	}

	@Override
	public void onNewOrder(Side side, BigDecimal price, long quantity, long orderId) {
		this.level2View.onNewOrder(side, price, quantity, orderId);
	}

	@Override
	public void onCancelOrder(long orderId) {
		this.level2View.onCancelOrder(orderId);

	}

	@Override
	public void onReplaceOrder(BigDecimal price, long quantity, long orderId) {
		this.level2View.onReplaceOrder(price, quantity, orderId);

	}

	@Override
	public void onTrade(long quantity, long restingOrderId) {
		this.level2View.onTrade(quantity, restingOrderId);

	}

	@Override
	public long getSizeForPriceLevel(Side side, BigDecimal price) {
		return this.level2View.getSizeForPriceLevel(side, price);
	}

	@Override
	public long getBookDepth(Side side) {
		return this.level2View.getBookDepth(side);
	}

	@Override
	public BigDecimal getTopOfBook(Side side) {
		return this.level2View.getTopOfBook(side);
	}

	@Override
	public String toString() {
		return this.level2View.toString();
	}

}
