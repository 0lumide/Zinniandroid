package org.xdump.android.zinnia;

/**
 * Exception thrown when the Model File cannot be found by zinnia
 */
public class ModelDoesNotExistException extends Exception {
    public ModelDoesNotExistException(String modelName){
        super(String.format("Model %s does not exist in the Assets directory", modelName));
    }
}
