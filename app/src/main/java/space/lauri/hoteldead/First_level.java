package space.lauri.hoteldead;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

class MusicHandler
{
    private MediaPlayer mediaPlayer;
    private Context context;
    private int iVolume;

    private final static int INT_VOLUME_MAX = 100;
    private final static int INT_VOLUME_MIN = 0;
    private final static float FLOAT_VOLUME_MAX = 1;
    private final static float FLOAT_VOLUME_MIN = 0;

    public MusicHandler(Context context)
    {
        this.context = context;
    }

    public void load(String path, boolean looping)
    {
        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(new File(path)));
        mediaPlayer.setLooping(looping);
    }

    public void load(int address, boolean looping)
    {
        mediaPlayer = MediaPlayer.create(context, address);
        mediaPlayer.setLooping(looping);
    }

    public void play(int fadeDuration)
    {
        //Set current volume, depending on fade or not
        if (fadeDuration > 0)
            iVolume = INT_VOLUME_MIN;
        else
            iVolume = INT_VOLUME_MAX;

        updateVolume(0);

        //Play music
        if(!mediaPlayer.isPlaying()) mediaPlayer.start();

        //Start increasing volume in increments
        if(fadeDuration > 0)
        {
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    updateVolume(1);
                    if (iVolume == INT_VOLUME_MAX)
                    {
                        timer.cancel();
                        timer.purge();
                    }
                }
            };

            // calculate delay, cannot be zero, set to 1 if zero
            int delay = fadeDuration/INT_VOLUME_MAX;
            if (delay == 0) delay = 1;

            timer.schedule(timerTask, delay, delay);
        }
    }

    public void pause(int fadeDuration)
    {
        //Set current volume, depending on fade or not
        if (fadeDuration > 0)
            iVolume = INT_VOLUME_MAX;
        else
            iVolume = INT_VOLUME_MIN;

        updateVolume(0);

        //Start increasing volume in increments
        if(fadeDuration > 0)
        {
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    updateVolume(-1);
                    if (iVolume == INT_VOLUME_MIN)
                    {
                        //Pause music
                        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                        timer.cancel();
                        timer.purge();
                    }
                }
            };

            // calculate delay, cannot be zero, set to 1 if zero
            int delay = fadeDuration/INT_VOLUME_MAX;
            if (delay == 0) delay = 1;

            timer.schedule(timerTask, delay, delay);
        }
    }

    private void updateVolume(int change)
    {
        //increment or decrement depending on type of fade
        iVolume = iVolume + change;

        //ensure iVolume within boundaries
        if (iVolume < INT_VOLUME_MIN)
            iVolume = INT_VOLUME_MIN;
        else if (iVolume > INT_VOLUME_MAX)
            iVolume = INT_VOLUME_MAX;

        //convert to float value
        float fVolume = 1 - ((float) Math.log(INT_VOLUME_MAX - iVolume) / (float) Math.log(INT_VOLUME_MAX));

        //ensure fVolume within boundaries
        if (fVolume < FLOAT_VOLUME_MIN)
            fVolume = FLOAT_VOLUME_MIN;
        else if (fVolume > FLOAT_VOLUME_MAX)
            fVolume = FLOAT_VOLUME_MAX;

        mediaPlayer.setVolume(fVolume, fVolume);
    }
}

public class First_level extends AppCompatActivity {
    int i = 0;//кол-во сообщений
    int eve = 0; //номер евента,после
    int[] paths = {-1,-1,-1};
    int wasz=0;
    int was2=0;
    int elkipalki=0;
    int wasz1=0;
    int wasz2=0;
    int endi = 0;
    MusicHandler music = new MusicHandler(this);

    void check(String [] goon, final int[] path, boolean eveplus) //выбирает в списке мессаджей то, на которое была нажата кнопка
    {
        yousay(goon[path[0]]);
        paths[eve] = path[0];
        if (eve == 1 && paths[0] == 0 && wasz==0)
            wasz=1;
        if (eveplus)
            eve++;
        if (paths[1] == 0 && wasz==1 && elkipalki==0)
        {
            npcsay(goon[1], "Петер Глебски");
            paths[1] = -1;
            elkipalki=1;
        }

    }

    void makebutwork(Button fir, int way, LinearLayout[] bs, String[] goon,boolean eveplus) {
        LinearLayout gameWindow = findViewById(R.id.mesWin);
        final int[] path = {0};
        fir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path[0] = way;
                for (LinearLayout b : bs) {
                    gameWindow.removeView(b); //убирает лишние лейауты
                }
                i = 0;
                check(goon,path,eveplus); //запуск сообщений
            }
        });
    }


    void npcsay(String text, String hero) //публикует сообщение npc
    {
        LinearLayout gameWindow = findViewById(R.id.mesWin);
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.message, gameWindow, false);
        TextView username = view.findViewById(R.id.name);
        username.setText(hero);
        TextView ttxt = view.findViewById(R.id.point);
        ttxt.setText(text);
        gameWindow.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    void yousay(String text) //публикует сообщение игрока
    {
        LinearLayout gameWindow = (LinearLayout) findViewById(R.id.mesWin);
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.messageme, gameWindow, false);
        TextView ttxt = view.findViewById(R.id.point);
        ttxt.setText(text);
        gameWindow.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    void makechoice(String [] choice, int len, String [] firmes, boolean eveplus) //создает окно выбора, нужно передать
            //список текстов кнопок, кол-во кнопок, последующие мессаджт
    {
        LinearLayout[] buttons;
        buttons = new LinearLayout[len];
        LinearLayout gameWindow = (LinearLayout) findViewById(R.id.mesWin);
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.choicewin, gameWindow, false);
        buttons[0] = view;
        Button ch1 = view.findViewById(R.id.buttonCH);
        ch1.setText(choice[0]);
        makebutwork(ch1,0,buttons,firmes,eveplus);
        for (int q=1; q<len; q++)
        {
            LinearLayout view1 = (LinearLayout) getLayoutInflater().inflate(R.layout.choicewin, gameWindow, false);
            Button ch2 = view1.findViewById(R.id.buttonCH);
            buttons[q] = view1;
            ch2.setText(choice[q]);
            makebutwork(ch2,q,buttons,firmes,eveplus);
            gameWindow.addView(view1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        gameWindow.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    void beginning ()
    {
        int howm = 6;
        String[] replics  = {"Такой вещи, как идеальный текст, не существует. Как не существует идеального отчаяния.","Устал. Я знаю — нехорошо так говорить и даже думать, но до чего же в наше время сложно устроиться таким образом, чтобы хоть на неделю, хоть на сутки, хоть на несколько часов остаться в одиночестве!",
                "Нет, я люблю своих детей, я люблю свою жену, у меня нет никаких злых чувств к моим родственникам, и большинство моих друзей и знакомых вполне тактичные и приятные в общении люди.",
                "Но когда изо дня в день, из часа в час они непрерывно толкутся около меня, сменяя друг друга, и нет никакой, ни малейшей возможности прекратить эту толчею, отделить себя от всех, запереться, отключиться…",
                "Может, уехать? Ускользнуть от удушливой повседневности в туманное никуда...",
                "Згут,например, всё рассказывает об одном местечке. Отель \"У погибшего альпиниста\", Отель \"У погибшего альпиниста\". Твердит одно и то же."};
        String[] who = {"Харуки Мураками","Петер Глебски", "Петер Глебски", "Петер Глебски", "Петер Глебски", "Петер Глебски"};
        String [] choice = {"Згут?", "продолжить слушать"};
        String [] firmes = {"Так, приятель, ну и коллега по совместительству. Специалируется на медвежатниках.",
                "Съезжу, проветрюсь, освобожусь от запахов затлых бумаг и провонявшиз сургучом коридоров. Во всяком случае, для меня две недельки отчужденности и одиночества — это как раз то, что нужно."};
        if (i < howm)
            npcsay(replics[i], who[i]);
        else if (i==howm){
            makechoice(choice,2,firmes,true);
        }
    }

    void checkingzgut()
    {

        String [] choice = {"загуглить", "продолжить слушать"};
        String [] firmes = {"Медвежатник — специализируется на кражах из сейфов. Медвежатники открывают сейфы отмычками.", "Съезжу, проветрюсь, освобожусь от запахов затлых бумаг и провонявшиx сургучом коридоров. Во всяком случае, для меня две недельки отчужденности и одиночества — это как раз то, что нужно."};
        makechoice(choice,2,firmes,false);
    }

    void firstend()
    {
        String [] choice = {"Не стоит, в горах опасно, да и семью оставлять не хочется", "А почему, собственно, нет? Конечно нужно съездить! Тем более, горы"};
        String [] firmes = {"Не стоит, в горах опасно, да и семью оставлять не хочется", "А почему, собственно, нет? Конечно нужно съездить! Тем более, горы"};
        makechoice(choice,2,firmes,true);
    }

    void bye1()
    {
        if (i==0)
            npcsay("Да в том-то и проблема. И работы много, конечно... Поворчал и хватит.","Петер Глебски");
        if (i==1)
        {
            npcsay("История не заканчивается, пока не произойдет наихудшее", "Фридрих Дюрренматт");
            npcsay("Поздравляю! Вы открыли первую концовку :)", "Дарья Лашутина");
        }
    }

    void letsgo()
    {
        String[] replics  = {"В дорогу!",
                "Только остановил машину, вылез и снял черные очки. Всё так, как рассказывал Згут. Двухэтажный отель, желтый с зеленым, над крыльцом - траурная вывеска: «У ПОГИБШЕГО АЛЬПИНИСТА».",
        "Высокие ноздреватые сугробы по сторонам крыльца утыканы разноцветными лыжами — я насчитал семь штук, одна была с ботинком.",
        "С крыши свисают мутные гофрированные сосульки толщиной в руку. О! На крыльце появился лысый коренастый человек в рыжем меховом жилете поверх ослепительной лавсановой рубашки.",
        "Тяжелой медлительной поступью он приблизился и остановился передо мною. У него была грубая красная физиономия и шея борца-тяжеловеса. На меня он не смотрел.",
        "Его меланхолический взгляд был устремлен куда-то в сторону и исполнен печального достоинства. Несомненно, это был сам Алек Сневар, владелец отеля, долины и Бутылочного Горлышка.",
        "Там... Вон там это произошло. На той вершине... Лопнул карабин... Двести метров он летел по вертикали вниз, к смерти, и ему не за что было зацепиться на гладком камне.",
        "Может быть, он кричал. Никто не слышал его. Может быть, он молился. Его слышал только бог. Потом он достиг склона, и мы здесь услышали лавину, рев разбуженного зверя, и земля дрогнула, когда он грянулся о нее вместе с сорока двумя тысячами тонн кристаллического снега…",
                "Позвольте мне погрузиться в прошлое."};
        String[] who = {"Петер Глебски", "Петер Глебски", "Петер Глебски", "Петер Глебски", "Петер Глебски", "Петер Глебски","Алек Сневар","Алек Сневар","Алек Сневар"};
        String [] choice = {"достать бутылки от Згута", "помолчать"};
        String [] firmes = {"Привет от инспектора Згута.",
                "история альпиниста"};
        if (i < replics.length)
            npcsay(replics[i], who[i]);
        else if (i==replics.length){
            makechoice(choice,2,firmes,true);
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_level);
        LinearLayout gameWindow = (LinearLayout) findViewById(R.id.mesWin);
        music.load(R.raw.sunnymount,true);
        music.play(1);
        gameWindow.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //AlertDialog alertDialog = new AlertDialog.Builder(this)
                  //      .setMessage(String.valueOf(eve))
                    //    .show();
                if (eve == 0)
                {
                    beginning();
                    i++;
                }
                else if (eve == 1 && paths[0] == 0 && wasz==0 && wasz1==0)
                {
                    checkingzgut();
                    wasz1=1;
                }
                else if (((eve == 1 && paths[0] == 0 && wasz==1) || (eve==1&&paths[0]==1)) && wasz2==0)
                {
                    wasz2=1;
                    firstend();
                }
                else if (eve == 2 && paths[1]==0 && i<2)
                {
                    bye1();
                    i++;
                }
                else if (eve == 2 && paths[1] == 1)
                {
                    letsgo();
                    i++;
                }

            }
            return false;
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        music.pause(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        music.play(1);
    }

}

