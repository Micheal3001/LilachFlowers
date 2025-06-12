module org.server {
    requires java.naming;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;
    requires org.entities;
    requires org.email;
    requires javafx.controls;
    exports org.server;


}
