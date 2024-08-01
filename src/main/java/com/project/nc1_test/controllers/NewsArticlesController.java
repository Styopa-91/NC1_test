package com.project.nc1_test.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.nc1_test.MainApplication;
import com.project.nc1_test.models.NewsArticle;
import com.project.nc1_test.models.NewsArticleDto;
import com.project.nc1_test.services.NewsArticleCell;
import com.project.nc1_test.services.NewsArticlesService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class NewsArticlesController {
    @Setter
    private NewsArticlesController parentController;
    @FXML
    private ListView<NewsArticle> newsListView;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private ComboBox<String> hoursComboBox;
    @FXML
    private ComboBox<String> minutesComboBox;
    private final NewsArticlesService newsArticlesService;
    private ObservableList<NewsArticle> observableArticles;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private NewsArticle currentArticle;

    private Timeline refreshTimeline;

    public NewsArticlesController() {
        this.newsArticlesService = new NewsArticlesService();
    }

    @FXML
    public void initialize() {
        // Initialize ComboBox options
        if (filterComboBox != null) {
            // Initialize ComboBox options
            filterComboBox.setItems(FXCollections.observableArrayList("All", "Morning", "Day", "Evening"));
            filterComboBox.setValue("All");
            filterComboBox.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> filterArticles(newValue)
            );
        }
        if (hoursComboBox != null) {
            // Initialize ComboBox options
            ObservableList<String> hours = FXCollections.observableArrayList();
            for (int i = 0; i < 24; i++) {
                hours.add(String.format("%02d", i));
            }
            hoursComboBox.setItems(hours);
            hoursComboBox.setValue("00");
        }
        if (minutesComboBox != null) {
            // Initialize ComboBox options
            ObservableList<String> minutes = FXCollections.observableArrayList();
            for (int i = 0; i < 60; i++) {
                minutes.add(String.format("%02d", i));
            }
            minutesComboBox.setItems(minutes);
            minutesComboBox.setValue("00"); // Default value
        }
        if (newsListView != null) {

            setupPeriodicRefresh();
        }
    }

    private void setupPeriodicRefresh() {
        loadArticles();
        refreshTimeline = new Timeline(new KeyFrame(Duration.minutes(1), e -> loadArticles()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private void loadArticles() {
        newsListView.getStylesheets().add(MainApplication.class.getResource("style.css").toExternalForm());

        List<NewsArticle> articles = newsArticlesService.getAllArticles();
        observableArticles = FXCollections.observableArrayList(articles);
        newsListView.setItems(observableArticles);
        newsListView.getSelectionModel().select(0); // Start by selecting the first item
        newsListView.setCellFactory(listView -> {
            return new NewsArticleCell(); // Your custom cell
        });
    }

    private void filterArticles(String period) {
        if (period == null || period.equals("All")) {
            List<NewsArticle> articles = newsArticlesService.getAllArticles();
            observableArticles = FXCollections.observableArrayList(articles);
            newsListView.setItems(observableArticles);
        } else {
            List<NewsArticle> filteredArticles = newsArticlesService.getFilteredArticles(period);
            observableArticles = FXCollections.observableArrayList(filteredArticles);
            newsListView.setItems(observableArticles);
        }
        newsListView.getSelectionModel().select(0); // Start by selecting the first item
    }

    @FXML
    public void handleNextArticle() {
        int currentIndex = newsListView.getSelectionModel().getSelectedIndex();
        int newIndex = currentIndex + 1;

        if (newIndex < observableArticles.size()) {
            newsListView.getSelectionModel().select(newIndex);
            newsListView.scrollTo(newIndex); // Optional: scroll to the new item
        }
    }

    @FXML
    public void handlePreviousArticle() {
        int currentIndex = newsListView.getSelectionModel().getSelectedIndex();
        int newIndex = currentIndex - 1;

        if (newIndex >= 0) {
            newsListView.getSelectionModel().select(newIndex);
            newsListView.scrollTo(newIndex); // Optional: scroll to the new item
        }
    }

    @FXML
    private void handleCancel() {
        // Close the window or return to the main view without saving
        closeWindow();
    }

    @FXML
    private void handleAddButton() {
        try {
            openArticleForm(null);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error, show a message to the user, etc.
        }
    }

    @FXML
    public void handleEditButton() {
        // Logic for editing an existing article
        NewsArticle selectedItem = newsListView.getSelectionModel().getSelectedItem();
        try {
            openArticleForm(selectedItem);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error, show a message to the user, etc.
        }
    }

    // Method to open the form for adding/editing articles
    private void openArticleForm(NewsArticle article) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("form.fxml"));
        Parent root = loader.load();

        NewsArticlesController controller = loader.getController();
        controller.setArticle(article);
        controller.setParentController(this);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL); // Optional: make it a modal dialog
        stage.showAndWait();
    }

    public void setArticle(NewsArticle article) {
        this.currentArticle = article;
        if (article != null) {
            titleField.setText(article.getNewsHeadline());
            contentArea.setText(article.getNewsDescription());
            hoursComboBox.setValue(String.valueOf(article.getPublicationTime().getHour()));
            minutesComboBox.setValue(String.valueOf(article.getPublicationTime().getMinute()));
        } else {
            // Clear fields for adding a new article
            titleField.clear();
            contentArea.clear();
            minutesComboBox.setValue(null);
            hoursComboBox.setValue(null);
        }
    }

    @FXML
    public void handleAddArticle() throws JsonProcessingException {
        // Create or update the article based on current state
        String title = titleField.getText();
        String content = contentArea.getText();
        String selectedHour = hoursComboBox.getValue();
        String selectedMinute = minutesComboBox.getValue();

        // Convert to integers
        int hour = Integer.parseInt(selectedHour);
        int minute = Integer.parseInt(selectedMinute);

        // Create LocalTime object
        LocalTime publicationTime = LocalTime.of(hour, minute);

        if (title.isEmpty() || content.isEmpty() || publicationTime == null) {
            // Handle error: show an alert or message
            return;
        }

        if (currentArticle == null) {
            // Adding a new article
            NewsArticleDto newArticle = new NewsArticleDto(title, content, publicationTime);
            // Add newArticle to the list or database
            newsArticlesService.createArticle(newArticle);
        } else {
            NewsArticleDto newArticle = new NewsArticleDto();
            // Editing an existing article
            newArticle.setNewsHeadline(title);
            newArticle.setNewsDescription(content);
            newArticle.setPublicationTime(publicationTime);
            // Update your list or database
            newsArticlesService.updateArticle(currentArticle.getId(), newArticle);
        }

        // Close the window or return to the main view
        closeWindow();
    }

    private void closeWindow() {
        // Code to close the current window
        if (parentController != null) {
            parentController.updateFilterComboBox();
            parentController.loadArticles();
        }
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public void updateFilterComboBox() {
        filterComboBox.setValue("All");
    }

    @FXML
    public void handleDeleteArticle() {
        NewsArticle selectedItem = newsListView.getSelectionModel().getSelectedItem();
        newsArticlesService.deleteArticle(selectedItem.getId());
        if (filterComboBox.getValue().equals("All")) {
            List<NewsArticle> articles = newsArticlesService.getAllArticles();
            observableArticles = FXCollections.observableArrayList(articles);
            newsListView.setItems(observableArticles);
            newsListView.getSelectionModel().select(0);
        } else {
            List<NewsArticle> articles = newsArticlesService.getFilteredArticles(filterComboBox.getValue());
            observableArticles = FXCollections.observableArrayList(articles);
            newsListView.setItems(observableArticles);
            newsListView.getSelectionModel().select(0);
        }
    }
}
