package pariseight.colormatch;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.os.Bundle;

/**
 * Created by Guillaume on 23/12/2016.
 */

public class ColorMatchActivity extends Activity {

    public boolean sound = true;

    private ColorMatchView mColorMatchView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_match);
        sound = getIntent().getExtras().getBoolean("soundActiv");
        mColorMatchView = (ColorMatchView)findViewById(R.id.ColorMarchView);
        mColorMatchView.setVisibility(View.VISIBLE);
        mColorMatchView.initparameters(sound);
    }

}