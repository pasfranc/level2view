package com.orderbook;

import java.math.BigDecimal;

import org.junit.Test;

import com.orderbook.Level2View.Side;

public class Level2ViewValidatedTest {

	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderValidateWrongSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(Side.valueOf("PIPPO"), new BigDecimal("10.00"), 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderValidateNullSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(null, new BigDecimal("10.00"), 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderValidateNegativeBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(Side.ASK, new BigDecimal("-10.00"), 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderValidateZeroBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(Side.ASK, BigDecimal.ZERO, 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderValidateNullBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(Side.ASK, null, 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderValidateNegativeQty() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(Side.ASK, new BigDecimal("10.00"), -100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderValidateZeroQty() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(Side.ASK, new BigDecimal("10.00"), 0, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnNewOrderNegativeOrderId() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onNewOrder(Side.ASK, new BigDecimal("10.00"), 1, -1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnCancelOrderNegativeOrderId() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onCancelOrder(-1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnCancelOrderZeroOrderId() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onCancelOrder(0);

    }
	
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnReplaceOrderValidateNegativeBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onReplaceOrder(new BigDecimal("-10.00"), 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnReplaceOrderValidateZeroBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onReplaceOrder(BigDecimal.ZERO, 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnReplaceOrderValidateNullBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onReplaceOrder(null, 100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnReplaceOrderValidateNegativeQty() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onReplaceOrder(new BigDecimal("10.00"), -100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnReplaceOrderValidateZeroQty() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onReplaceOrder(new BigDecimal("10.00"), 0, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnReplaceOrderNegativeOrderId() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onReplaceOrder(new BigDecimal("10.00"), 1, -1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnTradeOrderValidateNegativeQty() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onTrade(-100, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnTradeOrderValidateZeroQty() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onTrade(0, 1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testOnTradeOrderNegativeOrderId() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.onTrade(1, -1);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetSizeOnPriceLevelValidateWrongSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getSizeForPriceLevel(Side.valueOf("PIPPO"), new BigDecimal("10.00"));

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetSizeOnPriceLevelValidateNullSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getSizeForPriceLevel(null, new BigDecimal("10.00"));

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetSizeOnPriceLevelValidateNegativeBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getSizeForPriceLevel(Side.ASK, new BigDecimal("-10.00"));

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetSizeOnPriceLevelValidateZeroBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getSizeForPriceLevel(Side.ASK, BigDecimal.ZERO);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetSizeOnPriceLevelValidateNullBigDecimal() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getSizeForPriceLevel(Side.ASK, null);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetBookDepthValidateWrongSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getBookDepth(Side.valueOf("PIPPO"));

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetBookDepthValidateNullSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getBookDepth(null);

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetTopOfTheBookValidateWrongSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getTopOfBook(Side.valueOf("PIPPO"));

    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testGetTopOfTheBookValidateNullSide() {
		Level2View level2Validated = new Level2ViewValidated(new Level2ViewService());

		level2Validated.getTopOfBook(null);

    }

}
