package org.xdump.android.zinnia;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Path;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Zinnia {
    private Context context;
	public Zinnia(Context context){
        this.context = context;
    }
	static {
		System.loadLibrary("zinniajni");
	}

	public native long  zinnia_character_new();
	public static native void        zinnia_character_destroy(long character);
	public native void        zinnia_character_set_value(long character, String str);
	public native void        zinnia_character_set_value2(long character, String str, long length);
	public native String zinnia_character_value(long character);
	public native void        zinnia_character_set_width(long character, long width);
	public native void        zinnia_character_set_height(long character, long height);
	public native long      zinnia_character_width(long character);
	public native long      zinnia_character_height(long character);
	public native void        zinnia_character_clear(long stroke);
	public native int         zinnia_character_add(long character, long id, int x, int y);
	public native long      zinnia_character_strokes_size(long character);
	public native long      zinnia_character_stroke_size(long character, long id);
	public native int         zinnia_character_x(long character, long id, long i);
	public native int         zinnia_character_y(long character, long id, long i);
	public native int         zinnia_character_parse(long character, String str);
	public native int         zinnia_character_parse2(long character, String str, long length);
	public native int         zinnia_character_to_string(long character, String buf, long length);
	public native String zinnia_character_strerror(long character);


	public native String zinnia_result_value(long result, long i);
	public native float       zinnia_result_score(long result, long i);
	public native long      zinnia_result_size(long result);
	public native void        zinnia_result_destroy(long result);

	public long zinnia_recognizer_new(String modelName) throws ModelDoesNotExistException{
        String state = Environment.getExternalStorageState();
        String modelPath;
        //If the model file can be read from storage
        Log.d("Mount state", state);
        if(state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            File zinniaDir = new File(Environment.getExternalStorageDirectory(), "zinnia" + File.separatorChar);
            if(!zinniaDir.exists())
                zinniaDir.mkdir();
            File modelFile = new File(zinniaDir, modelName);
            //If model doesn't exist
            if(!modelFile.exists()){
                //Move the read model to zinniaDir
                modelPath = readFromAsset(modelName, zinniaDir);
            }else{
                modelPath = modelFile.getAbsolutePath();
            }
        }
        else{
            //Move the read model to a temp location
            modelPath = readFromAsset(modelName, context.getCacheDir());
        }
        if(modelPath == null)
            throw new ModelDoesNotExistException(modelName);
        long recognizer = zinnia_recognizer_new();
        zinnia_recognizer_open(recognizer, modelPath);
        return recognizer;
	}

    /**
     modified from: http://stackoverflow.com/a/4530294/2057884
     */
    private String readFromAsset(String assetName, File destinationDir){
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        File outFile = null;
        try {
            in = assetManager.open(assetName);
            outFile = new File(destinationDir, assetName);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch(IOException e) {
            outFile = null;
            Log.e("tag", "Failed to copy asset file: " + assetName, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
        if(outFile!=null)
            return outFile.getAbsolutePath();
        else
            return null;
    }
    /**
        author: Rohith Nandakumar
        source: http://stackoverflow.com/a/4530294/2057884
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

	private native long zinnia_recognizer_new();
	public native void                zinnia_recognizer_destroy(long recognizer);
	private native int                 zinnia_recognizer_open(long recognizer, String filename);
	public native int                 zinnia_recognizer_open_from_ptr(long recognizer,
			String ptr, long size);
	public native int                 zinnia_recognizer_close(long recognizer);
	public native long              zinnia_recognizer_size(long recognizer);
	public native String zinnia_recognizer_value(long recognizer, long i);
	public native String         zinnia_recognizer_strerror(long recognizer);
	public native long zinnia_recognizer_classify(long recognizer,
			long character,
			long nbest);

	public native long zinnia_trainer_new();
	public native void             zinnia_trainer_destroy(long trainer);
	public native int              zinnia_trainer_add(long trainer, long character);
	public native void             zinnia_trainer_clear(long trainer);
	public native int              zinnia_trainer_train(long trainer, String filename);
	public native String zinnia_trainer_strerror(long trainer);
	public native int              zinnia_trainer_convert_model(String txt_model,
			String binary_model,
			double compression_threshold);
	public native int              zinnia_trainer_make_header(String txt_model,
			String header_file,
			String name,
			double compression_threshold);
}
