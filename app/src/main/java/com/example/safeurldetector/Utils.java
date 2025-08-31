
package com.example.safeurldetector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import android.util.Log;

public class Utils {

    public static void openInChrome(Context context, String url) {
        try {
            Intent chromeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    .setPackage("com.android.chrome")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (chromeIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(chromeIntent);
            } else {
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fallbackIntent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to open browser", Toast.LENGTH_SHORT).show();
            Log.e("Utils", "Error redirecting to Chrome: " + e.getMessage());
        }
    }

    public static void openInDefaultBrowser(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to open browser", Toast.LENGTH_SHORT).show();
            Log.e("Utils", "Error opening in default browser: " + e.getMessage());
        }
    }
}
