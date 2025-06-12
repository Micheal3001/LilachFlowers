package org.client;

import javafx.scene.input.MouseEvent;

public class AddItemController extends ItemController {

    public void goToAddItemPage(MouseEvent event) throws InterruptedException {
        clickOnProductEffect(event);
        Controller controller = null;
        controller = App.client.storeSkeleton.changeCenter("AddProduct");
    }
}
