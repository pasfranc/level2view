package com.orderbook;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.orderbook.Level2View.Side;

public class Level2ViewFredrikServiceTest {

	@Test
	public void testOnNewOrder() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);

		assertEquals(new BigDecimal("10.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(100L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(1L, level2.getBookDepth(Side.ASK));

	}

	@Test
	public void testOnNewOrderMultipleOrdersSamePrice() {
		Level2View level2 = new Level2ViewFredrikService();
		long orderId = 1;

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, orderId++);

		assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(2L, level2.getBookDepth(Side.ASK));

	}

	@Test
	public void testOnNewOrderMultipleOrdersAskAndBid() {
		Level2View level2 = new Level2ViewFredrikService();
		long orderId = 1;

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 1, orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("7.50"), 54, orderId++);

		assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(54L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("7.50")));
		assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(2L, level2.getBookDepth(Side.ASK));
		assertEquals(2L, level2.getBookDepth(Side.BID));
	}

	@Test
	public void testOnReplaceOrder() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, 4);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 5);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 1, 6);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 7);
		level2.onNewOrder(Side.BID, new BigDecimal("7.50"), 54, 8);

		assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(54L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("7.50")));
		assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(2L, level2.getBookDepth(Side.ASK));
		assertEquals(2L, level2.getBookDepth(Side.BID));

		level2.onReplaceOrder(new BigDecimal("8.51"), 4, 6);
		assertEquals(200L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.51")));
		assertEquals(3L, level2.getBookDepth(Side.BID));

	}

	@Test
	public void testOnReplaceOrderOverTheSpread() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, 4);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 5);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 1, 6);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 7);
		level2.onNewOrder(Side.BID, new BigDecimal("7.50"), 54, 8);

		assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(54L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("7.50")));
		assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(2L, level2.getBookDepth(Side.ASK));
		assertEquals(2L, level2.getBookDepth(Side.BID));

		level2.onReplaceOrder(new BigDecimal("9.00"), 50, 6);
		assertEquals(200L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
		assertEquals(2L, level2.getBookDepth(Side.BID));
		assertEquals(1L, level2.getBookDepth(Side.ASK));

	}

	@Test
	public void testOnReplaceBidOrderOverTheSpreadThatInsertNewOrder() {
		Level2View level2 = new Level2ViewFredrikService(true);

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, 4);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 5);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 1, 6);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 7);
		level2.onNewOrder(Side.BID, new BigDecimal("7.50"), 54, 8);

		assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(54L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("7.50")));
		assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(2L, level2.getBookDepth(Side.ASK));
		assertEquals(2L, level2.getBookDepth(Side.BID));

		level2.onReplaceOrder(new BigDecimal("9.00"), 50, 6);
		assertEquals(200L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(46L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
		assertEquals(3L, level2.getBookDepth(Side.BID));
		assertEquals(1L, level2.getBookDepth(Side.ASK));

	}

	@Test
	public void testOnReplaceBidOrderOverTheSpreadThatConsumeAll() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.BID, new BigDecimal("9.00"), 4, 4);

		level2.onReplaceOrder(new BigDecimal("12.00"), 1000, 4);
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
		assertEquals(0L, level2.getBookDepth(Side.BID));
		assertEquals(0L, level2.getBookDepth(Side.ASK));

	}

	@Test
	public void testOnReplaceBidOrderOverTheSpreadThatConsumeAllAndInsertNewOrder() {
		Level2View level2 = new Level2ViewFredrikService(true);

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.BID, new BigDecimal("9.00"), 4, 4);

		level2.onReplaceOrder(new BigDecimal("12.00"), 1000, 4);
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
		assertEquals(849L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("12.00")));
		assertEquals(1L, level2.getBookDepth(Side.BID));
		assertEquals(0L, level2.getBookDepth(Side.ASK));

	}

	@Test
	public void testOnReplaceAskOrderOverTheSpreadThatInsertNewOrder() {
		Level2View level2 = new Level2ViewFredrikService(true);

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, 4);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 5);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 1, 6);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, 7);
		level2.onNewOrder(Side.BID, new BigDecimal("7.50"), 54, 8);

		assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(54L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("7.50")));
		assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(2L, level2.getBookDepth(Side.ASK));
		assertEquals(2L, level2.getBookDepth(Side.BID));

		level2.onReplaceOrder(new BigDecimal("8.50"), 99, 3);
		assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
		assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
		assertEquals(2L, level2.getBookDepth(Side.BID));
		assertEquals(3L, level2.getBookDepth(Side.ASK));

	}

	@Test
	public void testOnCancelOrderOverTheSpreadThatInsertNewOrder() {
		Level2View level2 = new Level2ViewFredrikService(true);

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);

		level2.onCancelOrder(1);

		assertEquals(new BigDecimal("10.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(50L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnCancelOrderThatDoesNotExist() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onCancelOrder(1);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnReplaceOrderThatDoesNotExist() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onReplaceOrder(new BigDecimal("12.00"), 1000, 4);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnTradeOrderThatDoesNotExist() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onTrade(1000, 1);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnCreateOrderThatAlreadyExist() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 2);

	}

	@Test
	public void testGetSizeOnPriceLevel() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 10, 4);

		assertEquals(161L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));

	}

	@Test
	public void testOnTradeThatCancelAnOrder() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.BID, new BigDecimal("10.00"), 100, 2);

		assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("10.00")));
		assertEquals(BigDecimal.ZERO, level2.getTopOfBook(Side.ASK));
		assertEquals(BigDecimal.ZERO, level2.getTopOfBook(Side.BID));
	}

	@Test
	public void testOnTradeThatPartiallyConsumeAnOrder() {
		Level2View level2 = new Level2ViewFredrikService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 101, 1);
		level2.onNewOrder(Side.BID, new BigDecimal("10.00"), 100, 2);

		assertEquals(1L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("10.00")));
		assertEquals(new BigDecimal("10.00"), level2.getTopOfBook(Side.ASK));
		assertEquals(BigDecimal.ZERO, level2.getTopOfBook(Side.BID));
	}

	@Test
	public void testGetSizeForPriceLevel() {
		Level2View level2 = new Level2ViewFredrikService();

		assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("10.00")));

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 101, 1);
		level2.onNewOrder(Side.BID, new BigDecimal("9.00"), 100, 2);

		assertEquals(101L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(100L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 101, 3);
		level2.onNewOrder(Side.BID, new BigDecimal("9.00"), 100, 4);

		assertEquals(202L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
		assertEquals(200L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));

	}

}
