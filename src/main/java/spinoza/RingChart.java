package spinoza;

import java.text.NumberFormat;
import java.util.Locale;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class RingChart extends PieChart {
	private StackPane stackPane;
    private Circle innerCircle;
    private Text text;
    private int fontSize;
    
    // parameterized constructor
    public RingChart() {

        innerCircle = new Circle();

        // just styled in code for demo purposes,
        // use a style class instead to style via CSS.
        innerCircle.setFill(Color.BLACK); // inner circle color
        innerCircle.setStroke(Color.BLACK); // inner circle border color
        innerCircle.setStrokeWidth(3);
        
        // inner circle text
        text = new Text("$0.00");
        text.setFill(Color.WHITE);
        
        // add elements to stack pane (StackPane stacks elements)
        stackPane = new StackPane();
        stackPane.getChildren().addAll(innerCircle); // bottom element "layer"
        stackPane.getChildren().addAll(text); // upper element "layer"
    }
    
    // parameterized constructor
    public void AddValues(ObservableList<Data> pieData) {
    		super.setData(pieData);
    }
    

    @Override
    protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
        super.layoutChartChildren(top, left, contentWidth, contentHeight);

        addInnerCircleIfNotPresent();
        updateInnerCircleLayout();
    }

    // Add StackPane to PieChart (sets StackStack pane as children of parent PieChart)
    private void addInnerCircleIfNotPresent() {
        if (getData().size() > 0) {
            Node pie = getData().get(0).getNode();
            if (pie.getParent() instanceof Pane) {
                Pane parent = (Pane) pie.getParent();

                if (!parent.getChildren().contains(stackPane)) {
                		parent.getChildren().add(stackPane);
                }
            }
        }
    }

    // update size of StackPane and its elements
    private void updateInnerCircleLayout() {
    		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (PieChart.Data data: getData()) {
            Node node = data.getNode();

            Bounds bounds = node.getBoundsInParent();
            if (bounds.getMinX() < minX) {
                minX = bounds.getMinX();
            }
            if (bounds.getMinY() < minY) {
                minY = bounds.getMinY();
            }
            if (bounds.getMaxX() > maxX) {
                maxX = bounds.getMaxX();
            }
            if (bounds.getMaxY() > maxY) {
                maxY = bounds.getMaxY();
            }
        }
       
        // Center StackPane in the center of pie chart
        stackPane.setTranslateX(minX + (maxX - minX) / 2);
        stackPane.setTranslateY(minY + (maxY - minY) / 2);
        // inner circle size (resizes StackPane as well)
        innerCircle.setRadius((maxX - minX) / 3);
      
        // resize inner text font according to minX (x-axis size of PieChart "canvas or pane")
        System.out.println(minX+" <-minX from RingChart.java");
        if (minX <= 9.994888305664062 && minX >= 9.992401123046875) {
        		fontSize = 20;
        } else {
        		fontSize = 30;
        }
        text.setStyle("-fx-font: " + fontSize + " arial; -fx-font-weight: bold;");
    }
    
    // set
    public void setTotalValuationText(double totalValuation) {
    		text.setText(NumberFormat.getCurrencyInstance(Locale.CANADA).format(totalValuation));
    		stackPane.toFront(); // bring stackPane to front as it goes hides behind when new data is added to ring chart
    }
}