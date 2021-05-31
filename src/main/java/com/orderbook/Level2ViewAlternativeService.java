package com.orderbook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.orderbook.model.Order;
import com.orderbook.model.Trade;

public class Level2ViewAlternativeService implements Level2View {

	private ConcurrentSkipListMap<BigDecimal, List<Order>> askOrderMapByPrice = new ConcurrentSkipListMap<>();
	private ConcurrentSkipListMap<BigDecimal, List<Order>> bidOrderMapByPrice = new ConcurrentSkipListMap<>();
	private List<Trade> historyTrades = new CopyOnWriteArrayList<>();

	private boolean newOrderIfStockPriceIsCrossed = false;

	public Level2ViewAlternativeService() {
	};

	public Level2ViewAlternativeService(boolean newOrderIfCrossed) {
		this.newOrderIfStockPriceIsCrossed = newOrderIfCrossed;
	}

	@Override
	public void onNewOrder(Side side, BigDecimal price, long quantity, long orderId) {

		List<Order> orderByPrice = getProperMap(side).get(price) == null ? new ArrayList<>()
				: getProperMap(side).get(price);
		List<Order> oppositeOrderByPrice = getProperMap(oppositeSide(side)).get(price) == null ? new ArrayList<>()
				: getProperMap(oppositeSide(side)).get(price);

		List<Order> fullOrderByPrice = Stream.concat(orderByPrice.stream(), oppositeOrderByPrice.stream())
				.collect(Collectors.toList());

		Optional<Order> orderWithSameOrderId = fullOrderByPrice.stream()
				.filter((order) -> order.getOrderId() == orderId).findAny();

		if (orderWithSameOrderId.isPresent()) {
			throw new IllegalArgumentException("An order with this orderId already exists!");
		}

		Order newOrder = new Order(side, price, quantity, orderId);
		List<Order> interestedOrders = findAllInterestedOrdersBySideAndPrice(side, price);

		if (interestedOrders.isEmpty()) {
			orderByPrice.add(newOrder);
			getProperMap(side).putIfAbsent(price, orderByPrice);
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
	public void onCancelOrder(long orderId) {
		List<Order> fullOrderList = getFullOrderList();

		Optional<Order> orderWithSameOrderId = fullOrderList.stream().filter((order) -> order.getOrderId() == orderId)
				.findAny();
		Order orderToCancel = findOrderWithSameOrderId(orderWithSameOrderId);

		List<Order> orderToCancelList = getProperMap(orderToCancel.getSide()).get(orderToCancel.getPrice());
		orderToCancelList.remove(orderToCancel);

		getProperMap(orderToCancel.getSide()).remove(orderToCancel.getPrice());

		if (!orderToCancelList.isEmpty()) {
			getProperMap(orderToCancel.getSide()).putIfAbsent(orderToCancel.getPrice(), orderToCancelList);
		}

	}

	private List<Order> getFullOrderList() {
		List<Order> askOrderList = askOrderMapByPrice.values().stream().flatMap(List::stream)
				.collect(Collectors.toList());

		List<Order> bidOrderList = bidOrderMapByPrice.values().stream().flatMap(List::stream)
				.collect(Collectors.toList());

		List<Order> fullOrderList = Stream.concat(askOrderList.stream(), bidOrderList.stream())
				.collect(Collectors.toList());
		return fullOrderList;
	}

	@Override
	public void onReplaceOrder(BigDecimal price, long quantity, long orderId) {
		List<Order> fullOrderList = getFullOrderList();

		Optional<Order> orderWithSameOrderId = fullOrderList.stream().filter((order) -> order.getOrderId() == orderId)
				.findAny();
		Order orderToReplace = findOrderWithSameOrderId(orderWithSameOrderId);

		onCancelOrder(orderToReplace.getOrderId());
		onNewOrder(orderToReplace.getSide(), price, quantity, orderId);
	}

	private Order findOrderWithSameOrderId(Optional<Order> orderWithSameOrderId) {
		Order orderToReplace;
		if (orderWithSameOrderId.isPresent()) {
			orderToReplace = orderWithSameOrderId.get();
		} else {
			throw new IllegalArgumentException("An order with this orderId does not exists!");
		}
		return orderToReplace;
	}

	@Override
	public void onTrade(long quantity, long restingOrderId) {

		List<Order> fullOrderList = getFullOrderList();

		Optional<Order> orderWithSameOrderId = fullOrderList.stream()
				.filter((order) -> order.getOrderId() == restingOrderId).findAny();
		Order restingOrder = findOrderWithSameOrderId(orderWithSameOrderId);

		if (restingOrder.getQuantity() > quantity) {
			long remainingQty = restingOrder.getQuantity() - quantity;
			onReplaceOrder(restingOrder.getPrice(), remainingQty, restingOrderId);
			historyTrades.add(new Trade(restingOrder.getPrice(), quantity, restingOrder.getOrderId()));
		} else {
			onCancelOrder(restingOrderId);
			historyTrades.add(new Trade(restingOrder.getPrice(), quantity, restingOrder.getOrderId()));
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

	private Side oppositeSide(Side side) {
		return side.equals(Side.ASK) ? Side.BID : Side.ASK;
	}

	@Override
	public String toString() {
		return "Level2ViewImpl [askOrderMapByPrice=" + askOrderMapByPrice + ",bidOrderMapByPrice" + bidOrderMapByPrice
				+ ",historyTrades=" + historyTrades + "]";
	}

}
