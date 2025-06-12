package org.client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {

            App.main(args);     //Main class for Jar calls client main
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
