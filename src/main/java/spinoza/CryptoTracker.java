package spinoza;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javafx.beans.property.*;
import javafx.beans.property.SimpleStringProperty;

public class CryptoTracker extends Transaction {
	private List<Transaction> TransactionDataBase;
	private CryptoCompareAPI cryptoCompareAPI;
	private FIFODeque fIFODeque;
	private double totalGains;
	private double totalLoss;
	private int editAtIndex;
	
	// Constructor
	public CryptoTracker() {
		TransactionDataBase = new ArrayList<Transaction>();
		cryptoCompareAPI = new CryptoCompareAPI();
		fIFODeque = new FIFODeque();
	}

	// add transaction into TransactionsHistory
	public void addTransaction(String date, String exchangeSite, String exchange, String type, double quantity, String rate, String price, String fee, String totalCostPrice, String gainLoss,  String memo, int tradeId) throws ParseException {

		if (!TransactionDataBase.isEmpty()) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm:ss a");
			//System.out.println(date + "   " + time);
			Date newDate = formatter.parse(date); // new date formatted
			Date currentDate = null; // current date "formatted"
			
		    // loop through all elements
		    for (int i = 0; i < getDataBaseSize(); i++) {
		    	// format current date
		    	currentDate = formatter.parse(TransactionDataBase.get(i).getDate() + " " + TransactionDataBase.get(i).getExchangeSite());
		    	// if the element you are looking at is smaller than x, go to the next element
		        if (currentDate.before(newDate))
		        		continue;
		        else
		        	System.out.println("comparing " + newDate.toString() + " with  " + currentDate.toString());
		        System.out.println("comparing real data" + date  + " with  " + TransactionDataBase.get(i).getDate() + " - " + TransactionDataBase.get(i).getExchangeSite());
		        	// otherwise, we have found the location to add x
		        	TransactionDataBase.add(i, new Transaction(date, exchangeSite, exchange, type, quantity, rate, price, fee, totalCostPrice, gainLoss, memo, tradeId));
		        	return;
		    }
		    // we looked through all of the elements, and they were all smaller than x, so we add ax to the end of the list
			TransactionDataBase.add(new Transaction(date, exchangeSite, exchange, type, quantity, rate, price, fee, totalCostPrice, gainLoss, memo, tradeId));
			return;
		}
	    // add first element to database
		TransactionDataBase.add(new Transaction(date, exchangeSite, exchange, type, quantity, rate, price, fee, totalCostPrice, gainLoss, memo, tradeId));
	}
	
	// remove a transaction from the dictionary
	public Transaction removeTransaction(int id) {
		Transaction removed = null;
		
		for (int i=0; i<getDataBaseSize();i++) {
			if (TransactionDataBase.get(i).getTradeId() == id) {
				removed = TransactionDataBase.remove(i);
			}
		}
		return removed;
	}
	
	// compare transactions with cell changes on the JTable
	public boolean checkForChanges(String date, String exchangeSite, String exchange, String type, double quantity, String rate, String price, String fee, String totalCostPrice, String gainLoss, String memo, int tradeId) {
		
		for (int i=0; i<getDataBaseSize();i++) {
			if (TransactionDataBase.get(i).getTradeId() == tradeId) {
				// save index in case they want to save their changes if any
				editAtIndex = i;
				// compare both trades for changes		
				if (!TransactionDataBase.get(i).getDate().equals(date) || 
						!TransactionDataBase.get(i).getExchangeSite().equals(exchangeSite) ||
						!TransactionDataBase.get(i).getExchange().equals(exchange) ||
						!TransactionDataBase.get(i).getType().equals(type) ||
						TransactionDataBase.get(i).getQuantity() != quantity ||
						TransactionDataBase.get(i).getRate() != (rate) ||
						TransactionDataBase.get(i).getPrice() != (price) ||
						TransactionDataBase.get(i).getFee() != (fee) ||
						TransactionDataBase.get(i).getTotalCostPrice() != (totalCostPrice) ||
						TransactionDataBase.get(i).getGainsLoss() != (gainLoss) ||
						!TransactionDataBase.get(i).getMemo().equals(memo) ||
						TransactionDataBase.get(i).getTradeId() != (tradeId)) {
					
					System.out.println("do you want to save changes?");
					System.out.println(tradeId);
					System.out.println(TransactionDataBase.get(i).getTradeId());
					
					return true;
				} else {
					System.out.println("Do Nothing!!");
					return false;
				}
			}
		}
		return false;
	}
	
	public void saveChanges(String date, String exchangeSite, String exchange, String type, double quantity, String rate, String price, String fee, String totalCostPrice, String gainLoss, String memo, int tradeId) {
		// "new" Transaction
		Transaction newTransaction = new Transaction(date, exchangeSite, exchange, type, quantity, rate, price, fee, totalCostPrice, gainLoss, memo, tradeId);
		// replace old transaction with new transaction
		TransactionDataBase.set(editAtIndex,newTransaction);
	}
	
	public int getDataBaseSize() {
		return TransactionDataBase.size();
	}
	
	public int getEditAtIndex() {
		return editAtIndex;
	}
	
	public Transaction getObjectTransaction(int i) {
		return TransactionDataBase.get(i);
	}

	// print database
	public void printDataBase() {
		for (int i=0; i<TransactionDataBase.size(); i++) {
			System.out.println(TransactionDataBase.get(i).toString());
		}
	}
	
	public double getTotalGains() {
		totalGains = 0;
		for (int i = 0; i<TransactionDataBase.size(); i++) {
			if (TransactionDataBase.get(i).getGainLossDouble() > 0) {
				totalGains += TransactionDataBase.get(i).getGainLossDouble();
			}
		}
		return totalGains;
	}
	
	public double getTotalLoss() {
		totalLoss = 0;
		for (int i = 0; i<TransactionDataBase.size(); i++) {
			if (TransactionDataBase.get(i).getGainLossDouble() < 0) {
				totalLoss += TransactionDataBase.get(i).getGainLossDouble();
			}
		}
		return totalLoss;
	}
	
	public double getTotalFees() {
		double totalFees = 0;
		for (int i = 0; i<TransactionDataBase.size(); i++) {
			if (TransactionDataBase.get(i).getFeeDouble() > 0) {
				totalFees += TransactionDataBase.get(i).getFeeDouble();
			}
		}
		return totalFees;
	}
	
	public double getFinalNetGainsLoss() {
		return (totalGains + totalLoss);
	}
	
	/**
	 * Reads file and adds the transaction into table
	 * @param filePath a String with the file path of the file you want to read 
	 * @param arraySet the ArraySet<String> to add content (words) form file
	 * @throws ParseException 
	 */
	public void readFileToDictionary(String filePath) throws Exception {
		
		try {
			// file to read
			Scanner inputFile = new Scanner(new File(filePath));
			// while there is another line in the file
			while (inputFile.hasNextLine()) {
				// get line
				String currentLine = inputFile.nextLine();
				if (!currentLine.equals("")) {
					// split line
					String[] transaction = currentLine.split(",");

					// string to double or int
					double quantity = Double.parseDouble(transaction[4]);
					int id = Integer.parseInt(transaction[11]);
					
					addTransaction(transaction[0], transaction[1], transaction[2], transaction[3], quantity, transaction[5], transaction[6], transaction[7], transaction[8], transaction[9], transaction[10], id);
				}
		}
			// close scanner
			inputFile.close();
			// catch exceptions
		} catch (FileNotFoundException e) {
			System.out.println("Specified File could not be found!");
		}
	}
	
	/**
	 * Reads file and adds the content (words) to a set
	 * @param filePath a String with the file path of the file you want to read -----------------------------------------------------------------------------
	 * @param arraySet the ArraySet<String> to add content (words) form file
	 */
	public void saveDictionaryToFile(String filePath, String date, String exchangeSite, String exchange, String type, double quantity, String rate, String price, String fee, String cost, String gainsLoss, String memo, int id) throws IOException {
		
		try {
			BufferedWriter outputFile = new BufferedWriter(new FileWriter(filePath, true));

			// double to string
			String quantityStr = String.valueOf(quantity);
			String tradeId = String.valueOf(id);
			// @todo see if WE CAN PASS DOUBLES DIRECTLY INTRO STRING WITHOUT CONVERTING IT TO STRING FIRST!
			// format word string
			String myWord = date + "," + exchangeSite + "," +  exchange + "," + type + "," + quantityStr + "," + rate + "," + price + "," + fee + "," + cost + "," + gainsLoss + "," + memo + "," + tradeId;
			// write word to file
			outputFile.newLine(); // select next line
			outputFile.write(myWord); // write word
			
			// close file
			outputFile.close();
			// catch exceptions
		} catch (FileNotFoundException e) {
			System.out.println("Specified File could not be found!");
		}
	}
	
	/**
	 * Synchronizes Dictionary file to "DataBase"
	 * @param filePath a String with the file path of the file you want to read -----------------------------------------------------------------------------
	 * @param arraySet the ArraySet<String> to add content (words) form file
	 */
	public void overwriteDictionaryFile(String filePath) throws IOException {
		//  when a word is removed from the dictionary it is also removed from the database
		// so I just have to make a copy of the database and overwrite the dictionary file.
		try {
			BufferedWriter outputFile = new BufferedWriter(new FileWriter(filePath));
			
			for (int i =0; i< getDataBaseSize(); i++) {
				// current transaction
				Transaction currentTransaction = TransactionDataBase.get(i);
				
				// get fields
				String date = currentTransaction.getDate();
				String exchangeSite = currentTransaction.getExchangeSite();
				String exchange = currentTransaction.getExchange();
				String type = currentTransaction.getType();
				String quantity = String.valueOf(currentTransaction.getQuantity());
				String rate = currentTransaction.getRate();
				String price = currentTransaction.getPrice();
				String fee = currentTransaction.getFee();
				String cost = currentTransaction.getTotalCostPrice();
				String gainsLoss = currentTransaction.getGainsLoss();
				String memo = currentTransaction.getMemo();
				String id = String.valueOf(currentTransaction.getTradeId());
				
				// format transaction properties to string
				String myWord = date + "," + exchangeSite + "," + exchange + "," + type + "," + quantity + "," + rate + "," + price + "," + fee + "," + cost + "," + gainsLoss + "," + memo + "," + id;
				
				// write the word string
				outputFile.newLine(); // select next line
				outputFile.write(myWord); // write word
			}
			
			// close file
			outputFile.close();
			// catch exceptions
		} catch (FileNotFoundException e) {
			System.out.println("Specified File could not be found!");
		}
	}
	
	/**
	 * Reads file and adds the content (words) to a set
	 * @param filePath a String with the file path of the file you want to read 
	 * @param arraySet the ArraySet<String> to add content (words) form file
	 */
	public  ArrayList<String> readCoinsFileToSet(String filePath) throws IOException {
		ArrayList<String> coins = new ArrayList<>();
		
		try {
			// file to read
			Scanner inputFile = new Scanner(new File(filePath));
			// while there is another line in the file
			while (inputFile.hasNextLine()) {
				// add words to set
				coins.add(inputFile.nextLine());
		}
			// close scanner
			inputFile.close();
			// catch exceptions
		} catch (FileNotFoundException e) {
			System.out.println("Specified File could not be found!");
		}
		return coins;
	}
	
	// PORTFOLIO
	public class Portfolio {
		private SimpleStringProperty cryptocurrency;
		private SimpleDoubleProperty price;
		private SimpleDoubleProperty holding;
		private SimpleDoubleProperty valuation;
		private double totalCurrentValuation;
		
		// default constructor
		public Portfolio() {
		}
		
		// parameterized constructor
		public Portfolio(String cryptocurrency, double price, double holding, double valuation) {
			this.cryptocurrency = new SimpleStringProperty(cryptocurrency);
			this.price = new SimpleDoubleProperty(price);
			this.holding = new SimpleDoubleProperty(holding);
			this.valuation = new SimpleDoubleProperty(valuation);
		}
		
		// setters
		public void setCryptocurrency(String cryptocurrency) {	
			this.cryptocurrency.set(cryptocurrency);
		}
		
		public void setPrice(double price) {
			this.price.set(price);
		}
		
		public void setHolding(double holding) {
			this.holding.set(holding);
		}
		
		public void setValuation(double valuation) {
			this.valuation.set(valuation);
		}
		
		public void setTotalCurrentValuation(double totalCurrentValuation) {
			this.totalCurrentValuation = totalCurrentValuation;
		}
		
		// getters
		
		public String getCryptocurrency() {
			return cryptocurrency.get();
		}
		
		public double getPrice() {
			return price.get();
		}
		
		public double getHolding() {
			return holding.get();
		}
		
		public double getValuation() {
			return valuation.get();
		}
		
		public double getTotalCurrentValuation() {
			return totalCurrentValuation;
		}
		
		
	} // end portfolio
	
	
	public CryptoCompareAPI getCryptoCompareAPI() {
		return cryptoCompareAPI;
	}
	
	public FIFODeque getFIFODeque() {
		return fIFODeque;
	}
	
} // end class
