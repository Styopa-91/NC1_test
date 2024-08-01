module com.project.nc1_test {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310; // Add this if you're using JavaTimeModule

    opens com.project.nc1_test to javafx.fxml, com.fasterxml.jackson.databind; // Grant Jackson access
    exports com.project.nc1_test;
    opens com.project.nc1_test.controllers to javafx.fxml;
    exports com.project.nc1_test.controllers;
    exports com.project.nc1_test.models;
    opens com.project.nc1_test.models to com.fasterxml.jackson.databind, javafx.fxml;
}
