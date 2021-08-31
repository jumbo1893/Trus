package com.jumbo.trus;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.jumbo.trus.user.LoginActivity;

public class Welcome extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();

        // Nastavíme a spustíme animaci pro logo
        ImageView imgvwSpsoa = findViewById(R.id.imageView2);
        Animation animSpsoaLogo = AnimationUtils.loadAnimation(this, R.anim.welcome_sc);
    /* Animace se „aplikuje“ na imageView, což je ID našeho loga.
       Veškeré potřebné nastavení pro samotnou animaci je v souboru welcome_sc
    */
        imgvwSpsoa.startAnimation(animSpsoaLogo); // spuštění samotné animace

        // Vytvoření druhého vlákna s časovačem, po uplynutí limitu je spuštěna další aktivita
        Thread thrdWlcmscrnDelay = new Thread() {
            public void run() {
                try {
                    sleep(3500);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        thrdWlcmscrnDelay.start();
    }
}
