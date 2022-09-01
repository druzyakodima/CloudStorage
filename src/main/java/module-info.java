module com.example.cloudst {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires io.netty.all;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens com.example.cloudst to javafx.fxml;
    exports com.example.cloudst;
    exports com.example.cloudst.controllers;
    opens com.example.cloudst.controllers to javafx.fxml;
}