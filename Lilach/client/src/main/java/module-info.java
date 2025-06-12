import org.entities.PreMadeProduct;
import org.entities.Product;

module org.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.email;
    requires org.entities;
    requires org.server;
    requires javafx.graphics;
    uses Product;
    uses PreMadeProduct;

    opens org.client to javafx.fxml;
    exports org.client;

}
