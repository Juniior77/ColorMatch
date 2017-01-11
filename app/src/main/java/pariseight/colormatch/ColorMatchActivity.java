package pariseight.colormatch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Guillaume on 23/12/2016.
 */

public class ColorMatchActivity extends Activity {

    public boolean sound = true;
    public boolean oldGame = false;
    public int nbCouleur = 4;
    public boolean etatChrono = true;


    private ColorMatchView mColorMatchView;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_match);

        sound = getIntent().getExtras().getBoolean(String.valueOf(R.string.ACTIVE_SOUND));
        nbCouleur = getIntent().getExtras().getInt(String.valueOf(R.string.NB_COULEUR));
        oldGame = getIntent().getExtras().getBoolean(String.valueOf(R.string.ACTIVE_OLD_GAME));
        etatChrono = getIntent().getExtras().getBoolean(String.valueOf(R.string.ACTIVE_CHRONO));

        mColorMatchView = (ColorMatchView)findViewById(R.id.ColorMarchView);
        mColorMatchView.setVisibility(View.VISIBLE);
        mColorMatchView.init(sound, oldGame, nbCouleur, etatChrono);
    }

    @Override
    protected void onStop() {
        mColorMatchView.cv_thread.interrupt();
        mColorMatchView.saveCarte();
        super.onStop();
    }
}