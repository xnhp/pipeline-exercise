package org.somecontributor;

import org.bm.Main;
import org.bm.operations.OperationsManager;

/**
 * @author Benjamin Moser.
 */
public class PluginMain {

    public static void main(String[] args) {

        // register some additional operations
        OperationsManager.registerOperations(AdditionalOperations.class);

        // start the main app
        Main.start(args);

        // TODO: ability to easily add new recognised input types

    }

}
