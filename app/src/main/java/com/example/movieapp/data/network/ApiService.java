package com.example.movieapp.data.network;

import com.example.movieapp.data.model.GenreResponse;
import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.MovieResponse;
import com.example.movieapp.data.model.Person;
import com.example.movieapp.data.model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //Pelis populares (he añadido el query de string para que detecte el idioma)
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("page") int page, @Query("language") String language);

    //series populares (he añadido el query de string para que detecte el idioma)
    @GET("tv/popular")
    Call<MovieResponse> getPopularTvShows(@Query("page") int page, @Query("language") String language);

    //Detalles de película
    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("language") String language
    );

    //Detalles de serie
    @GET("tv/{tv_id}")
    Call<Movie> getTvShowDetails(
            @Path("tv_id") int tvId,
            @Query("language") String language
    );

    //Videos de peli
    @GET("movie/{movie_id}/videos")
    Call<Video.VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("language") String language
    );

    //video de una serie
    @GET("tv/{tv_id}/videos")
    Call<Video.VideoResponse> getTvVideos(
            @Path("tv_id") int tvId,
            @Query("language") String language
    );
    /// MÉTODOS QUE SIRVEN PARA LA BÚSQUEDA EN SEGUIMIENTO ///

    //pelis
    @GET("search/movie")
    Call<TmdbResponse> searchMovies(
            @Query("query") String query, //texto a buscar
            @Query("language") String language, //idioma
            @Query("page") int page //resultadios
    );

    //series
    @GET("search/tv")
    Call<TmdbResponse> searchTv(
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page
    );

    //buscar personas por nombre
    @GET("search/person")
    Call<PersonResponse> searchPersons(
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page
    );

    //detalle de una persona
    @GET("person/{person_id}")
    Call<Person> getPersonDetails(
            @Path("person_id") int personId,
            @Query("language") String language,
            @Query("append_to_response") String appendToResponse
    );

    //obtener el top de personas mas visitadas o buscadas
    @GET("trending/person/{time_window}")
    Call<PersonResponse> getTrendingPersonas(
            @Path("time_window") String timeWindow,
            @Query("page") int page
    );

    //pelis por género
    @GET("discover/movie")
    Call<MovieResponse> discoverMoviesByGenre(
            @Query("with_genres") int genreId,
            @Query("sort_by")     String sortBy,
            @Query("language")    String language,
            @Query("page")        int page
    );

    //series por género
    @GET("discover/tv")
    Call<MovieResponse> discoverTvByGenre(
            @Query("with_genres") int genreId,
            @Query("sort_by")     String sortBy,
            @Query("language")    String language,
            @Query("page")        int page
    );

    //géneros de pelis
    @GET("genre/movie/list")
    Call<GenreResponse> getMovieGenres(@Query("language") String language);
    //géneros de series
    @GET("genre/tv/list")
    Call<GenreResponse> getTvGenres(@Query("language") String language);

}
