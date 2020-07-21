package com.somercelik.catchtheworm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

//Created by @somercelik

public class MainActivity extends AppCompatActivity {
    int score, highScore;
    TextView timeTextView, scoreTextView, highScoreTextView;
    ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9;
    ImageView[] imageViewArray;
    Handler handler;
    Runnable runnable;
    Button restartButton;
    SharedPreferences highScoreSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        highScoreSharedPreference = this.getSharedPreferences("com.somercelik.catchtheworm", Context.MODE_PRIVATE);
        highScore = highScoreSharedPreference.getInt("highScore", 0);                          //En yüksek puan highScore key'i ile saklanmış.
        //Bu değeri highScore değişkenine aldık.

        imageView1 = findViewById(R.id.imageView1);                                                 //Component'lar initialize edildi.
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        //Gösterilip gizlenecek imageView'lara tek bir yerden müdahale için hepsini bir diziye aktardık.
        imageViewArray = new ImageView[]{imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9};
        //App açılır açılmaz tüm imageView'ları gizliyoruz.
        hideImages();
        restartButton = findViewById(R.id.restartButton);
        restartButton.setVisibility(View.INVISIBLE);                    //Restart butonu oyun bitmeden görünür hale gelmez çünkü Runnable çalışırken yenisinin
        //devreye girmesi ile oyun çökmektedir. Bu şekilde bu bug önlenmiştir.

        timeTextView = findViewById(R.id.timeTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);
        highScoreTextView.setText("HI: " + highScore);      //Aldığımız highScore değerini sol üstteki textView'a yazdırıyoruz.

        new CountDownTimer(10000, 1000) {       //Bir CountDownTimer yardımıyla 10 saniyelik bir geri sayım oluşturuyoruz.
            @Override
            public void onTick(long l) {                                     //Her 1000ms'te bir üstte yazan Time: ... kısmına kalan süreyi yazdırıyoruz.
                timeTextView.setText("Time: " + l / 1000);
            }

            @Override
            public void onFinish() {                                         //Süre bittiğinde yapılacaklar.
                timeTextView.setText("Time is up!");

                if (score > highScore) {
                    highScoreSharedPreference.edit().putInt("highScore", score).apply();        //Eğer kullanıcı en yüksek skoru geçmişse highScore güncellenip
                    //sharedPreferences değişkenine yeni değer girilir.
                }

                handler.removeCallbacks(runnable);                                                  //Oyun işleyişini sağlayan döngü durur.

                for (ImageView image : imageViewArray) {                                            //Tüm solucanlar görünmez hale getirilir.
                    image.setVisibility(View.INVISIBLE);
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);     //Kullanıcıdan yeniden başlatımak isteyip istemediği bir AlertDialog
                alert.setTitle("Restart");                                                          //ile sorulur. Kendi skoru ve en yüksek skor da bu pencerede iletilir.
                alert.setMessage("Your score: " + score + " Highest: " + highScore);
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {                   //Yeniden başlatılmazsa skor ekranı gösterilir ve
                        Toast.makeText(MainActivity.this, "Game over!", Toast.LENGTH_LONG).show();  //RESTART butonu aktifleştirilir.
                        restartButton.setVisibility(View.VISIBLE);
                    }
                });
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {         //Onaylandığında oyun yani Activity yeniden başlar.
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartActivity();
                    }
                });
                alert.setCancelable(false);                             //Bu ayarlanmadığında kullanıcı boş ekrana bakacağından ve oyunu kapatıp tekrar açması
                alert.show();                                           //Gerektiğinden sadece NO ve YES seçeneklerini seçebilmesi sağlanarak bu hatalı durum önlenmiştir.

            }
        }.start();

    }

    public void restartOnClick(View view) {                             //RESTART butonu tarafından kullanılacak metod
        restartActivity();
    }

    private void restartActivity() {                                        //Activity'yi yeniden başlatan metod.
        Intent intent = getIntent();                                    //Bir intent initialize edilir.
        finish();                                                       //Varolan Activity gereksiz olduğu için sonlandırılır.
        startActivity(intent);                                          //Aynı Activity sıfırdan başlatılır.
    }

    public void upScore(View view) {                                    //Skoru artırıp textView'a yazdıran metod.
        score++;
        scoreTextView.setText("Score: " + score);
    }

    public void hideImages() {                                          //Başlangıçta çalışan ve tüm image'ları gizleyen metod.
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                for (ImageView image : imageViewArray) {                //Önce tüm image'lar gizlenir.
                    image.setVisibility(View.INVISIBLE);
                }
                Random random = new Random();
                int shownImage = random.nextInt(9);             //0-8 dahili aralığında rastgele bir sayı ile
                imageViewArray[shownImage].setVisibility(View.VISIBLE);//Dizideki bir image seçilerek ekranda gösterilir.
                handler.postDelayed(this, 500);            //Yarım saniyede bir run() bloğu işletilir.
            }
        };
        handler.post(runnable);

    }

}