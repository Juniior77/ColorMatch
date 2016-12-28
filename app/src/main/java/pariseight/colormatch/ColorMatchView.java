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
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

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
    private Bitmap gameover;

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
    public int carteTileSize = 96;
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
    private int Lvl = 1;
    private int nbCoup = 0;
    public boolean newmap = false;         //Boolean pour passer au level suivant ou charger une nouvelle map
    private int score = 0;
    public long tempsRestant = 30000;
    public boolean repereOldGame;          //Boolean pour reperer si on doit charger une carte sauvegarder ou une nouvelle

    //Declaration du mediaPlayer pour gérer les son du jeux
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private boolean sound;

    // thread utiliser pour animer la grille
    private boolean in = true;
    public Thread cv_thread;
    SurfaceHolder holder;

    Paint paint;
    Chrono mChrono;
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

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
        gameover = BitmapFactory.decodeResource(mRes, R.drawable.gameover);

        // creation du thread
        cv_thread = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);

        mPref = getContext().getSharedPreferences(String.valueOf(R.string.MY_PREF), Context.MODE_PRIVATE);
        mEditor = mPref.edit();

    }

    //Persistence du Score
    public void saveHighScore()
    {
        int tmpScore = 0;
        if(mPref.contains(String.valueOf(R.string.HIGH_SCORE)))
        {
            tmpScore = mPref.getInt(String.valueOf(R.string.HIGH_SCORE), 0);
        }
        if(score > tmpScore)
        {
            mEditor.putInt(String.valueOf(R.string.HIGH_SCORE), score);
            mEditor.apply();
        }
        Log.i("-> FCT <-", "tmpScore: " + tmpScore);
    }

    //Persistence de la carte
    public void saveCarte()
    {
        cv_thread.interrupt();
        StringBuilder carteStr = new StringBuilder();
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                carteStr.append(carte[i][j]).append(";");
            }
        }
        mEditor.putString(String.valueOf(R.string.OLD_CARTE), carteStr.toString());
        Log.i("-> FCT <-", "carteStr: " + carteStr.toString());
        mEditor.putInt(String.valueOf(R.string.OLD_LVL), Lvl);
        mEditor.putInt(String.valueOf(R.string.OLD_SCORE), score);
        mEditor.putLong(String.valueOf(R.string.OLD_TEMPS), mChrono.temps);
        Log.i("-> FCT <-", "Lvl: " + Lvl + " Score: " + score + " Chrono : " + mChrono.temps);
    }

    //Chargement de la carte sauvegarder
    public void loadOldCarte()
    {
        String oldCarte = mPref.getString(String.valueOf(R.string.OLD_CARTE), "");
        StringTokenizer myOldCarte = new StringTokenizer(oldCarte, ";");
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                carte[i][j] = Integer.parseInt(myOldCarte.nextToken());
                Log.i("-> FCT <-", "carte["+i+"]["+j+"]: " + carte[i][j]);
            }
        }
        Lvl = mPref.getInt(String.valueOf(R.string.OLD_LVL), 0);
        score = mPref.getInt(String.valueOf(R.string.OLD_SCORE), 0);
        tempsRestant = mPref.getLong(String.valueOf(R.string.OLD_TEMPS),0);
        Log.i("-> FCT <-", "Lvl: " + Lvl + " Score: " + score + " Chrono : " + mChrono.temps);
        //repereOldGame = false;

    }

    //Generation du nombre des couleurs
    private void loadRandCol() {
        int nbCouleur = 8, nbColMin = 10, nbColMax = 120;
        Random rand = new Random();

        tabCol.add(0, 136);//20);
        //Log.i("-> FCT <-", "tabCol0: NbCol: " + tabCol.get(0));
        for (int i = 1; i < 8; i++) {
        //    int colRand = nbColMin + rand.nextInt(20 - nbColMin);
        //    if ((colRand % 2) != 0) {
        //        colRand += 1;
        //    }
            tabCol.add(i, 0);//colRand);
        //    nbColMax -= colRand;
            //Log.i("-> FCT <-", "tabCol[i]: " + i + " nbColMax: " + tabCol.get(i));
        }
        tabCol.add(8, 4);//nbColMax);
        //Log.i("-> FCT <-", "tabCol8: nbColMax: " + tabCol.get(8));
        loadCarte();
    }

    //Chargement de la carte
    private void loadCarte() {
        Random rand = new Random();
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                int myCol = 0 + rand.nextInt(tabCol.size() - 0);
                if ((int) tabCol.get(myCol) == 0) {
                    while ((int) tabCol.get(myCol) == 0) {
                        myCol = 0 + rand.nextInt(tabCol.size() - 0);
                    }
                }
                carte[i][j] = myCol;
                tabCol.set(myCol, ((int) tabCol.get(myCol)) - 1);
                Log.i("-> FCT <-", "tabCol[" + i + "][" + j + "] :" + " rand: " + myCol + " Reste de tabCol[" + myCol + "]: " + tabCol.get(myCol));

            }
        }
    }

    public void init(boolean soundAct, boolean oldGame)
    {
        sound = soundAct;
        repereOldGame = oldGame;
    }

    // initialisation du jeu
    public void startGame(boolean soundAct, boolean oldGame) {

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
        if(repereOldGame == true)
        {
            loadOldCarte();
        }
        else
        {
            loadRandCol();
        }
        carteTopAnchor = (getHeight() - carteHeight * carteTileSize) / 2;
        carteLeftAnchor = (getWidth() - carteWidth * carteTileSize) / 2;
        mChrono = new Chrono(tempsRestant, 1000);
        if ((cv_thread != null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
        mChrono.start();
    }

    // dessin du gagne si gagne
    private void win(Canvas canvas) {
        canvas.drawBitmap(win, carteLeftAnchor * 7, carteTopAnchor * 4, null);
        Lvl += 1;
        nbCoup = 0;
        mChrono.onFinish();
        saveHighScore();
        score = 0;
    }

    // dessin du gameOver si finTemps ou reste 1 couleur
    private void gameOver(Canvas canvas) {
        canvas.drawBitmap(gameover, carteLeftAnchor * 7, carteTopAnchor * 4, null);
        score = 0;
        nbCoup = 0;
    }

    private void paintLvl(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        canvas.drawText("Level: " + Lvl, 675, 75, paint);
    }

    private void paintScore(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        canvas.drawText("Score: " + score, 75, 75, paint);
    }

    private void paintTemps(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        canvas.drawText("Score: " + mChrono.temps, 75, 75, paint);
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
        if (checkIsWon())
        {
            newmap = true;
            paintcarte(canvas);
            win(canvas);

        }
        else if(mChrono.finTemps==true)
        {
            newmap = true;
            gameOver(canvas);
        }
        else
        {
            paintcarte(canvas);
            paintLvl(canvas);
            //paintScore(canvas);
            paintTemps(canvas);
        }

    }

    //Check si la partie est gagner !
    private boolean checkIsWon() {
        boolean stop = false, IsWon = false;
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                if (carte[i][j] != 0) {
                    stop = true;
                    break;
                } else {
                    if (i == 13 && j == 9) {
                        IsWon = true;
                    }
                }
            }
            if (stop == true) {
                IsWon = false;
                break;
            }
        }
        return IsWon;
    }

    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged " + width + " - " + height);
        startGame(sound,repereOldGame);
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
        while (in)
        {
            if(newmap == false) {
                try {
                    cv_thread.sleep(50);
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
            else
            {
                cv_thread.interrupt();
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

        //Test si "Gagner" est afficher pour initaliser une nouvel carte
        if(newmap == true)
        {
            startGame(sound,repereOldGame);
            newmap = false;
        }

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
class Chrono extends CountDownTimer
{
    long temps;
    boolean finTemps;
    public Chrono(long startTime, long interval)
    {
      super(startTime, interval);
        finTemps = false;
    }

    @Override
    public void onFinish()
    {
        finTemps = true;
    }
    @Override
    public void onTick(long millisUntilFinished)
    {
        temps = millisUntilFinished / 1000;
    }
}