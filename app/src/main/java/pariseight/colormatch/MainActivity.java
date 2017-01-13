package pariseight.colormatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button ButtonPlay;
    Button ButtonSound;
    Button ButtonAPropos;
    Button ButtonReprendre;
    Button ButtonAddCol;
    Button ButtonRemCol;
    TextView TextHighScore;
    TextView TextNbColor;
    Switch switchChrono;

    public boolean sound = true;
    public boolean BoolOldGame = false;
    public int nbCouleur = 4;
    private int HighScore = 0;
    private boolean chronoActive = true;
    private SharedPreferences mPref;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        ButtonPlay = (Button)findViewById(R.id.bPlay);
        ButtonSound = (Button)findViewById(R.id.bSound);
        ButtonAPropos = (Button)findViewById(R.id.bAPropos);
        ButtonReprendre = (Button)findViewById(R.id.bReprendre) ;
        ButtonAddCol = (Button)findViewById(R.id.buttonAdd);
        ButtonRemCol = (Button)findViewById(R.id.buttonRem);
        TextHighScore = (TextView)findViewById(R.id.textView6);
        TextNbColor = (TextView)findViewById(R.id.textViewColor);
        ButtonReprendre.setVisibility(View.INVISIBLE);
        switchChrono = (Switch)findViewById(R.id.switchChrono);

        mPref = getBaseContext().getSharedPreferences(String.valueOf(R.string.MY_PREF), MODE_PRIVATE);

        TextNbColor.setText("Nombre Couleur: " + nbCouleur);

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

        ButtonAddCol.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(nbCouleur<8)
                {
                    nbCouleur++;
                    TextNbColor.setText("Nombre Couleur: " + nbCouleur);
                }
            }
        });

        ButtonRemCol.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(nbCouleur>1)
                {
                    nbCouleur--;
                    TextNbColor.setText("Nombre Couleur: " + nbCouleur);
                }
            }
        });

        ButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ColorMatchActivity.class);
                intent.putExtra(String.valueOf(R.string.ACTIVE_SOUND), sound);
                intent.putExtra(String.valueOf(R.string.NB_COULEUR), nbCouleur);
                intent.putExtra(String.valueOf(R.string.ACTIVE_CHRONO), chronoActive);
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

        switchChrono.setChecked(true);
        switchChrono.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    chronoActive = true;
                } else {
                    chronoActive = false;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
