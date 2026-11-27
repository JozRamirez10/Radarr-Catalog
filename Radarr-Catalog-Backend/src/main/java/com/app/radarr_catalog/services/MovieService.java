package com.app.radarr_catalog.services;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.app.radarr_catalog.constants.Constants.App;
import com.app.radarr_catalog.dtos.MovieDto;
import com.app.radarr_catalog.models.RadarrImage;
import com.app.radarr_catalog.models.RadarrMovie;

@Service
public class MovieService {

    private final RestClient restClient;

    public MovieService(
        @Value("${radarr.api.url}") String radarrUrl,
        @Value("${radarr.api.key}") String apiKey
    ) {
        this.restClient = RestClient.builder()
            .baseUrl(radarrUrl)
            .defaultHeader(App.API_KEY_HEADER, apiKey)
            .build();
    }

    public List<MovieDto> getMovies() {
        List<RadarrMovie> radarrMovies = restClient.get()
            .uri(App.URI_MOVIE)
            .retrieve()
            .body(new ParameterizedTypeReference<List<RadarrMovie>>(){});

        if (radarrMovies == null) {
            return List.of();
        }

        return radarrMovies.stream()
            .filter(movie -> movie.hasFile() && movie.movieFile() != null)
            .map(movie -> {
                String posterUrl = movie.images().stream()
                    .filter(img -> App.POSTER.equals(img.coverType()))
                    .map(RadarrImage::remoteUrl)
                    .findFirst()
                    .orElse(App.URL_IMAGE_DEFAULT);

                return new MovieDto(
                    movie.title(),
                    movie.year(),
                    posterUrl,
                    movie.movieFile().path()
                );
            }).toList();
    }

    public Resource loadMovieAsResource(String moviePath) {
        try {
            Path filePath = Paths.get(moviePath).normalize();
            if (!filePath.startsWith(App.DOWNLOAD_DIR)) {
                return null;
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
