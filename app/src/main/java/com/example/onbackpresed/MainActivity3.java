package com.example.onbackpresed;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity3 extends AppCompatActivity {

    /// /////////////////////////////////////////////////////////////////////////////
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    private static final String TAG = "MyActivity";
    private InterstitialAd interstitialAd;
    private boolean adIsLoading;
    /// ///////////////////////////////////////////////////////////////////////////////
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// ///////////////////////////////////

        loadAd();

        /// ///////////////////////////////////


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                /////////////////////////////////////////
                Toast.makeText(MainActivity3.this, "BACK EVENT", Toast.LENGTH_SHORT).show();
                /// /////////////////////////////////////
                if (interstitialAd != null) {
                    showInterstitial();
                } else {

                    finish();
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////
    public void loadAd() {
        // Request a new ad if one isn't already loaded.
        if (adIsLoading || interstitialAd != null) {
            return;
        }
        adIsLoading = true;
        // [START load_ad]
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d(TAG, "Ad was loaded.");
                        MainActivity3.this.interstitialAd = interstitialAd;
                        // [START_EXCLUDE silent]
                        adIsLoading = false;
                        Toast.makeText(MainActivity3.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        // [START set_fullscreen_callback]
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        Log.d(TAG, "The ad was dismissed.");
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity3.this.interstitialAd = null;
                                    }
                                });
                    }
                });
        // [END load_ad]
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise restart the game.
        // [START show_ad]
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {

            if (googleMobileAdsConsentManager.canRequestAds()) {
                loadAd();
            }
            // [END_EXCLUDE]
        }
        // [END show_ad]
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////////////////
}