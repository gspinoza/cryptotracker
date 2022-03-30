package spinoza;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class FIFODeque {
	// Store current Current Holdings for Transactions
	private HashMap<String, Deque<Coin>> coinHoldings = new HashMap<String, Deque<Coin>>();
	// Store Current Holdings For PortFolio
	private HashMap<String, Double> currentHoldings = new HashMap<String, Double>();
	
	public void calculateCurrentHoldings(String coin, double quantity) {
		if(currentHoldings.containsKey(coin)) {
			// edit existing element
			currentHoldings.put(coin, currentHoldings.get(coin) + quantity);
		} else { // add new coin
			currentHoldings.put(coin, quantity);
		}
	}
	
	public HashMap<String, Double> getCurrentHoldings() {
		return currentHoldings;
	}
	
	// clear Deque
	public void resetDeuqe() {
		coinHoldings.clear();
		currentHoldings.clear();
	}
	
	// when Buying or trading add quantity to the deque
	public void addCoinToFIFODeque(String coin, double quantity, double rate, int tradeId) {
		System.out.println(coin + "pp");
		calculateCurrentHoldings(coin, quantity); // for PortFolio
		
		if(coinHoldings.containsKey(coin)) {
			// add coin to deque
			coinHoldings.get(coin).add(new Coin(coin, new BigDecimal(Double.toString(quantity)), new BigDecimal(Double.toString(rate)), tradeId));
		} else {
			// create new deque for new coin
			Deque<Coin> coinDeque = new LinkedList<Coin>();
			// add new coin to deque
			coinDeque.add(new Coin(coin, new BigDecimal(Double.toString(quantity)), new BigDecimal(Double.toString(rate)), tradeId));
			// add (key/coin) and (deque/value) to "Dictionary"
			coinHoldings.put(coin, coinDeque);
		}
	}
	
	// when Selling or trading spend use FIFO
	public String sellTradeFIFO(String coin, double quantity, double rate, int tradeId) {
		// 
		double gainLoss = 0;
		// total quantity selling/trading
		BigDecimal quantitySelling = new BigDecimal(Double.toString(quantity));
		// current value of coins selling/trading
		BigDecimal currentValue = new BigDecimal(Double.toString(quantity)).multiply(new BigDecimal(Double.toString(rate)));
		
		// check if you have that coin in your deque
		if(coinHoldings.containsKey(coin)) {
			// total initial investment
			BigDecimal totalInitialInvestment = new BigDecimal("0");
			
			while (quantitySelling.compareTo(BigDecimal.ZERO) != 0) {
				// get first coin from the deque
				displayHashMap();
				Coin currentCoin = coinHoldings.get(coin).pop();
				// get current coin quantity
				BigDecimal currentQuantity = currentCoin.getQunatity();
				System.out.println(quantitySelling + " <-- SELLING Quanity");
				System.out.println(currentQuantity + " <-- Current Quanity");
				// get current coin rate
				BigDecimal currentRate = currentCoin.getRate();

				// get current coin TradeId
				int currentTradeId = currentCoin.getTradeId();
				
				// if quantitySelling is greater than current coin quantity
				if (quantitySelling.compareTo(currentQuantity) >= 0) {
					// quantity selling minus currentQuantity
					quantitySelling = quantitySelling.subtract(currentQuantity);
					// calculate totalInitialInvestment of current coin popped
					//BigDecimal totalInitialInvestment1 = currentQuantity.multiply(currentRate);
					totalInitialInvestment = totalInitialInvestment.add(currentQuantity.multiply(currentRate));
					System.out.println("ini 1= " + totalInitialInvestment);
					calculateCurrentHoldings(coin, -currentQuantity.doubleValue()); // for PortFolio
				} else {
					// calculate what will be the remaining quantity of coin popped
					BigDecimal remainingQuantity = currentQuantity.subtract(quantitySelling);
					// add remaining quantity of coin back to the front of queue
					coinHoldings.get(coin).addFirst(new Coin(coin, remainingQuantity, currentRate, currentTradeId));
					// quantity selling minus (currentQuantity-remainingQuantity)
					quantitySelling = (quantitySelling.subtract((currentQuantity.subtract(remainingQuantity))));
					// calculate totalInitialInvestment of current coin popped
					//totalInitialInvestment = ((currentQuantity.subtract(remainingQuantity)).multiply(currentRate));
					totalInitialInvestment = totalInitialInvestment.add(((currentQuantity.subtract(remainingQuantity)).multiply(currentRate)));
					System.out.println("ini 2= " + totalInitialInvestment);
					calculateCurrentHoldings(coin, -currentQuantity.doubleValue()); // for PortFolio
				}
			}
			// calculate gain/loss
			System.out.println("InitialValue = " + totalInitialInvestment);
			System.out.println("CurrentValue = " + currentValue);
			
			gainLoss = (currentValue.subtract(totalInitialInvestment)).doubleValue();
			// format ex ($500.00 USD~3.00 ETH @ $1.00)
			System.out.println(gainLoss+"SSSSSSSSSSSS");
			return ("$" + gainLoss + "~" + gainLoss + " " + coin + " @ $1.00"); 
		} else 
			// show message
			System.out.println("You don't have any " + coin + " to sell!");
			return "$" + gainLoss + "~" + gainLoss + " " + coin + " @ $0.00" ;
	}
	
	
	public double getCurrentHoldingsTrans(String coin, int tradeId) {

		// iterate dictionary (HashMap) and deque for the coin
		if(coinHoldings.containsKey(coin)) {
			// iterate coin deque
	        Iterator<Coin> iterator = coinHoldings.get(coin).iterator();
	        while (iterator.hasNext()) {
	        	Coin currentCoin = (Coin) iterator.next();
	        	if (currentCoin.getTradeId() == tradeId) {
	        		return currentCoin.getQunatity().doubleValue();
	        	}
	        }
		}
		//System.out.println("no holdings found for " + coin);
		return 0;
	}
	
	//
	public String getGainLossAndFillDeque(String buyingCoin, String action, double quantityBuying, double rateBuying, double price, int id, String sellingCoin, double sellingQuantity, double sellingRate) {
		String gainLoss = "$" + "0.00 USD" + "~" + "0" + " " + sellingCoin + " @ $1.00" ;

		// TRADING USD <> CRYPTO
		// System.out.println(buyingCoin+"buy");
		// System.out.println(sellingCoin+"sell");
		if (buyingCoin.contains("(USD)") || sellingCoin.contains("(USD)")) {
			if (sellingCoin.contains("(USD)")) { // selling USD
				addCoinToFIFODeque(buyingCoin, quantityBuying, rateBuying, id);
			} else { // Selling Crypto
				return gainLoss = sellTradeFIFO(sellingCoin, sellingQuantity, sellingRate, id);
			}
			
		} else { // TRADING CRYPTO <> CRYPTO
			// add buying coin to deque
			addCoinToFIFODeque(buyingCoin, 	quantityBuying, rateBuying, id);
			// calculate gainLoss
			System.out.println(sellingCoin+ "SELLINGGGG " + sellingQuantity + " "+sellingRate +" "+ id + "");
			return gainLoss = sellTradeFIFO(sellingCoin, sellingQuantity, sellingRate, id);
		}
		
		return gainLoss;
		// calculate gainLoss
		//return gainLoss = sellTradeFIFO(coin, quantity, rate, id);
		
		// buying - add coin to Deque
		// addCoinToFIFODeque(coin, quantity, rate, id);
	}
	
	public void displayHashMap() {
        // using for-each loop for iteration over Map.entrySet()
        for (HashMap.Entry<String, Deque<Coin>> entry : coinHoldings.entrySet()) {
        	
			// create new temp deque for temp coin
			Deque<Coin> tempCoinDeque = entry.getValue();
			
            System.out.print("[" + entry.getKey() +"]" + " - " + "[");
			// iterate coin deque
	        Iterator<Coin> iterator = tempCoinDeque.iterator();
	        while (iterator.hasNext()) {
	        	Coin currentCoin = (Coin) iterator.next();
	        	
	        	System.out.print(currentCoin.toString() + " || ");
	        }
	        System.out.println("]");
        } // [Tether (USDT)] - [(Tether (USDT), 0.08458132, 1.0, 1898439433)(Tether (USDT), 1809.54673469, 1.0, 1226757339)]
	}
	
	 public class Coin {
		private String coin;
		private BigDecimal quantity;
		private BigDecimal rate;
		private int tradeId;
		
		public Coin (String coin, BigDecimal quantity, BigDecimal rate,int tradeId) {
			this.coin = coin;
			this.quantity = quantity;
			this.rate = rate;
			this.tradeId = tradeId;
		}
		// getters
		public String getCoin() {
			return coin;
		}
		
		public BigDecimal getQunatity() {
			return quantity;
		}
		
		public BigDecimal getRate() {
			return rate;
		}
		
		public int getTradeId() {
			return tradeId;
		}
		
		// Setters
		public void setCoin(String coin) {
			this.coin = coin;
		}
		
		public void setQunatity(BigDecimal qunatity) {
			this.quantity = qunatity;
		}
		
		public void setRate(BigDecimal rate) {
			this.rate = rate;
		}
		
		public void setTradeId(int tradeId) {
			this.tradeId = tradeId;
		}
		
		public String toString() {
			String str = "(" + coin + ", " + quantity.toString() + ", " + rate.toString() + ", " + tradeId + ")";
			return  str;
		}
		
	} // end coin class
}
