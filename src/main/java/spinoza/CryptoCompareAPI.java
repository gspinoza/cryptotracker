package spinoza;

/*******************************************
 * File Name: CryptoCompareAPI.java
 * Purpose: get live prices from CryptoCompare.com
 * Programmer: Gabriel.Espinoza
 * Last Update: 02/15/2020
 * 
*******************************************/

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CryptoCompareAPI {
	private double coinPrice;
	private  String URL;
	
	public double getCoinPrice(String coin) throws IOException {
		
		if (coin.equals("BTC") || coin.equals("ETH") ) {
			URL = "https://min-api.cryptocompare.com/data/price?fsym=" + coin + "&tsyms=USD&e=Gemini"; //URLEncoder.encode(Search);	
		} else {
				URL = "https://min-api.cryptocompare.com/data/price?fsym=" + coin + "&tsyms=USD&e=Bittrex"; 
		} 
		
	    // Connect to the URL using java's native library
	    URL url = new URL(URL);
	    HttpURLConnection request = (HttpURLConnection) url.openConnection();
	    request.connect();

	    // Convert to a JSON object to print data
	    JsonParser jparser = new JsonParser(); //from json
	    try {
	    		JsonElement jelement = jparser.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
	    
	    		if (jelement.toString().contains("Error")) { // if coin not available on gemini or bittrex use average from cryptocompare
	    			url = new URL("https://min-api.cryptocompare.com/data/price?fsym=" + coin + "&tsyms=USD");
	    			request = (HttpURLConnection) url.openConnection();
	    			request.connect();
	    			jelement = jparser.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
	    		}
	    		
	    		JsonObject jobject = jelement.getAsJsonObject();
	    		//System.out.println(jobject.get("USD").toString());
	    		coinPrice = Double.parseDouble(jobject.get("USD").toString());
	    
	    } catch(IOException e){
	    		JOptionPane.showMessageDialog(null, "No Data Found!");
	    }
	    
	    return coinPrice;
	} // end main method

	
} // end class
