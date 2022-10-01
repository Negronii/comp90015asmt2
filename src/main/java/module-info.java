module com.ruiming.comp90015asmt2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires javafx.swing;


    opens com.ruiming.comp90015asmt2 to javafx.fxml;
    exports com.ruiming.comp90015asmt2;
}