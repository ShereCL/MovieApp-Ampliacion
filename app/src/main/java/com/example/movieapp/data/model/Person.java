package com.example.movieapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Person {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("profile_path")
    private String profilePath;

    @SerializedName("known_for_department")
    private String knownForDepartment;

    @SerializedName("biography")
    private String biography;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("place_of_birth")
    private String placeOfBirth;

    @SerializedName("known_for")
    private List<KnownForItem> knownFor;

    @SerializedName("combined_credits")
    private CombinedCredits combinedCredits;

    //Constructores
    public Person() {
    }

    //Getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getKnownForDepartment() {
        return knownForDepartment;
    }

    public String getBiography() {
        return biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public List<KnownForItem> getKnownFor() {
        return knownFor;
    }

    public CombinedCredits getCombinedCredits() {
        return combinedCredits;
    }

    //Setters


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setKnownForDepartment(String knownForDepartment) {
        this.knownForDepartment = knownForDepartment;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public void setKnownFor(List<KnownForItem> knownFor) {
        this.knownFor = knownFor;
    }

    public void setCombinedCredits(CombinedCredits combinedCredits) {
        this.combinedCredits = combinedCredits;
    }

    //Método auxiliar para la Url completa de la foto
    public String getFullProfileUrl() {
        if (profilePath != null && !profilePath.isEmpty()) {
            return "https://image.tmdb.org/t/p/w500" + profilePath;
        }
        return null;
    }

    //Clase interna para ver las pelis y series en las que ha participado el actor/director
    public static class KnownForItem {

        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("name")
        private String name;

        @SerializedName("poster_path")
        private String posterPath;

        @SerializedName("media_type")
        private String mediaType;

        @SerializedName("release_date")
        private String releaseDate;

        @SerializedName("first_air_date")
        private String firstAirDate;

        public KnownForItem() {
        }

        //Getters
        public int getId() {
            return id;
        }

        public String getMediaType() {
            return mediaType;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getFirstAirDate() {
            return firstAirDate;
        }

        public String getDisplayTitle() {
            if (title != null && !title.isEmpty()) return title;
            if (name != null && !name.isEmpty()) return name;
            return "Sin título";
        }

        public String getYear() {
            String date = releaseDate != null ? releaseDate : firstAirDate;
            if (date != null && date.length() >= 4) return date.substring(0, 4);
            return "";
        }

        public String getFullPosterUrl() {
            if (posterPath != null && !posterPath.isEmpty()) {
                return "https://image.tmdb.org/t/p/w500" + posterPath;
            }
            return null;
        }
    }

    public static class CombinedCredits {
        @SerializedName("cast")
        private List<KnownForItem> cast;

        @SerializedName("crew")
        private List<KnownForItem> crew;

        public CombinedCredits() {
        }

        public List<KnownForItem> getCast() {
            return cast;
        }

        public List<KnownForItem> getCrew() {
            return crew;
        }
    }
}
