package com.app.radarr_catalog.dtos;

public record MovieDto(
    String title,
    int year,
    String posterUrl,
    String moviePath
) { }
