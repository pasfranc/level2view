package com.orderbook;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.orderbook.model.Order;
import com.orderbook.model.Trade;

public class Level2ViewFredrikService implements Level2View {

	private TreeMap<BigDecimal, List<Order>> askOrderMapByPrice = new TreeMap<>();
	private TreeMap<BigDecimal, List<Order>> bidOrderMapByPrice = new TreeMap<>();
	private HashMap<Long, Order> orders = new HashMap<>();

	private List<Trade> historyTrades = new ArrayList<>();

	private boolean newOrderIfStockPriceIsCrossed = false;

	public Level2ViewFredrikService() {
	};

	public Level2ViewFredrikService(boolean newOrderIfCrossed) {
		this.newOrderIfStockPriceIsCrossed = newOrderIfCrossed;
	}

	@Override
	public synchronized void onNewOrder(Side side, BigDecimal price, long quantity, long orderId) {

		List<Order> orderByPrice = getProperMap(side).get(price) == null ? new ArrayList<>()
				: getProperMap(side).get(price);

		if (nonNull(orders.get(orderId))) {
			throw new IllegalArgumentException("An order with this orderId already exists!");
		}

		Order newOrder = new Order(side, price, quantity, orderId);
		List<Order> interestedOrders = findAllInterestedOrdersBySideAndPrice(side, price);

		if (interestedOrders.isEmpty()) {
			orderByPrice.add(newOrder);
			getProperMap(side).putIfAbsent(price, orderByPrice);
			orders.putIfAbsent(orderId, newOrder);
		} else {
			consumeOrder(newOrder, interestedOrders);
		}
	}

	private List<Order> findAllInterestedOrdersBySideAndPrice(Side side, BigDecimal price) {
		return Side.BID.equals(side) ? retrieveAllOrdersForBid(side, price) : retrieveAllOrdersForAsk(side, price);
	}

	private Map<BigDecimal, List<Order>> getProperMap(Side side) {
		return Side.BID.equals(side) ? bidOrderMapByPrice : askOrderMapByPrice;
	}

	private List<Order> retrieveAllOrdersForAsk(Side side, BigDecimal price) {
		return bidOrderMapByPrice.entrySet().stream().filter((order) -> order.getKey().compareTo(price) > 0)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())).values().stream().flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private List<Order> retrieveAllOrdersForBid(Side side, BigDecimal price) {
		return askOrderMapByPrice.entrySet().stream().filter((order) -> order.getKey().compareTo(price) <= 0)
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())).values().stream().flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private void consumeOrder(Order order, List<Order> oppositeSideSamePriceOrders) {
		long quantityToConsume = order.getQuantity();
		for (Order orderToConsume : oppositeSideSamePriceOrders) {
			if (quantityToConsume > 0) {
				long quantityToTrade = Math.min(quantityToConsume, orderToConsume.getQuantity());
				onTrade(quantityToTrade, orderToConsume.getOrderId());
				quantityToConsume = quantityToConsume - quantityToTrade;
			}
		}
		generateNewOrderWithRemainingQuantity(order, quantityToConsume);
	}

	private void generateNewOrderWithRemainingQuantity(Order order, long quantityToConsume) {
		if (newOrderIfStockPriceIsCrossed) {
			if (quantityToConsume > 0) {
				onNewOrder(order.getSide(), order.getPrice(), quantityToConsume, order.getOrderId());
			}
		}
	}

	@Override
	public synchronized void onCancelOrder(long orderId) {
		Order orderToCancel = getOrderFromOrdersMap(orderId);

		List<Order> orderToCancelList = getProperMap(orderToCancel.getSide()).get(orderToCancel.getPrice());
		orderToCancelList.remove(orderToCancel);

		orders.remove(orderId);
		getProperMap(orderToCancel.getSide()).remove(orderToCancel.getPrice());

		if (!orderToCancelList.isEmpty()) {
			getProperMap(orderToCancel.getSide()).putIfAbsent(orderToCancel.getPrice(), orderToCancelList);
		}

	}

	@Override
	public synchronized void onReplaceOrder(BigDecimal price, long quantity, long orderId) {

		Order orderToReplace = getOrderFromOrdersMap(orderId);

		onCancelOrder(orderToReplace.getOrderId());
		onNewOrder(orderToReplace.getSide(), price, quantity, orderId);
	}

	private Order getOrderFromOrdersMap(long orderId) {
		Order orderToReplace;

		if (isNull(orders.get(orderId))) {
			throw new IllegalArgumentException("An order with this orderId does not exists!");
		} else {
			orderToReplace = orders.get(orderId);
		}
		return orderToReplace;
	}

	@Override
	public synchronized void onTrade(long quantity, long restingOrderId) {

		Order orderToTrade = getOrderFromOrdersMap(restingOrderId);

		if (orderToTrade.getQuantity() > quantity) {
			long remainingQty = orderToTrade.getQuantity() - quantity;
			onReplaceOrder(orderToTrade.getPrice(), remainingQty, restingOrderId);
			historyTrades.add(new Trade(orderToTrade.getPrice(), quantity, orderToTrade.getOrderId()));
		} else {
			onCancelOrder(restingOrderId);
			historyTrades.add(new Trade(orderToTrade.getPrice(), quantity, orderToTrade.getOrderId()));
		}
	}

	@Override
	public long getSizeForPriceLevel(Side side, BigDecimal price) {
		return getProperMap(side).get(price) == null ? 0
				: getProperMap(side).get(price).stream().map((order) -> order.getQuantity()).reduce(0L,
						(acc, x) -> acc + x);
	}

	@Override
	public long getBookDepth(Side side) {
		return getProperMap(side).size();
	}

	@Override
	public BigDecimal getTopOfBook(Side side) {
		return Side.ASK.equals(side) ? getLowestAsk() : getHighestBid();
	}

	private BigDecimal getHighestBid() {
		return bidOrderMapByPrice.isEmpty() ? BigDecimal.ZERO : bidOrderMapByPrice.lastKey();
	}

	private BigDecimal getLowestAsk() {
		return askOrderMapByPrice.isEmpty() ? BigDecimal.ZERO : askOrderMapByPrice.firstKey();
	}

	@Override
	public String toString() {
		return "Level2ViewImpl [askOrderMapByPrice=" + askOrderMapByPrice + ",bidOrderMapByPrice" + bidOrderMapByPrice
				+ ",historyTrades=" + historyTrades + "]";
	}

}
