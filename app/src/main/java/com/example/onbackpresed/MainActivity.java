package com.example.onbackpresed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // Import View for OnClickListener
import android.widget.Button; // Import Button
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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    // Declare the buttons
    Button button1, button2, button3;
    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    private static final String TAG = "MyActivity";
    private InterstitialAd interstitialAd;
    private boolean adIsLoading;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Make sure this matches your layout file name
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reldva), (v, insets) -> { // Changed R.id.main to R.id.reldva based on your XML
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// ///////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////////////

        googleMobileAdsConsentManager =
                GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
                this,
                consentError -> {
                    if (consentError != null) {
                        // Consent not obtained in current session.
                        Log.w(
                                TAG,
                                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                    }



                    if (googleMobileAdsConsentManager.canRequestAds()) {
                        initializeMobileAdsSdk();
                    }

                    if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                        // Regenerate the options menu to include a privacy setting.
                        invalidateOptionsMenu();
                    }
                });

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }

        /// ///////////////////////////////////////////////////////////

        // Initialize the buttons from the layout
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        // Set OnClickListener for button1
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when button1 is clicked
                Toast.makeText(MainActivity.this, "Button 1 Clicked", Toast.LENGTH_SHORT).show();
                // You can add any other code here, like starting a new activity, etc.
                // Create an Intent to start MainActivity2
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent); // Start the new activity
            }
        });

        // Set OnClickListener for button2
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when button2 is clicked
                Toast.makeText(MainActivity.this, "Button 2 Clicked", Toast.LENGTH_SHORT).show();

                // Create an Intent to start MainActivity2
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent); // Start the new activity

            }
        });

        // Set OnClickListener for button3
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when button3 is clicked
                Toast.makeText(MainActivity.this, "Button 3 Clicked", Toast.LENGTH_SHORT).show();
                // Create an Intent to start MainActivity2
                Intent intent = new Intent(MainActivity.this, MainActivity4.class);
                startActivity(intent); // Start the new activity
            }
        });


        // --- Your existing OnBackPressedCallback ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(MainActivity.this, "Back button pressed!", Toast.LENGTH_SHORT).show();
                // if (isEnabled()) {
                //    setEnabled(false);
                //    getOnBackPressedDispatcher().onBackPressed();
                // }

                finish();
            }
        });
    }

   /// //////////////////////////////////////////////////////////////////////////////
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
                       MainActivity.this.interstitialAd = interstitialAd;
                       // [START_EXCLUDE silent]
                       adIsLoading = false;
                       Toast.makeText(MainActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                       // [START set_fullscreen_callback]
                       interstitialAd.setFullScreenContentCallback(
                               new FullScreenContentCallback() {
                                   @Override
                                   public void onAdDismissedFullScreenContent() {
                                       // Called when fullscreen content is dismissed.
                                       Log.d(TAG, "The ad was dismissed.");
                                       // Make sure to set your reference to null so you don't
                                       // show it a second time.
                                       MainActivity.this.interstitialAd = null;
                                   }
                               });
                   }
               });
       // [END load_ad]
   }
   private void initializeMobileAdsSdk() {
       if (isMobileAdsInitializeCalled.getAndSet(true)) {
           return;
       }

       // Set your test devices.
       MobileAds.setRequestConfiguration(
               new RequestConfiguration.Builder()
                       .setTestDeviceIds(Arrays.asList(TEST_DEVICE_HASHED_ID))
                       .build());

//       new Thread(
//               () -> {
//                   // Initialize the Google Mobile Ads SDK on a background thread.
//                   MobileAds.initialize(this, initializationStatus -> {
//                   });
//
//                   // Load an ad on the main thread.
//                   runOnUiThread(() -> loadAd());
//               })
//               .start();

   }



   /// ////////////////////////////////////////////////////////////////////////////////////////////////////////
}
