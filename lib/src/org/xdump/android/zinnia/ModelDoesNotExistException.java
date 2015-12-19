package org.xdump.android.zinnia;

/**
 * Created by Olumide on 7/8/2015.
 */
public class ModelDoesNotExistException extends Exception {
    public ModelDoesNotExistException(String modelName){
        super(String.format("Model %s does not exist in the Assets directory", modelName));
    }
}
