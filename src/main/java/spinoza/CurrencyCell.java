package spinoza;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.UnaryOperator;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class CurrencyCell<T> extends TableCell<T, Double> {

    private final TextField textField ;

    private final NumberFormat format = DecimalFormat.getCurrencyInstance(Locale.CANADA);
    private final DecimalFormat textFieldFormat = new DecimalFormat("0.00");

    public CurrencyCell() {
        this.textField = new TextField();
        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return object == null ? "" : textFieldFormat.format(object) ;
            }

            @Override
            public Double fromString(String string) {
                try {
                    return string.isEmpty() ? 0.0 : textFieldFormat.parse(string).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0.0 ;
                }
            }

        };
        UnaryOperator<Change> filter = (Change change) -> {
            String newText = change.getControlNewText() ;
            if (newText.isEmpty()) {
                return change ;
            }
            try {
                textFieldFormat.parse(newText);
                return change ;
            } catch (ParseException exc) {
                return null ;
            }
        };
        TextFormatter<Double> textFormatter = new TextFormatter<Double>(converter, 0.0, filter);
        textField.setTextFormatter(textFormatter);

        textField.setOnAction(e -> commitEdit(converter.fromString(textField.getText())));
        textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

    }

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else if (isEditing()) {
            textField.setText(item.toString());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(format.format(item));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(textFieldFormat.format(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(format.format(getItem()));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void commitEdit(Double newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }
}