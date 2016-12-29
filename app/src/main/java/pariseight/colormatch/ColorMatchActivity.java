package pariseight.colormatch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by Guillaume on 23/12/2016.
 */

public class ColorMatchActivity extends Activity {

    public boolean sound = true;
    public boolean oldGame = false;
    public int nbCouleur = 4;

    private ColorMatchView mColorMatchView;
    private AdView mAdView;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_match);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5980554617735180/9913814758");
        mAdView = (AdView) findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sound = getIntent().getExtras().getBoolean(String.valueOf(R.string.ACTIVE_SOUND));
        nbCouleur = getIntent().getExtras().getInt(String.valueOf(R.string.NB_COULEUR));
        oldGame = getIntent().getExtras().getBoolean(String.valueOf(R.string.ACTIVE_OLD_GAME));

        mColorMatchView = (ColorMatchView)findViewById(R.id.ColorMarchView);
        mColorMatchView.setVisibility(View.VISIBLE);
        mColorMatchView.init(sound, oldGame, nbCouleur);
    }

    @Override
    protected void onStop() {
        mColorMatchView.cv_thread.interrupt();
        mColorMatchView.saveCarte();
        if(mAdView != null)
        {
            mAdView.destroy();
        }
        super.onStop();
    }
}