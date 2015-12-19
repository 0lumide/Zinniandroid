package co.mide.zinniandroid.sample;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.xdump.android.zinnia.ModelDoesNotExistException;
import org.xdump.android.zinnia.Zinnia;
public class MainActivity extends Activity {
    Zinnia zin;
    TextView recog;
    TextView percent;
    DrawCanvas canvas;
    long character, recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zin = new Zinnia(this);
        try {
            recognizer = zin.zinnia_recognizer_new("handwriting-ja.model");
        }catch (ModelDoesNotExistException e){
            //
        }
        recog = (TextView)findViewById(R.id.recog);
        percent = (TextView)findViewById(R.id.percent);
        canvas = (DrawCanvas)findViewById(R.id.canvas);

        canvas.registerStrokeCallback(new DrawCanvas.StrokeCallback() {
            @Override
            public void onStrokeCountChange(int strokeCount) {
                //Clear previous character
                Zinnia.zinnia_character_destroy(character);
                character = zin.zinnia_character_new();
                zin.zinnia_character_set_width(character, canvas.getWidth());
                zin.zinnia_character_set_height(character, canvas.getHeight());

                //add stroke/points to character
                if (strokeCount > 0) {
                    for (int j = 0; j < strokeCount; j++) {
                        DrawCanvas.Stroke stroke = canvas.getStroke(j);
                        for (int i = 0; i < stroke.getSize(); i++) {
                            zin.zinnia_character_add(character, j, stroke.getPoint(i).x, stroke.getPoint(i).y);
                        }
                    }
                }

                //perform recognition and update views
                long result = zin.zinnia_recognizer_classify(recognizer, character, 1);
                if (result == 0) {
                    recog.setText("");
                    percent.setText("");
                    Log.e("Zinnia", String.format("%s", zin.zinnia_recognizer_strerror(recognizer)));
                } else {
                    for (int i = 0; i < zin.zinnia_result_size(result); i++) {
                        Log.v("Zinnia", String.format("%s\t%f\n", zin.zinnia_result_value(result, i), zin.zinnia_result_score(result, i)));
                        recog.setText(zin.zinnia_result_value(result, i));
                        percent.setText(String.format("%.2f", zin.zinnia_result_score(result, i)));
                    }
                }
            }
        });


        character = zin.zinnia_character_new();
        zin.zinnia_character_set_width(character, canvas.getWidth());
        zin.zinnia_character_set_height(character, canvas.getHeight());
        canvas.resetCanvas();

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Zinnia.zinnia_character_destroy(character);
                character = zin.zinnia_character_new();
                zin.zinnia_character_set_width(character, canvas.getWidth());
                zin.zinnia_character_set_height(character, canvas.getHeight());
                recog.setText("");
                percent.setText("");
                canvas.resetCanvas();
            }
        });

        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.undoStroke();
            }
        });
    }

}
