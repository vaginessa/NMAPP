package noonecares.whatever.nmapp.Dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import noonecares.whatever.nmapp.R;


public class CustomProgressDialog {

	public static Dialog ProgressDialog(Context currentContext, String dialogText){
		Dialog dialog = null;
		if(currentContext != null)
		{
			dialog = new Dialog(currentContext);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.progress);
			dialog.setCanceledOnTouchOutside(false);
			dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
			ProgressBar progressBar=(ProgressBar)dialog.findViewById(R.id.progressBar);
			ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500);
			animation.setDuration (5000);
			animation.setInterpolator (new DecelerateInterpolator());
			animation.start ();
			TextView text = (TextView) dialog.findViewById(R.id.progressText);
			dialog.setCancelable(true);
			text.setText(dialogText);
		}
		return dialog;
	}
}
