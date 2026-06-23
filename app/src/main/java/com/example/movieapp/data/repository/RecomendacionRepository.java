package com.example.movieapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.MovieResponse;
import com.example.movieapp.data.model.RecomendacionResult;
import com.example.movieapp.data.model.SeguimientoFS;
import com.example.movieapp.data.network.ApiService;
import com.example.movieapp.data.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecomendacionRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ApiService api = RetrofitClient.getRetrofit().create(ApiService.class);
    private final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //devuelve pelis o series según el género mas repetido en seguimiento
    public LiveData<RecomendacionResult> obtenerRecomendados() {
        MutableLiveData<RecomendacionResult> liveData = new MutableLiveData<>();

        db.collection("seguimientos").whereEqualTo("userId", uid).get().addOnSuccessListener(snapshot -> {
            List<SeguimientoFS> lista = snapshot.toObjects(SeguimientoFS.class);

            if (lista.isEmpty()) {
                liveData.setValue(new RecomendacionResult(Collections.emptyList(), ""));
                return;
            }

            //cuento los géneros y acumulo por id
            Map<Integer, Integer> conteoGenero = new HashMap<>();
            Map<Integer, String> tipoGenero = new HashMap<>();

            for (SeguimientoFS s : lista) {
                if (s.getGenreId() == 0) continue;
                int count = conteoGenero.getOrDefault(s.getGenreId(), 0);
                conteoGenero.put(s.getGenreId(), count + 1);
                tipoGenero.put(s.getGenreId(), s.getTipo());
            }

            if (conteoGenero.isEmpty()) {
                liveData.setValue(new RecomendacionResult(Collections.emptyList(), ""));
                return;
            }

            //genero más repetido
            int genreIdDominante = Collections.max(conteoGenero.entrySet(), Map.Entry.comparingByValue()).getKey();
            String tipoDominante = tipoGenero.getOrDefault(genreIdDominante, "pelicula");
            String lang = Locale.getDefault().getLanguage().equals("es") ? "es-ES" : "en-US";
            Call<MovieResponse> call = "pelicula".equals(tipoDominante) ? api.discoverMoviesByGenre(genreIdDominante, "popularity.desc", lang, 1) : api.discoverTvByGenre(genreIdDominante, "popularity.desc", lang, 1);

            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> c, Response<MovieResponse> r) {
                    if (r.isSuccessful() && r.body() != null) {
                        //filtra los que ya están en seguimiento
                        List<Integer> yaVistos = new java.util.ArrayList<>();
                        for (SeguimientoFS s : lista) yaVistos.add(s.getTmdbId());

                        List<Movie> filtrados = new java.util.ArrayList<>();
                        for (Movie m : r.body().getResults()) {
                            if (!yaVistos.contains(m.getId())) filtrados.add(m);
                            if (filtrados.size() == 10) break; // máximo 10
                        }
                        liveData.setValue(new RecomendacionResult(filtrados, tipoDominante));
                    } else {
                        liveData.setValue(new RecomendacionResult(Collections.emptyList(), ""));
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> c, Throwable t) {
                    liveData.setValue(new RecomendacionResult(Collections.emptyList(), ""));
                }
            });
        }).addOnFailureListener(e ->
                liveData.setValue(new RecomendacionResult(Collections.emptyList(), ""))
        );
        return liveData;
    }
}
