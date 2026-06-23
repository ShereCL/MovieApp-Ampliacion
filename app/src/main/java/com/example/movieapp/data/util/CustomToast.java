package com.example.movieapp.data.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.R;

public class CustomToast {

    public enum Type { SUCCESS, ERROR, INFO }

    public static void show(Context context, String message) {
        show(context, message, Type.SUCCESS);
    }

    @SuppressWarnings("deprecation")
    public static void show(Context context, String message, Type type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_toast, null);

        LinearLayout container = view.findViewById(R.id.toastContainer);
        ImageView icon = view.findViewById(R.id.ivToastIcon);
        TextView text = view.findViewById(R.id.tvToastMessage);

        text.setText(message);

        switch (type) {
            case ERROR:
                container.setBackgroundResource(R.drawable.bg_toast_error);
                icon.setImageResource(R.drawable.ic_error);
                break;
            case INFO:
                container.setBackgroundResource(R.drawable.bg_toast_info);
                icon.setImageResource(R.drawable.ic_info);
                break;
            case SUCCESS:
            default:
                container.setBackgroundResource(R.drawable.bg_custom_toast);
                icon.setImageResource(R.drawable.ic_check);
                break;
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 120);
        toast.show();
    }
}