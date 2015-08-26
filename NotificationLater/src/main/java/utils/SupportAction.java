package utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.kimcy929.notificationlater.R;


/**
 * Created by TeddyBoy on 23/05/2015.
 */
public class SupportAction {

    private Context context;

    public SupportAction(Context context) {
        this.context = context;
    }

    public void shareApplication(String linkApp) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", context.getResources()
                .getString(R.string.app_name));
        intent.putExtra("android.intent.extra.TEXT", linkApp);
        context.startActivity(Intent.createChooser(intent, "Share App Via"));
    }

    public void moreApplication() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:Kimcy929"));
        context.startActivity(intent);
    }

    public void searchOnMarket(String appPackageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + appPackageName));
        context.startActivity(intent);
    }

    public void sendFeedBack() {
        String[] TO = {"your_email_@gmail.com"};
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.setData(Uri.parse("mailto:"));
        intentEmail.setType("message/rfc822");

        String subject = "Your subject to "
                + context.getResources().getString(R.string.app_name)
                + " V_" + Utils.getVersionName(context) + "("
                + Build.MANUFACTURER + " " + Build.DEVICE + " AV " + Build.VERSION.RELEASE + ")";
        String feedbackContent = "Please describe your problem as detail as possible, and we will try to fix it as fast as we can. Thank you for your feedback!";
        intentEmail.putExtra(Intent.EXTRA_EMAIL, TO);
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
        intentEmail.putExtra(Intent.EXTRA_TEXT, feedbackContent);

        try {
            context.startActivity(Intent.createChooser(intentEmail, "Send FeedBack..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "There is no email client installed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void startApp(String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }
}
