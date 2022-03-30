package spinoza;

import spinoza.CryptoTracker.Portfolio; // portfolio nested class from CryptoTracker
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import com.jfoenix.controls.JFXToggleButton;
import com.sun.javafx.charts.Legend;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

public class Controller implements Initializable {
	private CryptoTracker theModel;
	
	@FXML private AnchorPane MainAnchorPane;
	@FXML private BorderPane borderPanePortfolio, borderPaneTransactions;
	@FXML private VBox vBoxSettings;
	@FXML private HBox hBoxRow1, hBoxInnerRow;
	@FXML private JFXButton fxButtonPortfolio, fxButtonTransactions, fxButtonSettings;
	
	// Portfolio Page
	@FXML private TableView<Portfolio> portfolioTable;
	@FXML private TableColumn<Portfolio, String> columnPortfolioCoin;
	@FXML private TableColumn<Portfolio, Double> columnPortfolioPrice, columnPortfolioHoldings, columnPortfolioValuation;
	@FXML private ObservableList<Portfolio> portfolioObservableList;
	@FXML private HBox hBoxRing;
    private ObservableList<PieChart.Data> pieChartData;
    private RingChart pieChart;
    
	// Transactions Page
	@FXML private JFXButton fxButtonBuy, fxButtonCancelBuy, fxButtonSaveBuy;
	@FXML private JFXButton fxButtonSell, fxButtonCancelSell, fxButtonSaveSell;
	@FXML private JFXButton fxButtonTrade, fxButtonCancelTrade, fxButtonSaveTrade;
	@FXML private Label label1, label2, label3;
	@FXML private JFXTimePicker fxTimePciker;
	@FXML private DialogPane dialogPaneBuy;
	
	// table
	@FXML private TableView<Transaction> transactionsTable;
	@FXML private TableColumn<Transaction, String> tableColumnDate, tableColumnExchangeSite, tableColumnExchange, tableColumnType, tableColumnRate, tableColumnSubTotal, tableColumnFee, tableColumnTotal, tableColumnGainLoss, tableColumnMemo;
	@FXML private TableColumn<Transaction, Double> tableColumnQuantity;
	@FXML private TableColumn<Transaction, Integer> tableColumnTradeId;
	@FXML private TableColumn<Transaction, Transaction> tableColumnHoldings;
	@FXML private TableColumn<Transaction, Transaction> tableColumnLivePrice;
	@FXML private TableColumn<Transaction, Transaction> tableColumnAction;
	@FXML private ObservableList<Transaction> MYObservableList;
	// buy sell trade
	@FXML private HBox hBoxBuyDialog, hBoxSellDialog, hBoxTradeDialog;
	
	// buying, selling, trade fields
	@FXML private JFXDatePicker fxDatePickerBuyDate, fxDatePickerSellDate, fxDatePickerTradeDate;
	@FXML private JFXComboBox<String> fxComboBoxCoinsBuy, fxComboBoxCoinsSell, fxComboBoxCoinsTradeBuy, fxComboBoxCoinsTradeSell;
	@FXML private JFXTextArea fxTextAreaBuyMemo, fxTextAreaSellMemo, fxTextAreaTradeMemo;
	// buying Fields
	@FXML private JFXTextField fxTextFieldBuyTime, fxTextFieldBuyQuantity, fxTextFieldBuyRate, fxTextFieldBuyPrice, fxTextFieldBuyFee, fxTextFieldBuyOverallCost;
	// selling Fields
	@FXML private JFXTextField fxTextFieldSellTime, fxTextFieldSellQuantity, fxTextFieldSellRate, fxTextFieldSellPrice, fxTextFieldSellFee, fxTextFieldSellOverallCost;
	// trading Fields
	@FXML private JFXTextField fxTextFieldTradeTime, fxTextFieldTradeQuantityBuying, fxTextFieldTradeQuantitySelling, fxTextFieldTradeRateBuying, fxTextFieldTradeRateSelling;
	@FXML private JFXTextField fxTextFieldTradePrice, fxTextFieldTradeFee, fxTextFieldTradeOverallCost;
	// Bottom Bar
	@FXML private Label totalTransactionsLabel, totalGainsLabel, totalLossesLabel, totalFeesLabel, FinalNetGainsLossesLabel;
	// Settings Page
	@FXML private JFXToggleButton alwaysOnTopButton;
	Thread taskThread;
	int counter;

	ObservableList<String> cbCoins;
	private ArrayList<String> arrayCoins;
	HashMap<String,String> dataColors;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
		// initialize the Model
	 	this.theModel = new CryptoTracker(); 
	

		try { // read transactions and coins from file
			arrayCoins = theModel.readCoinsFileToSet("resources/Coins.coinTracker");
    		cbCoins = FXCollections.observableArrayList(arrayCoins);

    		theModel.readFileToDictionary("resources/TransactionsDemo.coinTracker");
		} catch (Exception e) { e.printStackTrace(); }
		
		// set ComboBox values
		fxComboBoxCoinsBuy.setItems(cbCoins);
		fxComboBoxCoinsSell.setItems(cbCoins);
		fxComboBoxCoinsTradeBuy.setItems(cbCoins);
		fxComboBoxCoinsTradeSell.setItems(cbCoins);
    		
    	// initial state of panels
		hBoxBuyDialog.setVisible(false);
		hBoxBuyDialog.toFront();
		hBoxSellDialog.setVisible(false);
		hBoxSellDialog.toFront();
		hBoxTradeDialog.setVisible(false);
		hBoxTradeDialog.toFront();
		
		// initialize observable list to hold data for the table
		portfolioObservableList = FXCollections.observableArrayList();
		// initialize observableArrayList
        pieChartData = FXCollections.observableArrayList();
        
        addColorStylesToHashMap();
        
		//
		setUpTableColumns();
		
		// add data to table
		addDataToTable();
		
		//set BottomBar values: gains, losses, etc...
		setBottomBarValues();
		
		// set up ring chart
		setUpRingChart();
		
		// set up PortFolio tables columns
		setUpPortfolioTableColumns();
		
		// add data to PortFolio table
		try { addDataToPortfolioTable(); } catch (IOException e) { e.printStackTrace(); }
		
		// pass observable list to table for displaying
		portfolioTable.setItems(portfolioObservableList);
        // pass observable list to PieChart
        pieChart.AddValues(pieChartData);
        //pieChart.setData(pieChartData);
        
        // set custom pie colors
        setLegendPieColors();
        
		// Refresh All PortFolio Data
		// wait 2 sec to start thread
		new java.util.Timer().schedule( 
			new java.util.TimerTask() {
				@Override
				public void run() {
					//reFreshPortFolioData(); //2019
				}
			}, 
			2000 
		);
		
		// temporary placed here: note this has to be executed after data is already added
		transactionsTable.setRowFactory(row -> new TableRow<Transaction>(){
		    @Override
		    public void updateItem(Transaction transaction, boolean empty){
		        super.updateItem(transaction, empty);

		        if (transaction == null || empty) {
		        		setStyle("");
		        		//setStyle("-fx-background-color: white");
		        		//setStyle("-fx-background-color: "+ java.awt.Color.decode("#1f2839"));
		        } else {
		            //Now 'item' has all the info of the Person in this row
		            if (transaction.getType().equals("BUY")) {
		            	this.setTextFill(Color.GREEN);
		            	//We apply now the changes in all the cells of the row
		            	setStyle("-fx-control-inner-background: #0dc700;");  //darker #0cb300
		            }
		            else if (transaction.getType().equals("SELL")) {
		            	this.setTextFill(Color.RED);
		            	//We apply now the changes in all the cells of the row
		            	setStyle("-fx-control-inner-background: #fd2722;");        
		            }
		            else {
		            	//We apply now the changes in all the cells of the row
		            	setStyle("-fx-control-inner-background: #0077c0;");         
		            }
		        }
		    }
		});
		
		// note this has to be executed after data is already added
	    //portfolioTable.setStyle("-fx-control-inner-background: black;");
		
    } // end initilizer
    
	
	@SuppressWarnings("deprecation")
	@FXML // Handle All Button Actions
	private void handleButtonAction(ActionEvent event) throws ParseException {
		if (event.getSource() == fxButtonPortfolio) {
			fxButtonPortfolio.setStyle("-fx-background-color:#696969;");
			fxButtonTransactions.setStyle(null);
			fxButtonSettings.setStyle(null);
			//borderPanePortfolio.setVisible(true);
			borderPanePortfolio.toFront();
			// wait 2 sec then resume thread
			new java.util.Timer().schedule( 
				new java.util.TimerTask() {
					@Override
					public void run() {
						taskThread.resume();
					}
				}, 
				1000 
			);
			
			//borderPaneTransactions.setVisible(false);
		} 
		else if (event.getSource() == fxButtonTransactions) {
			fxButtonTransactions.setStyle("-fx-background-color:#696969;");
			fxButtonPortfolio.setStyle(null);
			fxButtonSettings.setStyle(null);
			borderPaneTransactions.toFront();
			taskThread.suspend();
			//borderPaneTransactions.setVisible(true);
			//borderPanePortfolio.setVisible(false);
		} 
		else if (event.getSource() == fxButtonSettings) {
			fxButtonSettings.setStyle("-fx-background-color:#696969;");
			fxButtonPortfolio.setStyle(null);
			fxButtonTransactions.setStyle(null);
			vBoxSettings.toFront();
			taskThread.suspend();
		}
		else if (event.getSource() == fxButtonBuy) {
			hBoxBuyDialog.setVisible(true);
			hBoxSellDialog.setVisible(false);
			hBoxTradeDialog.setVisible(false);
			hBoxBuyDialog.toFront();
		}
		else if (event.getSource() == fxButtonCancelBuy) {
			// clear content and disable visibility
			hBoxBuyDialog.setVisible(false);
		}
		else if (event.getSource() == fxButtonSaveBuy) {
			// save content then clear content then disable visibility
			saveTransaction("BUY");
			hBoxBuyDialog.setVisible(false);
		}
		else if (event.getSource() == fxButtonSell) {
			hBoxSellDialog.setVisible(true);
			hBoxBuyDialog.setVisible(false);
			hBoxTradeDialog.setVisible(false);
			hBoxSellDialog.toFront();
		}
		else if (event.getSource() == fxButtonCancelSell) {
			// clear content the disable visibility
			hBoxSellDialog.setVisible(false);
		}
		else if (event.getSource() == fxButtonSaveSell) {
			// save content then clear content then disable visibility
			saveTransaction("SELL");
			hBoxSellDialog.setVisible(false);
		}
		else if (event.getSource() == fxButtonTrade) {
			hBoxTradeDialog.setVisible(true);
			hBoxBuyDialog.setVisible(false);
			hBoxSellDialog.setVisible(false);
			hBoxTradeDialog.toFront();
		}
		else if (event.getSource() == fxButtonCancelTrade) {
			// clear content the disable visibility
			hBoxTradeDialog.setVisible(false);
		}
		else if (event.getSource() == fxButtonSaveTrade) {
			// save content then clear content then disable visibility
			saveTransaction("TRADE");
			hBoxTradeDialog.setVisible(false);
		} 
		else if (event.getSource() == alwaysOnTopButton) {
			// Toggle on off
			if (alwaysOnTopButton.isSelected())
				Main.getStage().setAlwaysOnTop(true);
			else 
				Main.getStage().setAlwaysOnTop(false);
		}
		
	} // end handle button
	
	public void addDataToTable() {
		
		// initialize observable list to hold data for the table
		MYObservableList = FXCollections.observableArrayList();

		// reset deque, so when dynamically updating table to buy don't adds the quantity to the front of the queue 
		theModel.getFIFODeque().resetDeuqe();
		
		// get info from dictionary and update the table or view
		for (int i = 0; i<theModel.getDataBaseSize(); i++) {
			
			// add transactions to observable list NOTE: each object we pass represent a new row!
			MYObservableList.add(0, theModel.getObjectTransaction(i)); // 0 parameter adds it to the top
			
			// get raw fields for gains/loss and holdings calculations
			String type = theModel.getObjectTransaction(i).getType();
			
			// determine selling and buying fields
			theModel.getObjectTransaction(i).determineBuyingSellingCoin(arrayCoins);
			String coinBuying = theModel.getObjectTransaction(i).getBuyingCoin();
			String coinSelling = theModel.getObjectTransaction(i).getSellingCoin();
			double quantityBuying = theModel.getObjectTransaction(i).getQuantityBuying();
			double rateBuying = theModel.getObjectTransaction(i).getRateDoubleBuying();
			String gainsLoss = theModel.getObjectTransaction(i).getGainsLoss();
			double price = theModel.getObjectTransaction(i).getSubTotalDouble();
			int id = theModel.getObjectTransaction(i).getTradeId();
			double sellingQuantity = theModel.getObjectTransaction(i).getQuantitySelling();
			double sellingRate = theModel.getObjectTransaction(i).getRateDoubleSelling();
			
			// validate/check that transaction has correct gainloss and redo getGainLossAndFillDeque
			String gainsLossRecheck = theModel.getFIFODeque().getGainLossAndFillDeque(coinBuying, type, quantityBuying, rateBuying, price, id, coinSelling, sellingQuantity, sellingRate);
			if (gainsLoss.equals(gainsLossRecheck)){
				System.out.println(gainsLossRecheck + "looks GOOD");
			} else {
				System.out.println("this transaction looks BAD");
				theModel.getObjectTransaction(i).setTotalGainsLoss(gainsLossRecheck);
			}
			
			//double currentHoldings = FIFODeque.getcurrentHoldings(coin, id);
			
		}
		// pass observable list to table for displaying
		System.out.println(theModel.getObjectTransaction(0).toString()+"----------------------");
		transactionsTable.setItems(MYObservableList);
	}
	
	//set BottomBar values: gains, losses, etc...
	public void setBottomBarValues() {
		int totalTransactions = theModel.getDataBaseSize();
		double totalGains = theModel.getTotalGains();
		double totalLosses = theModel.getTotalLoss();
		double totalFees = theModel.getTotalFees();
		double finalGainsLosses = theModel.getFinalNetGainsLoss();
		
		totalTransactionsLabel.setText(Integer.toString(totalTransactions));
		totalGainsLabel.setText(NumberFormat.getCurrencyInstance(Locale.CANADA).format(totalGains));
		totalLossesLabel.setText(NumberFormat.getCurrencyInstance(Locale.CANADA).format(totalLosses));
		totalFeesLabel.setText(NumberFormat.getCurrencyInstance(Locale.CANADA).format(totalFees));
		FinalNetGainsLossesLabel.setText(NumberFormat.getCurrencyInstance(Locale.CANADA).format(finalGainsLosses));
		
		if (totalGains > 0) {
			totalGainsLabel.setTextFill(Color.GREEN.brighter());
		}
		if (totalLosses < 0) {
			totalLossesLabel.setTextFill(Color.RED.brighter());
    		}
		if (totalFees > 0) {
			totalFeesLabel.setTextFill(Color.RED.brighter());
    		}
    		if (finalGainsLosses > 0) {
    			FinalNetGainsLossesLabel.setTextFill(Color.CYAN);
    		}
    		// TODO# add total fees to total gain and loss
    		if (finalGainsLosses < 0) {
    			FinalNetGainsLossesLabel.setTextFill(Color.RED.brighter());
    		}
	}
	
	
	
	
	//HEADER
	 private void makeHeaderWrappable(TableColumn col) {
		    Label label = new Label(col.getText());
		    label.setStyle("-fx-padding: 8px;");
		    label.setWrapText(true);
		    label.setAlignment(Pos.CENTER);
		    label.setTextAlignment(TextAlignment.CENTER);

		    StackPane stack = new StackPane();
		    stack.getChildren().add(label);
		    stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
		    label.prefWidthProperty().bind(stack.prefWidthProperty());
		    col.setText(null);
		    col.setGraphic(stack);
		  }
	 
	// Set up columns associate data with columns
	public void setUpTableColumns()  {
		// set table editable
		transactionsTable.setEditable(true);
		
		makeHeaderWrappable(tableColumnExchangeSite);
		makeHeaderWrappable(tableColumnHoldings);
		makeHeaderWrappable(tableColumnLivePrice);
		
		// DATE
		tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		tableColumnDate.setCellFactory(TextFieldTableCell.forTableColumn()); // TextField for input
		//tableColumnDate.getCellData(theModel);
		tableColumnDate.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			// get info from cell
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newDate = event.getNewValue(); // get new entry
			
			// ask user if they want to save changes
		    Alert alert = new Alert(AlertType.CONFIRMATION);
		    alert.setTitle("Confirmation Dialog");
		    alert.setHeaderText("Edit Transaction");
		    alert.setContentText("Are you sure you want to edit this transaction?");
		    
		    Optional<ButtonType> result = alert.showAndWait();
		    if (result.get() == ButtonType.OK){ // OK
				event.getTableView().getItems().get(row).setDate(newDate); /// save newEntry
				// refresh table
				addDataToTable();
		    		// Synchronize DictionaryFile >< DataBase
		    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		    } else { // CANCEL
		        // ... do nothing..closes dialog
		    }
		});
		 
		// TIME
		tableColumnExchangeSite.setCellValueFactory(new PropertyValueFactory<>("exchangeSite"));
		tableColumnExchangeSite.setCellFactory(TextFieldTableCell.forTableColumn()); // TextField for input
		tableColumnExchangeSite.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newExchangeSite = event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setExchangeSite(newExchangeSite); /// save newEntry
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// EXCHANGE
		tableColumnExchange.setCellValueFactory(new PropertyValueFactory<>("exchange"));
		tableColumnExchange.setCellFactory(ComboBoxTableCell.forTableColumn(cbCoins)); // ComboBox for input
		tableColumnExchange.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newType = event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setType(newType); /// save newEntry
			// refresh table (re-add data to table) maybe not good for performance, but saves a lot of code, 
			// as it re-calculates all transactions, instead of "manually" updating each row "transaction" when setting new values
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}

		});
		
		// TYPE
		tableColumnType.setCellValueFactory(new PropertyValueFactory<>("type"));
		tableColumnType.setCellFactory(ComboBoxTableCell.forTableColumn("BUY", "SELL")); // TextField for input
		tableColumnType.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newType = event.getNewValue(); // get new entry
			
			//double newQuantity = Double.parseDouble(event.getNewValue()); // get new entry
			
			event.getTableView().getItems().get(row).setType(newType); /// save newEntry
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// QUANTITY
		tableColumnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumnQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter())); // TextField for input
		tableColumnQuantity.setOnEditCommit((CellEditEvent<Transaction, Double> event) -> { //Action
			TablePosition<Transaction, Double> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			double newQuantity = event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setQuantity(newQuantity); /// save newEntry
			// refresh table
			addDataToTable();
    			// Synchronize DictionaryFile >< DataBase
    			try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// RATE
		tableColumnRate.setCellValueFactory(new PropertyValueFactory<>("rate"));
		// tableColumnRate.setCellFactory(TextFieldTableCell.forTableColumn()); // TextField for input
		tableColumnRate.setCellFactory(tv -> new TableCell<Transaction, String>() {

		    private final VBox lines; {
		        lines = new VBox();
		        lines.getStyleClass().add("address");
		        setGraphic(lines);
		    }

		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        lines.getChildren().clear();
		        if (!empty && item != null) {
		            int lineNo = 1;
		            // Been LAZY, switching values for displaying only
		            //String tmpStr[] = item.split("~");  
		            //item = tmpStr[1] + "~" + tmpStr[0];
		            
		            for (String line : item.split("~")) {
		                Text text = new Text(line);
		                text.getStyleClass().add("line-" + (lineNo++));
		                lines.getChildren().add(text);
		            }
		        }
		    }

		});
		
		tableColumnRate.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newRate = event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setRate(newRate); /// save newEntry
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
        
		
		// PRICE
		tableColumnSubTotal.setCellValueFactory(new PropertyValueFactory<>("price"));
		//tableColumnPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter())); // TextField for input
		//tableColumnSubTotal.setCellFactory(TextFieldTableCell.forTableColumn()); // TextField for input
		tableColumnSubTotal.setCellFactory(tv -> new TableCell<Transaction, String>() {

		    private final VBox lines; {
		        lines = new VBox();
		        lines.getStyleClass().add("address");
		        setGraphic(lines);
		    }

		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        lines.getChildren().clear();
		        if (!empty && item != null) {
		            int lineNo = 1;
		            // Been LAZY, switching values for displaying only
		            //String tmpStr[] = item.split("~");  
		            //item = tmpStr[1] + "~" + tmpStr[0];
		            
		            for (String line : item.split("~")) {
		                Text text = new Text(line);
		                text.getStyleClass().add("line-" + (lineNo++));
		                lines.getChildren().add(text);
		            }
		        }
		    }
		});
		
		tableColumnSubTotal.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newPrice = event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setPrice(newPrice); /// save newEntry
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// FEE
		tableColumnFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
		//tableColumnFee.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter())); // TextField for input
		//tableColumnFee.setCellFactory(TextFieldTableCell.forTableColumn()); // TextField for input
		tableColumnFee.setCellFactory(tv -> new TableCell<Transaction, String>() {

		    private final VBox lines; {
		        lines = new VBox();
		        lines.getStyleClass().add("address");
		        setGraphic(lines);
		    }

		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        lines.getChildren().clear();
		        if (!empty && item != null) {
		            int lineNo = 1;
		            // Been LAZY, switching values for displaying only
		            //String tmpStr[] = item.split("~");  
		            //item = tmpStr[1] + "~" + tmpStr[0];
		            
		            for (String line : item.split("~")) {
		                Text text = new Text(line);
		                text.getStyleClass().add("line-" + (lineNo++));
		                lines.getChildren().add(text);
		            }
		        }
		    }

		});
		
		tableColumnFee.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newFee= event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setFee(newFee); /// save newEntry
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// OVERALL COST
		tableColumnTotal.setCellValueFactory(new PropertyValueFactory<>("totalCostPrice"));
		//tableColumnOverall.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter())); // TextField for input
		//tableColumnTotal.setCellFactory(TextFieldTableCell.forTableColumn()); // TextField for input
		tableColumnTotal.setCellFactory(tv -> new TableCell<Transaction, String>() {

		    private final VBox lines; {
		        lines = new VBox();
		        lines.getStyleClass().add("address");
		        setGraphic(lines);
		    }

		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        lines.getChildren().clear();
		        if (!empty && item != null) {
		            int lineNo = 1;
		            // Been LAZY, switching values for displaying only
		            //String tmpStr[] = item.split("~");  
		            //item = tmpStr[1] + "~" + tmpStr[0];
		            
		            for (String line : item.split("~")) {
		                Text text = new Text(line);
		                text.getStyleClass().add("line-" + (lineNo++));
		                lines.getChildren().add(text);
		            }
		        }
		    }
		});
		tableColumnTotal.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newOverallCost = event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setTotalCostPrice(newOverallCost); /// save newEntry
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// GAINS/LOSSES
		tableColumnGainLoss.setCellValueFactory(new PropertyValueFactory<>("gainsLoss"));
		//tableColumnGainLoss.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter())); // TextField for input
		//tableColumnGainLoss.setCellFactory(tableC -> new CurrencyCell<>());
		tableColumnGainLoss.setCellFactory(tv -> new TableCell<Transaction, String>() {

		    private final VBox lines; {
		        lines = new VBox();
		        lines.getStyleClass().add("address");
		        setGraphic(lines);
		    }

		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        lines.getChildren().clear();
		        if (!empty && item != null) {
		            int lineNo = 1;
		            // Been LAZY, switching values for displaying only
		            //String tmpStr[] = item.split("~");  
		            //item = tmpStr[1] + "~" + tmpStr[0];
		            
		            for (String line : item.split("~")) {
		                Text text = new Text(line);
		                text.getStyleClass().add("line-" + (lineNo++));
		                lines.getChildren().add(text);
		            }
		        }
		    }
		});
		tableColumnGainLoss.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newGainsLoss = event.getNewValue();// get new entry
			
			event.getTableView().getItems().get(row).setTotalGainsLoss(newGainsLoss); /// save newEntry
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// MEMO
		tableColumnMemo.setCellValueFactory(new PropertyValueFactory<>("memo"));
		tableColumnMemo.setCellFactory(TextFieldTableCell.forTableColumn()); // TextField for input
		tableColumnMemo.setOnEditCommit((CellEditEvent<Transaction, String> event) -> { //Action
			TablePosition<Transaction, String> 
			position = event.getTablePosition(); // get position
			int row = position.getRow(); // get row
			String newMemo = event.getNewValue(); // get new entry
			
			event.getTableView().getItems().get(row).setMemo(newMemo); /// save newEntry
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		});
		
		// TRADE ID
		tableColumnTradeId.setCellValueFactory(new PropertyValueFactory<>("tradeId"));
		
		// SPECIAL COLUMNS --------
		
		// HOLDINGS
		tableColumnHoldings.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnHoldings.setCellFactory(param -> new TableCell<Transaction, Transaction>() {
			double currentHoldings = 0;
		    @Override
		    protected void updateItem(Transaction Transaction, boolean empty) {
		        super.updateItem(Transaction, empty);
		        if (Transaction == null) {
		        	setText(null);
		            return;
		        } else {
		        	String coinBuying = Transaction.getBuyingCoin();
		        	currentHoldings = theModel.getFIFODeque().getCurrentHoldingsTrans(coinBuying, Transaction.getTradeId());
		        	setText(String.valueOf(currentHoldings));
		        }

		    }
		});
		
		// LIVE PRICE
		tableColumnLivePrice.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnLivePrice.setCellFactory(param -> new TableCell<Transaction, Transaction>() {
			double LivePrice = 0;
		    @Override
		    protected void updateItem(Transaction Transaction, boolean empty) {
		        super.updateItem(Transaction, empty);
		        if (Transaction == null) {
		        	setText(null);
		            return;
		        } else {
		        	String coinBuying = Transaction.getBuyingCoin();
		        	String theCoin = Transaction.getBuyingCoin().replaceAll(".*\\(", "").replaceAll("\\)", "");
		        	double holding =  theModel.getFIFODeque().getCurrentHoldingsTrans(coinBuying, Transaction.getTradeId());
		        	double price = 0;
		        	System.out.println(holding+"FFF");
				try { price = theModel.getCryptoCompareAPI().getCoinPrice(theCoin);
				} catch (IOException e) { e.printStackTrace(); }
		        	LivePrice = holding * price;
		        	setText(NumberFormat.getCurrencyInstance(Locale.CANADA).format(LivePrice));
		        	//this.setTextFill(Color.BLUE); // only works if we remove the style on style.css
		        }
		    }
		});
		
		// ACTION
		tableColumnAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnAction.setCellFactory(param -> new TableCell<Transaction, Transaction>() {
			private final Button deleteButton = new Button("Delete");
		    @Override
		    protected void updateItem(Transaction Transaction, boolean empty) {
		        super.updateItem(Transaction, empty);
		        if (Transaction == null) {
		            setGraphic(null);
		            return;
		        } else {
		        		setGraphic(deleteButton);
		        }
		        //deleteButton.setOnAction(event -> getTableView().getItems().remove(Transaction));
		        deleteButton.setOnAction(event -> {
		        	
	    			// ask user if they want to delete transaction
	    		    Alert alert = new Alert(AlertType.CONFIRMATION);
	    		    alert.setTitle("Confirmation Dialog");
	    		    alert.setHeaderText("Delete Transaction");
	    		    alert.setContentText("Are you sure you want to permanently delete this transaction?");
	    		    
	    		    // optional code: show dialog on top when APP has always on top enabled too
	    		    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
	    		    stage.setAlwaysOnTop(true);

	    		    Optional<ButtonType> result = alert.showAndWait();
	    		    if (result.get() == ButtonType.OK){ // OK
		        		MYObservableList.remove(Transaction);
		        		theModel.removeTransaction(Transaction.getTradeId());
		        		
			    		// Synchronize DictionaryFile >< DataBase
			    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		        		
		        		// refresh table
		        		addDataToTable();
		        		
		        		// reset PieChart ObseravableList
		        		pieChartData.clear();
		        		
		        		try { addDataToPortfolioTable(); } catch (IOException e) { e.printStackTrace(); }
	    		    } else { // CANCEL
	    		        // ... do nothing..closes dialog
	    		    }
		        });
		        
		    }
		});
		
	} // end set up columns 
	
	public void saveTransaction(String type) throws ParseException {
		// generating random trade id
		Random rand = new Random();
		int  id = rand.nextInt(999999999) + 1111111111;
		//50 is the maximum and the 1 is our minimum 
		
		// time formatter
		DateTimeFormatter Dateformatter = DateTimeFormatter.ofPattern("MM/dd/yy");
		
		if (type.equals("BUY")) {
			
			// get buy fields
			String date = Dateformatter.format(fxDatePickerBuyDate.getValue());
			System.out.println(date);
			String time = fxTextFieldBuyTime.getText(); // convert from date to string
			String coin = fxComboBoxCoinsBuy.getValue();
			double quantity = Double.parseDouble(fxTextFieldBuyQuantity.getText());
			double rate = Double.parseDouble(fxTextFieldBuyRate.getText());
			double price = Double.parseDouble(fxTextFieldBuyPrice.getText());
			double fee = Double.parseDouble(fxTextFieldBuyFee.getText());
			double totalCostPrice = Double.parseDouble(fxTextFieldBuyOverallCost.getText());
			String memo = fxTextAreaBuyMemo.getText();
			
			// set Gains and Losses depending on the type transaction
			//double gainLoss = theModel.getFIFODeque().getGainLossAndFillDeque(coin, type, quantity, rate, price, id, "$USD", sellingQuantity, sellingRate);

			// save new transaction
			//theModel.addTransaction(date, time, type, coin, quantity, rate, "$USD", price, fee, totalCostPrice, gainLoss, memo, id);
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
			
		} else if(type.equals("SELL")) {
			
			// get sell fields
			String date = Dateformatter.format(fxDatePickerBuyDate.getValue());
			String time = fxTextFieldSellTime.getText(); // convert from date to string
			String coin = fxComboBoxCoinsSell.getValue();
			double quantity = Double.parseDouble(fxTextFieldSellQuantity.getText());
			double rate = Double.parseDouble(fxTextFieldSellRate.getText());
			double price = Double.parseDouble(fxTextFieldSellPrice.getText());
			double fee = Double.parseDouble(fxTextFieldSellFee.getText());
			double totalCostPrice = Double.parseDouble(fxTextFieldSellOverallCost.getText());
			String memo = fxTextAreaSellMemo.getText();
			
			// set Gains and Losses depending on the type transaction
			//@ -  double gainLoss = theModel.getFIFODeque().getGainLossAndFillDeque(coin, type, quantity, rate, price, id, "$USD", sellingQuantity, sellingRate);
			
			// save new transaction
			//@ -  theModel.addTransaction(date, time, type, coin, quantity, rate, "$USD", price, fee, totalCostPrice, gainLoss, memo, id);
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}

		} else {
			
			// get trade fields
			String date = Dateformatter.format(fxDatePickerTradeDate.getValue());
			String time = fxTextFieldTradeTime.getText(); // convert from date to string
			String coinBuying = fxComboBoxCoinsTradeBuy.getValue();
			String coinSelling = fxComboBoxCoinsTradeSell.getValue();
			double quantityBuying = Double.parseDouble(fxTextFieldTradeQuantityBuying.getText());
			double quantitySelling = Double.parseDouble(fxTextFieldTradeQuantitySelling.getText());
			double rateBuying = Double.parseDouble(fxTextFieldTradeRateBuying.getText());
			double rateSelling = Double.parseDouble(fxTextFieldTradeRateSelling.getText());
			
			double price = Double.parseDouble(fxTextFieldTradePrice.getText());
			double fee = Double.parseDouble(fxTextFieldTradeFee.getText());
			double totalCostPrice = Double.parseDouble(fxTextFieldTradeOverallCost.getText());
			String memo = fxTextAreaTradeMemo.getText();
			
			String selling  = quantitySelling + " " + coinSelling + " @ $" + rateSelling;
			// set Gains and Losses depending on the type transaction
			//@ -  double gainLoss = theModel.getFIFODeque().getGainLossAndFillDeque(coinBuying, type, quantityBuying, rateBuying, price, selling, id);
			
			
			// save new transaction
			//@ -  theModel.addTransaction(date, time, type, coinBuying, quantityBuying, rateBuying, selling, price, fee, totalCostPrice, gainLoss, memo, id);
			// refresh table
			addDataToTable();
	    		// Synchronize DictionaryFile >< DataBase
	    		try {theModel.overwriteDictionaryFile("TransactionsDemo.coinTracker");} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	// Set up columns and associate data with columns
	public void setUpPortfolioTableColumns()  {
		// set table editable
		portfolioTable.setEditable(false);
		
		// CRYPTOCURRENCY
		columnPortfolioCoin.setCellValueFactory(new PropertyValueFactory<>("cryptocurrency"));
		// CURRENT PRICE
		columnPortfolioPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		columnPortfolioPrice.setCellFactory(tableC -> new CurrencyCell<>());
		// HOLDINGS
		columnPortfolioHoldings.setCellValueFactory(new PropertyValueFactory<>("holding"));
		// CURRENT VALUATION
		columnPortfolioValuation.setCellValueFactory(new PropertyValueFactory<>("valuation"));
		columnPortfolioValuation.setCellFactory(tableC -> new CurrencyCell<>());
	}
	
	public void addDataToPortfolioTable() throws IOException {
		double totalValuation = 0;

		// reset PortFolio ObseravableList
		portfolioObservableList.clear();
		
		// iterate currentHoldings HashMap for PortFolio
		HashMap<String, Double> currentHoldingsHM = theModel.getFIFODeque().getCurrentHoldings();

	    Iterator<HashMap.Entry<String, Double>> iterator = currentHoldingsHM.entrySet().iterator();
	    while (iterator.hasNext()) {
	    		HashMap.Entry<String, Double> tempHashMap = (HashMap.Entry<String, Double>)iterator.next();
	    		// get coin and total holding
			String coin = tempHashMap.getKey();
			double holding = tempHashMap.getValue();
			// get price and calculate valuation
			System.out.println(holding + " BTC holding oo");
			double price =  theModel.getCryptoCompareAPI().getCoinPrice(coin.substring(coin.indexOf("(")+1,coin.indexOf(")")));
			double valuation = holding * price;

			totalValuation += valuation;

			try {
				portfolioObservableList.add(0, theModel.new Portfolio(coin, price, holding, valuation));
			} catch (Exception e1) { e1.printStackTrace(); }
			
			
			if (holding != 0) {
				addDataToRingChart(coin, holding);
			}
			
			//iterator.remove(); // avoids a ConcurrentModificationException
			
	    }

		// set totalVaulation for Ring Chart
	    pieChart.setTotalValuationText(totalValuation);
	}
	
	public void setUpRingChart() {
        // initialize RingChart
        pieChart = new RingChart();
        //pieChart.setTitle("Imported Fruits");
        pieChart.setTitleSide(Side.TOP);
        pieChart.setLabelsVisible(false);
        pieChart.setLabelLineLength(10);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(Side.LEFT);
        
        // add RingChart to HBox
        hBoxRing.getChildren().addAll(pieChart);
	}
	
	public void addDataToRingChart(String coin, double quantity) {
		// if coin already exists just update quantity
		for(PieChart.Data element : pieChartData) {
			if(element.getName().equals(coin)) {
				element.setPieValue(quantity);
				return;
			}
	    } // and new coin with quantity
		 pieChartData.add(new PieChart.Data(coin, quantity));
	}
	
	public void reFreshPortFolioData() {
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				@SuppressWarnings("unused")
				int i = 0;
			    while (true) {
			    		Platform.runLater(new Runnable() {
			    			@Override
			    			public void run() {
			    				try { addDataToPortfolioTable(); } catch (IOException e) { e.printStackTrace(); }
			    				setLegendPieColors();
			    				System.out.println("Execution = " + counter++);
			    			}
			    		});
			    	i++;
			    	Thread.sleep(10000); // Every 10 seconds 
			    }
			}
		};
		taskThread = new Thread(task);
		taskThread.setDaemon(true);
		taskThread.start();
	}
	
	private void setLegendPieColors() {
		for(PieChart.Data element : pieChartData) {
			setPieColors(element.getName());
			setLegendColors(element.getName());
	    }
	}
	
	private void setPieColors(String dataName) {
			for(PieChart.Data data : pieChartData) {
				if(data.getName().equals(dataName)) {
					data.getNode().setStyle("-fx-pie-color: " + dataColors.get(dataName) + ";");
				}
		    }
	  }
	  
	@SuppressWarnings("restriction")
	public void setLegendColors(String dataName) {
		  for(Node n : pieChart.getChildrenUnmodifiable()) {
			   if(n instanceof Legend) {
			      for(Legend.LegendItem legendItem : ((Legend)n).getItems())	{
			          if (legendItem.getText().equals(dataName)) {
			        	  	legendItem.getSymbol().setStyle("-fx-background-color: " + dataColors.get(dataName) +";");
			          }
			      }
			   }
		  }
	  }
	
	public void addColorStylesToHashMap() {
		dataColors = new HashMap<String,String>();
		
		dataColors.put("Bitcoin (BTC)","#f7921a");
		dataColors.put("Ethereum (ETH)","#497595");
		dataColors.put("Tether (USDT)","#23a078");
		dataColors.put("FunFair (FUN)","#d2285e");
		dataColors.put("Stellar Lumens (XLM)","#dcf2fa");
		dataColors.put("Ripple (XRP)","#1175a7");
		dataColors.put("SiaCoin (SC)","#00cba0");
		dataColors.put("Litecoin (LTC)","#bebebe");
		dataColors.put("IOTA (MIOTA)","#4a4444");
		dataColors.put("EOS (EOS)","#2f2d2e");
		dataColors.put("NEO (NEO)","#72c811");
		dataColors.put("LISK (LSK)","#0093c9");
		dataColors.put("Verge (XVG)","#0098bf");
		dataColors.put("Aragon (ANT)","#03dde3");
		dataColors.put("Cardano (ADA)","#246dd3");
		dataColors.put("NEM (XEM)","#00ffb1");
		dataColors.put("Dash (DASH)","#1c74bd");
		dataColors.put("Waves (Waves)","#3cb6d3");
		dataColors.put("Monero (XMR)","#ff6600");
		dataColors.put("TRON (TRX)","#251313");
		dataColors.put("Nxt (NXT)","#00b8e6");

	}
	
} // end controller