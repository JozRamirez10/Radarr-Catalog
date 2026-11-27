package com.app.radarr_catalog.controllers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.radarr_catalog.dtos.MovieDto;
import com.app.radarr_catalog.services.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDto> getMovies() {
        return movieService.getMovies();
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadMovie(@RequestParam String path) {

        Resource resource = movieService.loadMovieAsResource(path);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        String filename = resource.getFilename();
        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename(filename, StandardCharsets.UTF_8)
            .build();

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .body(resource);
    }
}
