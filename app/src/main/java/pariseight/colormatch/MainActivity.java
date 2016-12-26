package pariseight.colormatch;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.EasyEditSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button ButtonPlay;
    Button ButtonSound;
    Button ButtonAPropos;
    public boolean sound = true;
    private ColorMatchView mColorMatchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButtonPlay = (Button)findViewById(R.id.bPlay);
        ButtonSound = (Button)findViewById(R.id.bSound);
        ButtonAPropos = (Button)findViewById(R.id.bAPropos);


        ButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ColorMatchActivity.class);
                intent.putExtra("soundActiv", sound);
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
}
