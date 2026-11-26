import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiRoutes } from '../constants/constants';
import { MovieDto } from '../models/movie.dto';

@Injectable({
    providedIn: 'root'
})
export class MovieService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    private readonly api = `${this.apiUrl}${ApiRoutes.API}`

    getMovies(): Observable<MovieDto[]> {
        return this.http.get<MovieDto[]>(this.api);
    }

    downloadMovie(moviePath: string): void {
        const downloadUrl = new URL(`${this.api}${ApiRoutes.DOWNLOAD_ENDPOINT}`);
        downloadUrl.searchParams.set(ApiRoutes.PATH_PARAM, moviePath);

        const a = document.createElement('a');
        a.href = downloadUrl.toString();

        document.body.appendChild(a);
        a.click();

        document.body.removeChild(a);
    }
}
