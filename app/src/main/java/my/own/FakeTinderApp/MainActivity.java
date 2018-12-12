package my.own.FakeTinderApp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.huxq17.swipecardsview.SwipeCardsView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import my.own.FakeTinderApp.Adapter.CardAdapter;
import my.own.FakeTinderApp.Model.Model;

public class MainActivity extends AppCompatActivity {

    private SwipeCardsView swipeCardsView;
    private Button doneBtn;
    private List<Model> modelList = new ArrayList<>();
    private Subject subject;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    private String[] faces;
    private String gender;


    Handler handler;
    int Seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int subjectId = getIntent().getIntExtra("subjectNum",0);
        gender = getIntent().getStringExtra("gender");

        subject = new Subject(subjectId);
        AssetManager myAssets = getAssets();
        try {
            if (gender.equals("male")){
                faces = Objects.requireNonNull(myAssets.list("malePics"));
            } else{
                faces = myAssets.list("femalePics");
            }
        } catch (IOException e) {
        }

        doneBtn = findViewById(R.id.doneBtn);
        doneBtn.setVisibility(View.INVISIBLE);
        swipeCardsView = findViewById(R.id.swipeCardsView);
        swipeCardsView.retainLastCard(false);
        swipeCardsView.enableSwipe(true);
        getData();
        handler = new Handler() ;
        start();

        swipeCardsView.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
            @Override
            public void onShow(int index) {
                reset();
                start();
            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type) {
                switch (type) {
                    case LEFT:
                        subject.swipeLeft();
                        break;
                    case RIGHT:
                        subject.swipeRight();
                        break;
                }
                stop();
                if (index == modelList.size()-1){
                    doneBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {
            }



        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StartSubject.class);
                intent.putExtra("prevSubject",subject.getId());
                intent.putExtra("history",subject.toString());
                subject.clear();
                reset();
                startActivity(intent);

            }
        });

    }

    private void start(){
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable,0);

    }

    private void stop(){
        handler.removeCallbacks(runnable);
        subject.addTime(Seconds);
        reset();
    }

    private void reset(){
        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
    }



    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            handler.postDelayed(this, 0);
        }
    };

    private void getData() {
        makeShuffleList(faces);
        CardAdapter cardAdapter = new CardAdapter(modelList,gender,this);
        swipeCardsView.setAdapter(cardAdapter);
    }

    private void makeShuffleList(String[] faces){
        List<Model> list = new ArrayList<>();
        for (String face: faces){
            list.add(new Model(face));
        }
        Collections.shuffle(list);
        modelList.addAll(list);

    }
}
