package com.project.nc1_test.services;

import com.project.nc1_test.models.NewsArticle;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class NewsArticleCell extends ListCell<NewsArticle> {
    private GridPane gridPane = new GridPane();
    private Text headlineText = new Text();
    private Text contentText = new Text();
    private Text timeText = new Text();

    public NewsArticleCell() {
        super();
        gridPane.add(headlineText, 0, 0);
        gridPane.add(contentText, 1, 0);
        gridPane.add(timeText, 2, 0);

        gridPane.setHgap(10); // Set horizontal gap between columns
        gridPane.setPadding(new Insets(5, 10, 5, 10)); // Padding around the grid

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(200); // Set preferred width for headline column
        col1.setHalignment(HPos.LEFT);
        gridPane.getColumnConstraints().add(col1);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(800); // Set preferred width for content column
        col2.setHalignment(HPos.LEFT);
        col2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().add(col2);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPrefWidth(200); // Set preferred width for time column
        col3.setHalignment(HPos.RIGHT);
        gridPane.getColumnConstraints().add(col3);

        // Wrap text
        headlineText.setWrappingWidth(200); // Adjust wrapping width as needed
        contentText.setWrappingWidth(800); // Adjust wrapping width as needed

        // Set alignment and style
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setStyle("-fx-border-color: black; -fx-border-width: 1px;"); // CSS style for border
    }

    @Override
    protected void updateItem(NewsArticle item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            headlineText.setText(item.getNewsHeadline());
            contentText.setText(item.getNewsDescription());
            timeText.setText(item.getPublicationTime().toString());
            setGraphic(gridPane);
        }
    }
}
