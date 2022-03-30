package spinoza;

import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Transaction {
	private SimpleStringProperty date, exchangeSite, exchange, type, rate, price, fee, totalCostPrice, gainsLoss, memo;
	private SimpleDoubleProperty quantity;
	private SimpleIntegerProperty tradeId;
	private String buyingCoin, sellingCoin;
	
	// default constructor
	public Transaction() {
	}
	
	// parameterized constructor
	public Transaction(String date, String exchangeSite, String exchange, String type, double quantity, String rate, String price, String fee, String totalCostPrice, String gainsLoss, String notes, int tradeId) {
		this.date = new SimpleStringProperty(date);
		this.exchangeSite = new SimpleStringProperty(exchangeSite);
		this.exchange = new SimpleStringProperty(exchange);
		this.type = new SimpleStringProperty(type);
		this.quantity = new SimpleDoubleProperty (quantity);
		this.rate = new SimpleStringProperty(rate);
		this.price = new SimpleStringProperty(price);
		this.fee = new SimpleStringProperty(fee);
		this.totalCostPrice = new SimpleStringProperty(totalCostPrice);
		this.gainsLoss = new SimpleStringProperty(gainsLoss);
		this.memo = new SimpleStringProperty(notes);
		this.tradeId = new SimpleIntegerProperty(tradeId);
	}
	
	/*
	 * Setters
	 */
	
	public void setDate(String date) {
		this.date.set(date);
	}
	
	public void setExchangeSite(String exchangeSite) {
		this.exchangeSite.set(exchangeSite);
	}
	
	public void setExchange(String exchange) {
		this.exchange.set(exchange);
	}
	
	public void setType(String type){ 
		this.type.set(type);
	}
	
	public void setQuantity(double quantity) {
		this.quantity.set(quantity);
	}
	
	public void setRate(String rate) {
		this.rate.set(rate);
	}
	
	public void setPrice(String price) {
		this.price.set(price);
	}
	
	public void setFee(String fee) {
		this.fee.set(fee);
	}
	
	public void setTotalCostPrice(String totalCostPrice) {
		this.totalCostPrice.set(totalCostPrice);
	}
	
	public void setTotalGainsLoss(String gainsLoss) {
		this.gainsLoss.set(gainsLoss);
	}

	public void setMemo(String notes) {
		this.memo.set(notes);
	}
	
	public void setTradeID(int tradeId) {
		this.tradeId.set(tradeId);
	}
	
	/*
	 * Getters
	 */
	
	public String getDate() {
		return date.get();
	}
	
	public String getExchangeSite() {
		return exchangeSite.get();
	}
	
	public String getExchange() {
		return exchange.get();
	}
	
	public String getType() {
		return type.get();
	}
	
	public double getQuantity() {
		return quantity.get();
	}
	
	public String getRate() {
		return rate.get();
	}
	
	public String getPrice() {
		return price.get();
	}
	
	public String getFee() {
		return fee.get();
	}
	
	public String getTotalCostPrice() {
		return totalCostPrice.get();
	}
	
	public String getGainsLoss() {
		return gainsLoss.get();
	}

	public String getMemo() {
		return memo.get();
	}
	
	public int getTradeId() {
		return tradeId.get();
	}
	
	public String toString() {
		String str = "\n" + "[" + date + ", " + exchangeSite + ", " + exchange + ", " + type + ", " + quantity + ", " + rate + ", " + price + ", " + fee + ", " + totalCostPrice + ", " + gainsLoss + ", " + memo + ", " + tradeId + "]";
		return str;
	}
	
	/*
	 * Get Specific Fields
	 */
	
	// decide selling and buying fields
	public void determineBuyingSellingCoin(ArrayList<String> fileCoins) {
		// first get coin names. example: "BTC" to "Bitcoin (BTC)"
		String marketCoin = "";
		String altCoin = "";
		for (String coin : fileCoins) {			
			if (coin.contains("("+getExchange().replaceAll("-.*", "")+")")) {
				marketCoin = coin;
			} else if (coin.contains("("+getExchange().replaceAll(".*-", "")+")")) {
				altCoin = coin;
			}
		}
		// decide which is which and give the full coin name
		if (getType().equals("BUY")) {
			buyingCoin = altCoin;
			sellingCoin = marketCoin;
		} else {
			buyingCoin = marketCoin;
			sellingCoin = altCoin;
		}
	}
	
	// return buying coin
	public String getBuyingCoin() {
		return buyingCoin;
	}
	
	// return selling coin
	public String getSellingCoin() {
		return sellingCoin;
	}
	//0.5523423 ETH @ $33.44
	//~ $500.00 USD
	
	public double getRateDoubleBuying() {
		String tempBuyingRate;
		if (getType().equals("BUY")) {
			// System.out.println(getRate()+"!");
			tempBuyingRate = getRate().replaceAll("\\$", "").replaceAll(" USD.*", ""); //$(921.10) USD~1 ETH @ $921.1
			// System.out.println(tempBuyingRate+"!");
		} else {
			tempBuyingRate = getTotalCostPrice().replaceAll(".* \\$", ""); //$-999.49 USD~-999.49 USD @ $(1.00)
		}
		return Double.parseDouble(tempBuyingRate);
	}
	
	public double getRateDoubleSelling() {
		String tempSellingRate;
		if (getType().equals("SELL")) {
			//String tmpRateSelling = getPrice().replaceAll("^(?=[^\\n]+$)[^$]+\\$", "");
			System.out.println(getTotalCostPrice()+"REAYE SElling");
			tempSellingRate = getRate().replaceAll("\\$", "").replaceAll(" USD.*", ""); //$(921.10) USD~1 ETH @ $921.1
			System.out.println(tempSellingRate+"REAYE AFTER");
		} else {
			tempSellingRate = getTotalCostPrice().replaceAll(".* \\$", ""); //$-999.49 USD~-999.49 USD @ $(1.00)
		}
		return Double.parseDouble(tempSellingRate);
	}
	
	public double getFeeDouble() {
		String tmpFee = getFee().replaceAll(".*\\$", "").replaceAll(" USD", "");
		return Double.parseDouble(tmpFee);
	}
	
	public double getSubTotalDouble() {
		String tmpSubTotal = getFee().replaceAll(".*\\$", "").replaceAll(" USD", "");
		return Double.parseDouble(tmpSubTotal);
	}
	
	public double getQuantityBuying() {
		Double tmpQuantityBuying;
		if (getType().equals("BUY")) {
			tmpQuantityBuying = getQuantity();
		} else {
			tmpQuantityBuying = Double.parseDouble(getTotalCostPrice().replaceAll(".*~", "").replaceAll(" .*", "")); // $-999.49 USD~-999.49 USD @ $(1.00)
		}
		return tmpQuantityBuying;
	}
	
	public double getQuantitySelling() { //1.1334 ADA @ $40.00\n$50.00 USD
		// Extract Coin Quantity  just in case: regex to extract quantity ".+?(?=[ ])"  
		// Double.parseDouble(selling.substring(selling.indexOf("\\d") + 1, selling.indexOf(" ")));
		Double tmpQuantitySelling;
		if (getType().equals("SELL")) {
			tmpQuantitySelling = getQuantity();
		} else {
			// $500.93 USD~503.43806143 USDT @ $1.00 
			tmpQuantitySelling = Double.parseDouble(getTotalCostPrice().replaceAll(".*~", "").replaceAll(" .*", ""));
		}
		return tmpQuantitySelling;
	}
	
	public double getGainLossDouble() {
		String tmpGainLoss = getGainsLoss().replaceAll(".*\\$", "").replaceAll(" USD", "");
		return Double.parseDouble(tmpGainLoss);
	}
	
	// 0.04315556 Bitcoin (BTC) @ $10000.60
	// Extract CoinName
	//String sellingCoin = selling.replaceAll("[\\d\\.]", "").replace(" @ $", "").trim(); //0.04315556 BTC @ $10000.60
	
	
} // end Transaction class
