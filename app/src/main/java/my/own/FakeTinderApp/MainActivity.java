package my.own.FakeTinderApp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.huxq17.swipecardsview.SwipeCardsView;

import java.util.ArrayList;
import java.util.List;

import my.own.FakeTinderApp.Adapter.CardAdapter;
import my.own.FakeTinderApp.Model.Model;

public class MainActivity extends AppCompatActivity {

    private SwipeCardsView swipeCardsView;
    private Button doneBtn;
    private List<Model> modelList = new ArrayList<>();
    private Subject subject;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    private final String[] puppies = new String[]{"https://images.pexels.com/photos/356378/pexels-photo-356378.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
    "https://images.pexels.com/photos/39317/chihuahua-dog-puppy-cute-39317.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
    "https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
    "https://images.pexels.com/photos/460823/pexels-photo-460823.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/356378/pexels-photo-356378.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/39317/chihuahua-dog-puppy-cute-39317.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/460823/pexels-photo-460823.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/356378/pexels-photo-356378.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/39317/chihuahua-dog-puppy-cute-39317.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/460823/pexels-photo-460823.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/356378/pexels-photo-356378.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/39317/chihuahua-dog-puppy-cute-39317.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350",
            "https://images.pexels.com/photos/460823/pexels-photo-460823.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=350"};


    Handler handler;
    int Seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int subjectId = getIntent().getIntExtra("subjectNum",0);
        subject = new Subject(subjectId);

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
//        modelList.add(new Model("https://i.pinimg.com/originals/43/19/be/4319be65da73451d2d12ef105daff213.jpg"  ));
//        modelList.add(new Model("https://i.pinimg.com/originals/4e/d1/ef/4ed1efa48980e43cccd66bcde7585f59.jpg" ));
//        modelList.add(new Model("https://www.thenology.com/wp-content/uploads/2014/09/iron-man-comic-mobile-wallpaper-1080x1920-6212-108.jpg"  ));

        for (String pup: puppies){
            modelList.add(new Model(pup));
        }
        CardAdapter cardAdapter = new CardAdapter(modelList,this);
        swipeCardsView.setAdapter(cardAdapter);
    }
}
