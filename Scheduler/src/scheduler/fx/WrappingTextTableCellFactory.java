package scheduler.fx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.fx.WrappingTextTableCellFactory}
 */
public class WrappingTextTableCellFactory<T> implements Callback<TableColumn<T, String>, TableCell<T, String>> {

    @Override
    public TableCell<T, String> call(TableColumn<T, String> param) {
        return new TableCell<T, String>() {
//            private Text text;
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                this.setWrapText(true);
                if (item == null) {
//                    if (null != text) {
//                        text.wrappingWidthProperty().unbind();
//                        text = null;
//                    }
                    setText("");
                } else {
//                    if (null == text) {
//                        text = new Text();
//                        //text.setStyle("-fx-text-alignment:justify;");                      
//                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
//                    }
//                    text.setText(item);
                    setText(item);
                }
//                setGraphic(text);
            }
        };
    }

}
