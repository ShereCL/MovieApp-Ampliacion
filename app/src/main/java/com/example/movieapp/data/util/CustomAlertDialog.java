package com.example.movieapp.data.util;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.movieapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomAlertDialog {

    /**
     * Dialogo con botones y con acción
     */
    public static void show(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            Runnable onPositive,
            Runnable onNegative
    ) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (d, w) -> {
                    if (onPositive != null) onPositive.run();
                })
                .setNegativeButton(negativeText, (d, w) -> {
                    if (onNegative != null) onNegative.run();
                })
                .create();

        dialog.show();
        styleMessage(context, dialog);
    }

    /**
     * dialogo con  botones y sin acción
     */
    public static void show(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            Runnable onPositive
    ) {
        show(context, title, message, positiveText, negativeText, onPositive, null);
    }

    /**
     * Aceptar/Cancelar
     */
    public static void show(
            Context context,
            String title,
            String message,
            Runnable onPositive
    ) {
        show(context, title, message, context.getString(R.string.aceptar), context.getString(R.string.cancelar), onPositive, null);
    }

    /**
     * Dialogo con botones
     */
    public static void show(
            Context context,
            String title,
            String message,
            String positiveText,
            String negativeText,
            boolean cancelable,
            Runnable onPositive,
            Runnable onNegative
    ) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText, (d, w) -> {
                    if (onPositive != null) onPositive.run();
                })
                .setNegativeButton(negativeText, (d, w) -> {
                    if (onNegative != null) onNegative.run();
                })
                .create();

        dialog.show();
        styleMessage(context, dialog);
    }

    /**
     * Informativo
     */
    public static void showInfo(
            Context context,
            String title,
            String message,
            String buttonText,
            Runnable onAccept
    ) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.CustomAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonText, (d, w) -> {
                    if (onAccept != null) onAccept.run();
                })
                .create();

        dialog.show();
        styleMessage(context, dialog);
    }

    /**
     * Dialog informativo
     */
    public static void showInfo(Context context, String title, String message) {
        showInfo(context, title, message, context.getString(R.string.entendido), null);
    }

    // Aplica color al texto del mensaje
    private static void styleMessage(Context context, AlertDialog dialog) {
        // Mensaje
        TextView msgView = dialog.findViewById(android.R.id.message);
        if (msgView != null) {
            msgView.setTextColor(ContextCompat.getColor(context, R.color.textoGrisMedio));
            msgView.setTextSize(14f);
            msgView.setLineSpacing(4f, 1f);
        }

        // Título
        int titleId = context.getResources().getIdentifier("alertTitle", "id", "android");
        TextView titleView = dialog.findViewById(titleId);
        if (titleView != null) {
            titleView.setTextColor(ContextCompat.getColor(context, R.color.textoNegro));
            titleView.setTextSize(17f);
        }
    }
}
