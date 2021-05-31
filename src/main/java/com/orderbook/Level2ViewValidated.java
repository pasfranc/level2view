package com.orderbook;

import static java.util.Objects.isNull;

import java.math.BigDecimal;

public class Level2ViewValidated extends Level2ViewDecorator {

	public Level2ViewValidated(Level2View level2View) {
		super(level2View);
	}

	@Override
	public void onNewOrder(Side side, BigDecimal price, long quantity, long orderId) {
		validateSide(side);
		validateQuantity(quantity);
		validateOrderId(orderId);
		validatePrice(price);

		super.onNewOrder(side, price, quantity, orderId);
	}

	@Override
	public void onCancelOrder(long orderId) {
		validateOrderId(orderId);

		super.onCancelOrder(orderId);

	}

	@Override
	public void onReplaceOrder(BigDecimal price, long quantity, long orderId) {
		validateQuantity(quantity);
		validateOrderId(orderId);
		validatePrice(price);

		super.onReplaceOrder(price, quantity, orderId);

	}

	@Override
	public void onTrade(long quantity, long restingOrderId) {
		validateQuantity(quantity);
		validateOrderId(restingOrderId);

		super.onTrade(quantity, restingOrderId);

	}

	@Override
	public long getSizeForPriceLevel(Side side, BigDecimal price) {
		validateSide(side);
		validatePrice(price);

		return super.getSizeForPriceLevel(side, price);
	}

	@Override
	public long getBookDepth(Side side) {
		validateSide(side);

		return super.getBookDepth(side);
	}

	@Override
	public BigDecimal getTopOfBook(Side side) {
		validateSide(side);

		return super.getTopOfBook(side);
	}

	private void validateQuantity(long quantity) {
		if (quantity <= 0)
			throw new IllegalArgumentException("Quantity should be more than zero!");
	}

	private void validateOrderId(long orderId) {
		if (orderId <= 0)
			throw new IllegalArgumentException("OrderId should be more than zero!");
	}

	private void validateSide(Side side) {
		if (isNull(side))
			throw new IllegalArgumentException("Side cannot be null!");
	}

	private void validatePrice(BigDecimal price) {
		if (isNull(price))
			throw new IllegalArgumentException("Price cannot be null!");
		if (price.signum() < 1)
			throw new IllegalArgumentException("Price should be more than zero!");
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
