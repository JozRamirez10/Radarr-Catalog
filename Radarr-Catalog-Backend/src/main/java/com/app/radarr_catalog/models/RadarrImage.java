package com.app.radarr_catalog.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RadarrImage(
    String coverType,
    String remoteUrl
) { }
