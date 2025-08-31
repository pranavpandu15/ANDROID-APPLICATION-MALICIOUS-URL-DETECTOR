package com.example.safeurldetector;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "UrlSafetyChecker";
    public static final String GOOGLE_SAFE_BROWSING_API_KEY = "AIzaSyC_iyN7p5gLI6nVc5nNerqBuPkU0SEbSMk"; // Insert your API key here
    private static final String EXTRA_FROM_SAFETY_CHECKER = "from_safety_checker";

    private TextView urlTextView;
    private String clickedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlTextView = findViewById(R.id.urlTextView);

        // Prevent redirect loop
        if (getIntent().getBooleanExtra(EXTRA_FROM_SAFETY_CHECKER, false)) {
            Log.d(TAG, "Received forwarded URL, opening in browser...");
            openInBrowser(getIntent().getDataString());
            finish();
            return;
        }

        // Handle incoming URL
        Uri data = getIntent().getData();
        if (data != null) {
            clickedUrl = data.toString();
            urlTextView.setText(getString(R.string.checking_url, clickedUrl));
            new CheckUrlTask().execute(clickedUrl);
        } else {
            Toast.makeText(this, R.string.no_url_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class CheckUrlTask {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Handler handler = new Handler(Looper.getMainLooper());

        public void execute(String url) {
            executor.execute(() -> {
                try {
                    boolean isSafe = GoogleSafeBrowsingAPI.isUrlSafe(url, GOOGLE_SAFE_BROWSING_API_KEY);
                    handler.post(() -> onPostExecute(isSafe));
                } catch (Exception e) {
                    Log.e(TAG, "Error checking URL safety: " + e.getMessage(), e);
                    handler.post(() -> onPostExecute(null));
                }
            });
        }

        private void onPostExecute(Boolean isSafe) {
            if (isSafe == null) {
                Toast.makeText(MainActivity.this, R.string.url_check_failed, Toast.LENGTH_LONG).show();
                urlTextView.setText(getString(R.string.url_check_failed_detail));
                logAccess(clickedUrl, "error");
                return;
            }

            if (isSafe) {
                logAccess(clickedUrl, "safe");
                openInBrowser(clickedUrl);
            } else {
                logAccess(clickedUrl, "unsafe");
                showWarningDialog(clickedUrl);
            }
        }
    }

    private void showWarningDialog(String url) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.warning_dialog_title)
                .setMessage(getString(R.string.unsafe_url_warning, url))
                .setPositiveButton(R.string.proceed_anyway, (dialog, which) -> {
                    logAccess(url, "proceeded_after_warning");
                    openInBrowser(url);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    logAccess(url, "blocked");
                    Toast.makeText(this, R.string.navigation_blocked, Toast.LENGTH_SHORT).show();
                })
                .setOnDismissListener(dialog -> finish())
                .show();
    }

    private void openInBrowser(String url) {
        Utils.openInChrome(this, url);
        finish();
    }

    private void logAccess(String url, String status) {
        Log.i(TAG, "URL Access Log: " + url + " | Status: " + status);
        // Optional: Log to Firebase or analytics
    }
}
