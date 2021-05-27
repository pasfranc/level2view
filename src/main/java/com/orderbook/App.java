package com.orderbook;

import java.math.BigDecimal;

import com.orderbook.Level2View.Side;

public class App {
    public static void main( String[] args ) {
		Level2View level2 = new Level2ViewValidated(new Level2ViewService());
		
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 1);
		System.out.println(level2);
		level2.onNewOrder(Side.ASK, new BigDecimal("10.00"), 100, 2);
		System.out.println(level2);
		level2.onNewOrder(Side.ASK, new BigDecimal("11.00"), 100, 3);
		System.out.println(level2);
		level2.onNewOrder(Side.ASK, new BigDecimal("12.00"), 100, 4);
		System.out.println(level2);
		level2.onNewOrder(Side.BID, new BigDecimal("9.00"), 100, 5);
		System.out.println(level2);
		level2.onNewOrder(Side.ASK, new BigDecimal("8.00"), 100, 6);
		System.out.println(level2);
		level2.onNewOrder(Side.BID, new BigDecimal("20.00"), 110, 7);
		System.out.println(level2);
		level2.onNewOrder(Side.BID, new BigDecimal("5.00"), 150, 8);
		System.out.println(level2);
		level2.onNewOrder(Side.BID, new BigDecimal("7.00"), 150, 9);
		System.out.println(level2);
		System.out.println(level2.getBookDepth(Side.ASK));
		System.out.println(level2.getBookDepth(Side.BID));
		System.out.println(level2.getTopOfBook(Side.ASK));

		System.out.println(level2.getTopOfBook(Side.BID));
		
		System.out.println(level2.getSizeForPriceLevel(Side.ASK,new BigDecimal("15.00")));
    }
}
