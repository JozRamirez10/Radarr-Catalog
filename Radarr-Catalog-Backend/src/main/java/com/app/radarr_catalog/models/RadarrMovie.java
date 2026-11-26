package com.app.radarr_catalog.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RadarrMovie(
    String title,
    int year,
    List<RadarrImage> images,
    RadarrMovieFile movieFile,
    boolean hasFile
) { }
