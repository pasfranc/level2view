package com.orderbook;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.orderbook.Level2View.Side;

public class Level2ViewServiceTest{
	
	@Test
    public void testOnNewOrder() {
		Level2View level2 = new Level2ViewService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);

        assertEquals(new BigDecimal("10.00"), level2.getTopOfBook(Side.ASK));
        assertEquals(100L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
        assertEquals(1L,level2.getBookDepth(Side.ASK));

    }
	
	@Test
    public void testOnNewOrderMultipleOrdersSamePrice() {
		Level2View level2 = new Level2ViewService();
		long orderId = 1;

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, orderId++);

        assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
        assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
        assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
        assertEquals(2L,level2.getBookDepth(Side.ASK));

    }
	
	@Test
    public void testOnNewOrderMultipleOrdersAskAndBid() {
		Level2View level2 = new Level2ViewService();
		long orderId = 1;

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, orderId++);
		level2.onNewOrder(Side.ASK, new BigDecimal("9.00"), 4, orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100,orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 1, orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("8.50"), 100, orderId++);
		level2.onNewOrder(Side.BID, new BigDecimal("7.50"), 54, orderId++);

        assertEquals(new BigDecimal("9.00"), level2.getTopOfBook(Side.ASK));
        assertEquals(151L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("10.00")));
        assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
        assertEquals(54L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("7.50")));
        assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
        assertEquals(2L,level2.getBookDepth(Side.ASK));
        assertEquals(2L,level2.getBookDepth(Side.BID));
    }
	
	@Test
    public void testOnReplaceOrder() {
		Level2View level2 = new Level2ViewService();

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
        assertEquals(2L,level2.getBookDepth(Side.ASK));
        assertEquals(2L,level2.getBookDepth(Side.BID));
        
        level2.onReplaceOrder(new BigDecimal("8.51"), 4, 6);
        assertEquals(200L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
        assertEquals(4L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.51")));
        assertEquals(3L,level2.getBookDepth(Side.BID));

    }
	
	@Test
    public void testOnReplaceOrderOverTheSpread() {
		Level2View level2 = new Level2ViewService();

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
        assertEquals(2L,level2.getBookDepth(Side.ASK));
        assertEquals(2L,level2.getBookDepth(Side.BID));
        
        level2.onReplaceOrder(new BigDecimal("9.00"), 50, 6);
        assertEquals(200L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
        assertEquals(2L,level2.getBookDepth(Side.BID));
        assertEquals(1L,level2.getBookDepth(Side.ASK));

    }
	
	@Test
    public void testOnReplaceBidOrderOverTheSpreadThatInsertNewOrder() {
		Level2View level2 = new Level2ViewService(true);

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
        assertEquals(2L,level2.getBookDepth(Side.ASK));
        assertEquals(2L,level2.getBookDepth(Side.BID));
        
        level2.onReplaceOrder(new BigDecimal("9.00"), 50, 6);
        assertEquals(200L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
        assertEquals(46L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
        assertEquals(3L,level2.getBookDepth(Side.BID));
        assertEquals(1L,level2.getBookDepth(Side.ASK));

    }
	
	@Test
    public void testOnReplaceBidOrderOverTheSpreadThatConsumeAll() {
		Level2View level2 = new Level2ViewService();

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.BID, new BigDecimal("9.00"), 4, 4);
        
        level2.onReplaceOrder(new BigDecimal("12.00"), 1000, 4);
        assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
        assertEquals(0L,level2.getBookDepth(Side.BID));
        assertEquals(0L,level2.getBookDepth(Side.ASK));

    }
	
	@Test
    public void testOnReplaceBidOrderOverTheSpreadThatConsumeAllAndInsertNewOrder() {
		Level2View level2 = new Level2ViewService(true);

		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 50, 2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, 3);
		level2.onNewOrder(Side.BID, new BigDecimal("9.00"), 4, 4);
        
        level2.onReplaceOrder(new BigDecimal("12.00"), 1000, 4);
        assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
        assertEquals(849L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("12.00")));
        assertEquals(1L,level2.getBookDepth(Side.BID));
        assertEquals(0L,level2.getBookDepth(Side.ASK));

    }
	
	@Test
    public void testOnReplaceAskOrderOverTheSpreadThatInsertNewOrder() {
		Level2View level2 = new Level2ViewService(true);

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
        assertEquals(2L,level2.getBookDepth(Side.ASK));
        assertEquals(2L,level2.getBookDepth(Side.BID));
        
        level2.onReplaceOrder(new BigDecimal("8.50"), 99, 3);
        assertEquals(201L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("8.50")));
        assertEquals(4L, level2.getSizeForPriceLevel(Side.ASK, new BigDecimal("9.00")));
        assertEquals(0L, level2.getSizeForPriceLevel(Side.BID, new BigDecimal("9.00")));
        assertEquals(2L,level2.getBookDepth(Side.BID));
        assertEquals(3L,level2.getBookDepth(Side.ASK));

    }

}
