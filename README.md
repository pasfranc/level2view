# level2view

Level2view is one implementation for an interface describing an electronic order book (see https://en.wikipedia.org/wiki/Order_book_(trading) ).
 
Instances of the implementing class should listen to market events (new orders, order cancellations/replacements, trades)
and provide a Level 2 view (total aggregated order quantity for a given side and price level).

## Assumptions

This implementation is making following assumptions:

```json

    1. The order are consumed from the cheapest to the most expensive in case of BID that is crossing the stock price
    2. The order are consumed from the most expansive ones to thecheapest in case of ASK that is crossing the stock price
    3. A replace order that crosses the stock price is consuming an order as well
    4. Order Id is not assigned by the system, but anyway it is unique (if not with replace order)
    5. Level2ViewService can be created with a boolean parameters (if true) that creates a new order in case we cannot satisfy the quantity with existing orders when there is a trade


``` 
 