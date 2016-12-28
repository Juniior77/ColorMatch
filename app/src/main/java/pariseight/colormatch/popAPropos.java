package pariseight.colormatch;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.os.Bundle;

/**
 * Created by Guillaume on 23/12/2016.
 */

public class popAPropos extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popapropos);

        DisplayMetrics dmAPropos = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dmAPropos);

        int width = dmAPropos.widthPixels;
        int height = dmAPropos.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));
    }
}
