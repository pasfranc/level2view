package com.orderbook;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.orderbook.model.Order;
import com.orderbook.model.Trade;

public class Level2ViewService implements Level2View {

	private Map<Long, Order> orderMap = new ConcurrentHashMap<>();
	private List<Trade> historyTrades = new CopyOnWriteArrayList<>();

	private boolean newOrderIfStockPriceIsCrossed = false;

	public Level2ViewService() {
	};

	public Level2ViewService(boolean newOrderIfCrossed) {
		this.newOrderIfStockPriceIsCrossed = newOrderIfCrossed;
	}

	@Override
	public void onNewOrder(Side side, BigDecimal price, long quantity, long orderId) {
		if (isNull(orderMap.get(orderId))) {

			Order newOrder = new Order(side, price, quantity, orderId);
			List<Order> interestedOrders = findAllInterestedOrdersBySideAndPrice(side, price);

			if (interestedOrders.isEmpty()) {
				orderMap.put(orderId, newOrder);
			} else {
				consumeOrder(newOrder, interestedOrders);
			}
		} else {
			throw new IllegalArgumentException("An order with this orderId already exists!");
		}
	}

	private List<Order> findAllInterestedOrdersBySideAndPrice(Side side, BigDecimal price) {
		return Side.BID.equals(side) ? retrieveAllOrdersForBid(side, price) : retrieveAllOrdersForAsk(side, price);
	}

	private List<Order> retrieveAllOrdersForAsk(Side side, BigDecimal price) {
		return orderMap.entrySet().stream().filter((order) -> order.getValue().getSide().equals(oppositeSide(side)))
				.collect(Collectors.groupingBy((order) -> order.getValue().getPrice())).entrySet().stream()
				.filter((order) -> order.getKey().compareTo(price) > 0).map(Map.Entry::getValue)
				.collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()).stream()
				.map(Map.Entry::getValue).sorted((o2, o1) -> o1.getPrice().compareTo(o2.getPrice()))
				.collect(Collectors.toList());
	}

	private List<Order> retrieveAllOrdersForBid(Side side, BigDecimal price) {
		return orderMap.entrySet().stream().filter((order) -> order.getValue().getSide().equals(oppositeSide(side)))
				.collect(Collectors.groupingBy((order) -> order.getValue().getPrice())).entrySet().stream()
				.filter((order) -> order.getKey().compareTo(price) <= 0).map(Map.Entry::getValue)
				.collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()).stream()
				.map(Map.Entry::getValue).sorted((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()))
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
	public void onCancelOrder(long orderId) {
		if (nonNull(orderMap.get(orderId))) {
			orderMap.remove(orderId);
		} else {
			throw new IllegalArgumentException("An order with this orderId does not exists!");
		}
	}

	@Override
	public void onReplaceOrder(BigDecimal price, long quantity, long orderId) {
		if (nonNull(orderMap.get(orderId))) {
			Order oldOrder = orderMap.get(orderId);
			onCancelOrder(oldOrder.getOrderId());
			onNewOrder(oldOrder.getSide(), price, quantity, orderId);
		} else {
			throw new IllegalArgumentException("An order with this orderId does not exists!");
		}
	}

	@Override
	public void onTrade(long quantity, long restingOrderId) {
		if (nonNull(orderMap.get(restingOrderId))) {
			Order restingOrder = orderMap.get(restingOrderId);
			if (restingOrder.getQuantity() > quantity) {
				long remainingQty = restingOrder.getQuantity() - quantity;
				onReplaceOrder(restingOrder.getPrice(), remainingQty, restingOrderId);
				historyTrades.add(new Trade(restingOrder.getPrice(), quantity, restingOrder.getOrderId()));
			} else {
				onCancelOrder(restingOrderId);
				historyTrades.add(new Trade(restingOrder.getPrice(), quantity, restingOrder.getOrderId()));
			}
		} else {
			throw new IllegalArgumentException("An order with this orderId does not exists!");
		}
	}

	@Override
	public long getSizeForPriceLevel(Side side, BigDecimal price) {
		return orderMap.entrySet().stream().filter((order) -> order.getValue().getSide().equals(side))
				.collect(Collectors.groupingBy((order) -> order.getValue().getPrice())).entrySet().stream()
				.filter((order) -> order.getKey().equals(price)).map(Map.Entry::getValue).collect(Collectors.toList())
				.stream().flatMap(List::stream).collect(Collectors.toList()).stream().map(Map.Entry::getValue)
				.collect(Collectors.toList()).stream().map((order) -> order.getQuantity())
				.reduce(0L, (acc, x) -> acc + x);
	}

	@Override
	public long getBookDepth(Side side) {
		return orderMap.entrySet().stream().filter((order) -> order.getValue().getSide().equals(side))
				.collect(Collectors.groupingBy((order) -> order.getValue().getPrice())).entrySet().stream()
				.collect(Collectors.counting());
	}

	@Override
	public BigDecimal getTopOfBook(Side side) {
		return Side.ASK.equals(side) ? getLowestAsk() : getHighestBid();
	}

	private BigDecimal getHighestBid() {
		return orderMap.entrySet().stream().filter((order) -> order.getValue().getSide().equals(Side.BID))
				.map((order) -> order.getValue().getPrice()).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
	}

	private BigDecimal getLowestAsk() {
		return orderMap.entrySet().stream().filter((order) -> order.getValue().getSide().equals(Side.ASK))
				.map((order) -> order.getValue().getPrice()).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
	}

	private Side oppositeSide(Side side) {
		return side.equals(Side.ASK) ? Side.BID : Side.ASK;
	}

	@Override
	public String toString() {
		return "Level2ViewImpl [orderMap=" + orderMap + ", historyTrades=" + historyTrades + "]";
	}

}
