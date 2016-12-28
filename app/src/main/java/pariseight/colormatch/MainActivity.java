package pariseight.colormatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button ButtonPlay;
    Button ButtonSound;
    Button ButtonAPropos;
    Button ButtonReprendre;
    TextView TextHighScore;

    public boolean sound = true;
    public boolean BoolOldGame = false;
    private int HighScore = 0;
    private SharedPreferences mPref;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButtonPlay = (Button)findViewById(R.id.bPlay);
        ButtonSound = (Button)findViewById(R.id.bSound);
        ButtonAPropos = (Button)findViewById(R.id.bAPropos);
        ButtonReprendre = (Button)findViewById(R.id.bReprendre) ;
        TextHighScore = (TextView)findViewById(R.id.textView6);

        mPref = getBaseContext().getSharedPreferences(String.valueOf(R.string.MY_PREF), MODE_PRIVATE);

        if(mPref.contains(String.valueOf(R.string.HIGH_SCORE)))
        {
            HighScore = mPref.getInt(String.valueOf(R.string.HIGH_SCORE), 0);
        }
        TextHighScore.setText("High Score: " + HighScore);

        if(mPref.contains(String.valueOf(R.string.OLD_CARTE)))
        {
            BoolOldGame = true;
            ButtonReprendre.setVisibility(View.VISIBLE);
        }

        Log.i("-> FCT <-", "tmpScore: " + BoolOldGame + "HighScore: " + HighScore);

        ButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ColorMatchActivity.class);
                intent.putExtra(String.valueOf(R.string.ACTIVE_SOUND), sound);
                startActivity(intent);

            }
        });

        ButtonReprendre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ColorMatchActivity.class);
                intent.putExtra(String.valueOf(R.string.ACTIVE_SOUND), sound);
                intent.putExtra(String.valueOf(R.string.ACTIVE_OLD_GAME), BoolOldGame);
                startActivity(intent);
            }
        });

        //Activation ou non des effects sonore
        ButtonSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlgSoundAlerte = new AlertDialog.Builder(MainActivity.this);
                dlgSoundAlerte.setTitle("Effects sonore");

                if(sound == true) {
                    sound = false;
                    dlgSoundAlerte.setMessage("Les effects sonore son désactivés");
                }

                else
                {
                    sound = true;
                    dlgSoundAlerte.setMessage("Les effects sonore son activés");
                }
                //Création d'un bouton "OK" dans notre fenetre Dialog
                dlgSoundAlerte.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int wichButton){
                        dialog.dismiss();
                    }
                });
                dlgSoundAlerte.setCancelable(true);
                dlgSoundAlerte.create().show();

            }
        });

        ButtonAPropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,popAPropos.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
