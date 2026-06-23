package com.example.movieapp.data.util;

import android.content.Context;

import com.example.movieapp.R;

public class Validadores {

    //Validar de email
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailPattern = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return email.matches(emailPattern);
    }

    //Validar contraseña
    public static boolean validarPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean tieneLetra = password.matches(".*[a-zA-Z].*");
        boolean tieneNumero = password.matches(".*\\d.*");

        return tieneLetra && tieneNumero;
    }

    //Mensajes de error para el mail
    public static String getMensajeErrorEmail(Context context, String email) {
        if (email == null || email.trim().isEmpty()) {
            return context.getString(R.string.errorEmailVacio);
        }
        if (!email.contains("@")) {
            return context.getString(R.string.errorEmailArroba);
        }
        if (email.indexOf("@") == 0 || email.indexOf("@") == email.length() - 1) {
            return context.getString(R.string.errorEmailArrobaVacio);
        }
        if (!email.substring(email.indexOf("@")).contains(".")) {
            return context.getString(R.string.errorEmailPunto);
        }
        return context.getString(R.string.errorEmailFormato);
    }


    //Mensajes de error para la contraseña
    public static String getMensajeErrorPassword(Context context, String password) {
        if (password == null || password.isEmpty()) {
            return context.getString(R.string.errorPasswordVacia);
        }
        if (password.length() < 8) {
            return context.getString(R.string.errorPasswordCorta);
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            return context.getString(R.string.errorPasswordLetra);
        }
        if (!password.matches(".*\\d.*")) {
            return context.getString(R.string.errorPasswordNumero);
        }
        return context.getString(R.string.errorPasswordInvalida);
    }

}
