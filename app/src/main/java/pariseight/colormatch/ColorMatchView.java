package pariseight.colormatch;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Guillaume on 17/12/2016.
 */

public class ColorMatchView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // Declaration des images
    private Bitmap bleu;
    private Bitmap ciel;
    private Bitmap jaun;
    private Bitmap marr;
    private Bitmap oran;
    private Bitmap rose;
    private Bitmap turq;
    private Bitmap vert;
    private Bitmap vide;
    private Bitmap win;

    // Declaration des objets Ressources et Context permettant d'acceder aux ressources de notre application et de les charger
    private Resources mRes;
    private Context mContext;

    // tableau modelisant la carte du jeu
    int[][] carte;

    // ancres pour pouvoir centrer la carte du jeu
    float carteTopAnchor;                   // coordonnees en Y du point d'ancrage de notre carte
    float carteLeftAnchor;                  // coordonnees en X du point d'ancrage de notre carte

    //Ressource pour transposer les coordonnées X et Y du Click sur les bonne cases
    int posClickX, posClickY;
    int posCouleurGauche, posCouleurDroite, posCouleurHaut, posCouleurBas;

    // taille de la carte
    static final int carteWidth = 10;
    static final int carteHeight = 14;
    static final int carteTileSize = 96;
    public ArrayList tabCol = new ArrayList();

    // constante modelisant les differentes types de cases
    static final int CST_vide = 0;
    static final int CST_bleu = 1;
    static final int CST_ciel = 2;
    static final int CST_jaun = 3;
    static final int CST_marr = 4;
    static final int CST_oran = 5;
    static final int CST_rose = 6;
    static final int CST_turq = 7;
    static final int CST_vert = 8;

    //Déclaration des variable de gestion des evenement du jeux
    public static int Lvl = 1;
    public int nbCoup = 0;
    public boolean nextLvl = false;
    public static boolean nxtLevel = false;
    int score = 0;

    //Declaration du mediaPlayer pour gérer les son du jeux
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private boolean sound;

    /* compteur et max pour animer les zones d'arrivee des diamants */
    int currentStepZone = 0;
    int maxStepZone = 4;

    // thread utiliser pour animer les zones de depot des diamants
    private boolean in = true;
    private Thread cv_thread;
    SurfaceHolder holder;

    Paint paint;

    /**
     * The constructor called from the main JetBoy activity
     *
     * @param context
     * @param attrs
     */
    public ColorMatchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext = context;
        mRes = mContext.getResources();
        // chargement des images
        mContext = context;
        mRes = mContext.getResources();
        /*bleu = BitmapFactory.decodeResource(mRes, R.mipmap.bleu);
        ciel = BitmapFactory.decodeResource(mRes, R.mipmap.ciel);
        jaun = BitmapFactory.decodeResource(mRes, R.mipmap.jaune);
        marr = BitmapFactory.decodeResource(mRes, R.mipmap.marron);
        oran = BitmapFactory.decodeResource(mRes, R.mipmap.orange);
        rose = BitmapFactory.decodeResource(mRes, R.mipmap.rose);
        turq = BitmapFactory.decodeResource(mRes, R.mipmap.turquoise);
        vert = BitmapFactory.decodeResource(mRes, R.mipmap.vertclaire);
        vide = BitmapFactory.decodeResource(mRes, R.mipmap.vide);*/
        bleu = BitmapFactory.decodeResource(mRes, R.drawable.bleu);
        ciel = BitmapFactory.decodeResource(mRes, R.drawable.ciel);
        jaun = BitmapFactory.decodeResource(mRes, R.drawable.jaune);
        marr = BitmapFactory.decodeResource(mRes, R.drawable.marron);
        oran = BitmapFactory.decodeResource(mRes, R.drawable.orange);
        rose = BitmapFactory.decodeResource(mRes, R.drawable.rose);
        turq = BitmapFactory.decodeResource(mRes, R.drawable.turquoise);
        vert = BitmapFactory.decodeResource(mRes, R.drawable.vertclaire);
        vide = BitmapFactory.decodeResource(mRes, R.drawable.vide);
        win = BitmapFactory.decodeResource(mRes, R.drawable.win);

        // initialisation des parmametres du jeu
        //initparameters();

        // creation du thread
        cv_thread = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);

    }

    //Generation du nombre des couleurs
    private void loadRandCol()
    {
        int nbCouleur = 8, nbColMin = 10, nbColMax = 120;
        Random rand = new Random();

        tabCol.add(0, 20);
        Log.i("-> FCT <-", "tabCol0: NbCol: " + tabCol.get(0));
        for (int i = 1; i < 8 ; i++)
        {
            int colRand = nbColMin + rand.nextInt( 20 - nbColMin);
            if((colRand%2) != 0)
            {
                colRand += 1;
            }
            tabCol.add(i, colRand);
            nbColMax -= colRand;
            Log.i("-> FCT <-", "tabCol[i]: " + i + " nbColMax: " + tabCol.get(i));
        }
        tabCol.add(8, nbColMax);
        Log.i("-> FCT <-", "tabCol8: nbColMax: " + tabCol.get(8));
        loadCarte();
    }

    //Chargement de la carte
    private void loadCarte()
    {
        Random rand = new Random();
        for (int i = 0; i < carteHeight; i++)
        {
            for(int j = 0; j < carteWidth; j++)
            {
                int myCol = 0 + rand.nextInt(tabCol.size() - 0);
                if((int)tabCol.get(myCol) == 0)
                {
                    while((int)tabCol.get(myCol) == 0)
                    {
                        myCol = 0 + rand.nextInt(tabCol.size() - 0);
                    }
                }
                carte[i][j] = myCol;
                tabCol.set(myCol, ((int)tabCol.get(myCol))-1);
                Log.i("-> FCT <-", "tabCol["+i+"]["+j+"] :" + " rand: " + myCol + " Reste de tabCol["+ myCol+"]: " + tabCol.get(myCol));

            }
        }
    }

    // initialisation du jeu
    public void initparameters(boolean soundAct) {

        sound = soundAct;

        paint = new Paint();
        paint.setColor(0xff0000);

        paint.setDither(true);
        paint.setColor(0xFFFFFF00);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
        paint.setTextAlign(Paint.Align.LEFT);
        carte = new int[carteHeight][carteWidth];
        loadRandCol();
        carteTopAnchor = (getHeight() - carteHeight * carteTileSize) / 2;
        carteLeftAnchor = (getWidth() - carteWidth * carteTileSize) / 2;
        if ((cv_thread != null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }

    // dessin du gagne si gagne
    private void paintwin(Canvas canvas) {
        canvas.drawBitmap(win, carteLeftAnchor + 3 * carteTileSize, carteTopAnchor + 4 * carteTileSize, null);
        Lvl = Lvl + 1;
        nbCoup = 0;
        nextLvl = true;
        initparameters(sound);
    }

    private void paintLvl(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        canvas.drawText("Level: " + Lvl, carteLeftAnchor + 3 * carteTileSize, carteTopAnchor - 2 * carteTileSize, paint);
    }

    private void printScore(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        canvas.drawText("Score: "+ score, 75 , 75, paint);
    }

    // dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                switch (carte[i][j]) {
                    case CST_vide:
                        canvas.drawBitmap(vide, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_bleu:
                        canvas.drawBitmap(bleu, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_ciel:
                        canvas.drawBitmap(ciel, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_jaun:
                        canvas.drawBitmap(jaun, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_marr:
                        canvas.drawBitmap(marr, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_oran:
                        canvas.drawBitmap(oran, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_rose:
                        canvas.drawBitmap(rose, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_turq:
                        canvas.drawBitmap(turq, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                    case CST_vert:
                        canvas.drawBitmap(vert, carteLeftAnchor + j * carteTileSize, carteTopAnchor + i * carteTileSize, null);
                        break;
                }
            }
        }
    }

    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(44, 44, 44);
        if (checkIsWon()) {
            paintcarte(canvas);
            paintwin(canvas);
        } else {
            paintcarte(canvas);
            paintLvl(canvas);
            printScore(canvas);
        }

    }

    //Check si la partie est gagner !
    private boolean checkIsWon()
    {
        boolean stop = false, IsWon = false;
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                if(carte[i][j] != 0)
                {
                    stop = true;
                    break;
                }
                else
                {
                    if(i==13 && j==9)
                    {
                        IsWon = true;
                    }
                }
            }
            if(stop==true)
            {
                IsWon = false;
                break;
            }
        }
        return IsWon;
    }


    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged " + width + " - " + height);
        initparameters(sound);
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");

    }


    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
    }

    /**
     * run (run du thread cree)
     * on endort le thread, on modifie le compteur d'animation, on prend la main pour dessiner et on dessine puis on libere le canvas
     */
    public void run() {
        Canvas c = null;
        while (in) {
            try {
                if (nextLvl == false && nxtLevel == false) {
                    Thread.sleep(30);
                    // currentStepZone = (currentStepZone + 1) % maxStepZone;
                } else {
                    if (nxtLevel == true) {     //Test si il faut passer au lvl suivant
                        initparameters(sound);
                        nxtLevel = false;
                    } else {
                        Thread.sleep(3000);
                        nextLvl = false;
                    }
                }
                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch (Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN" + e.getMessage());
            }
        }
    }
    //Gestion du match des couleurs
    private void CheckCarteMatch(int x, int y)
    {
        if(carte[y][x] == 0) {
            int couleurGauche = getGauche(x, y);
            int couleurDroite = getDroite(x, y);
            int couleurHaut = getHaut(x, y);
            int couleurBas = getBas(x, y);
            int scoreTmp = score; //Permet de verifier si le score a deja été MAJ

            //Log.i("-> FCT <-", "Couleur de droite  : " + couleurDroite);
            //Log.i("-> FCT <-", "Couleur de gauche  : " + couleurGauche);
            //Log.i("-> FCT <-", "Couleur du haut    : " + couleurHaut);
            //Log.i("-> FCT <-", "Couleur du bas     : " + couleurBas);

            //Gestion des Quadruples
            if(couleurGauche == couleurDroite && couleurHaut == couleurBas && couleurGauche != 0 && couleurHaut != 0) {
                clickSound();
                carte[y][posCouleurGauche] = 0;
                carte[y][posCouleurDroite] = 0;
                carte[posCouleurHaut][x] = 0;
                carte[posCouleurBas][x] = 0;
                score += 120;
            }
            else if(score == scoreTmp){
                //Gestion des Triples
                if (couleurDroite == couleurHaut && couleurDroite == couleurBas && couleurDroite != 0) {
                    clickSound();
                    carte[y][posCouleurDroite] = 0;
                    carte[posCouleurHaut][x] = 0;
                    carte[posCouleurBas][x] = 0;
                    score += 60;
                }
                if (couleurHaut == couleurDroite && couleurHaut == couleurGauche && couleurHaut != 0) {
                    clickSound();
                    carte[posCouleurHaut][x] = 0;
                    carte[y][posCouleurDroite] = 0;
                    carte[y][posCouleurGauche] = 0;
                    score += 60;
                }
                if (couleurGauche == couleurHaut && couleurGauche == couleurBas && couleurGauche != 0) {
                    clickSound();
                    carte[y][posCouleurGauche] = 0;
                    carte[posCouleurHaut][x] = 0;
                    carte[posCouleurBas][x] = 0;
                    score += 60;
                }
                if (couleurBas == couleurDroite && couleurBas == couleurGauche && couleurBas != 0) {
                    clickSound();
                    carte[posCouleurBas][x] = 0;
                    carte[y][posCouleurDroite] = 0;
                    carte[y][posCouleurGauche] = 0;
                    score += 60;
                }
                else if (score == scoreTmp){

                    //Gestion des Double
                    if (couleurDroite == couleurHaut && couleurDroite != 0) {
                        clickSound();
                        carte[y][posCouleurDroite] = 0;
                        carte[posCouleurHaut][x] = 0;
                        score += 40;
                    }
                    if (couleurHaut == couleurGauche && couleurHaut != 0) {
                        clickSound();
                        carte[posCouleurHaut][x] = 0;
                        carte[y][posCouleurGauche] = 0;
                        score += 40;
                    }
                    if (couleurGauche == couleurBas && couleurGauche != 0) {
                        clickSound();
                        carte[y][posCouleurGauche] = 0;
                        carte[posCouleurBas][x] = 0;
                        score += 40;
                    }
                    if (couleurBas == couleurDroite && couleurBas != 0) {
                        clickSound();
                        carte[posCouleurBas][x] = 0;
                        carte[y][posCouleurDroite] = 0;
                        score += 40;
                    }
                    if (couleurBas == couleurHaut && couleurBas != 0) {
                        clickSound();
                        carte[posCouleurBas][x] = 0;
                        carte[posCouleurHaut][x] = 0;
                        score += 40;
                    }
                    if (couleurDroite == couleurGauche && couleurDroite != 0) {
                        clickSound();
                        carte[y][posCouleurDroite] = 0;
                        carte[y][posCouleurGauche] = 0;
                        score += 40;
                    }
                    else{
                        //BOYCOT DU TEMPS !!!!
                    }
                }
            }
        }
    }

    //Stock la couleur de gauche
    private int getGauche (int x, int y)
    {
        int colorNegFirst = 0;
        for(int i = x; i >= 0; i-- )
        {
            if(carte[y][i]!=0)
            {
                colorNegFirst = carte[y][i];
                posCouleurGauche = i;
                break;
            }
        }
        return colorNegFirst;
    }

    //Stock la couleur de droite
    private int getDroite(int x, int y)
    {
        int colorPosFirst = 0;
        for(int i = x; i <= 9; i++ )
        {
            if(carte[y][i]!=0)
            {
                colorPosFirst = carte[y][i];
                posCouleurDroite = i;
                break;
            }
            //Log.i("-> FCT <-", "Incrémentation de i: " + i);
        }
        return colorPosFirst;
    }

    //Stock la couleur du haut
    private int getHaut(int x, int y)
    {
        int colorNegFirst = 0;
        for(int i = y; i >= 0; i-- )
        {
            if(carte[i][x]!=0)
            {
                colorNegFirst = carte[i][x];
                posCouleurHaut = i;
                break;
            }
        }
        return colorNegFirst;
    }

    //Stock la couleur du bas
    private int getBas(int x, int y)
    {
        int colorPosFirst = 0;
        for(int i = y; i <= 13; i++ )
        {
            if(carte[i][x]!=0)
            {
                colorPosFirst = carte[i][x];
                posCouleurBas = i;
                break;
            }
            //Log.i("-> FCT <-", "Incrémentation de i: " + i);
        }
        return colorPosFirst;
    }

    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent(MotionEvent event) {

        posClickX = (int)(event.getX() - carteLeftAnchor) / carteTileSize;
        posClickY = (int)(event.getY() - carteTopAnchor) / carteTileSize;

        //Log.i("-> FCT <-", "onTouchEvent getX: " + posClickX);
        //Log.i("-> FCT <-", "onTouchEvent getY: " + posClickY);
        //Log.i("-> FCT <-", "DecalageWidth: " + decalageWidth + " DécalageHeigt: " + decalageHeight);

        if(posClickX >= 0 && posClickX <= 9)
        {
            if(posClickY >= 0 && posClickY<= 13)
            {
                //Log.i("-> FCT <-", "BlockSelectionné: " + carte[posClickY][posClickX]);
                CheckCarteMatch(posClickX, posClickY);
            }
        }
        return super.onTouchEvent(event);
    }

    private void clickSound()
    {
        if(sound==true)
        {
            mMediaPlayer.reset();
            mMediaPlayer = MediaPlayer.create(mContext, R.raw.click);
            mMediaPlayer.setVolume((1),(1));
            mMediaPlayer.start();
        }
    }
}
