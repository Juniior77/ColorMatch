package pariseight.colormatch;

import android.app.Activity;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

/**
 * Created by Guillaume on 23/12/2016.
 */

public class ColorMatchActivity extends Activity {

    public boolean sound = true;
    public boolean oldGame = false;
    Button ButtonTest;

    private ColorMatchView mColorMatchView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_match);
        sound = getIntent().getExtras().getBoolean(String.valueOf(R.string.ACTIVE_SOUND));
        oldGame = getIntent().getExtras().getBoolean(String.valueOf(R.string.ACTIVE_OLD_GAME));


        mColorMatchView = (ColorMatchView)findViewById(R.id.ColorMarchView);
        mColorMatchView.setVisibility(View.VISIBLE);
        mColorMatchView.init(sound, oldGame);


        ButtonTest = (Button)findViewById(R.id.buttonTest);

        ButtonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mColorMatchView.saveCarte();
                mColorMatchView.saveHighScore();
            }
        });

    }
}

