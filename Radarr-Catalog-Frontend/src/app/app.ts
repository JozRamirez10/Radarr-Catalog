import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Messages } from './constants/constants';
import { MovieDto } from './models/movie.dto';
import { MovieService } from './services/movie.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  private readonly movieService = inject(MovieService);

  allMovies = signal<MovieDto[]>([]);
  searchQuery = signal<string>('');

  filteredMovies = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    if (!query) return this.allMovies();

    return this.allMovies().filter(movie =>
      movie.title.toLowerCase().includes(query)
    );
  });

  isLoading = signal<boolean>(true);
  hasError = signal<boolean>(false);

  ngOnInit(): void {
    this.movieService.getMovies().subscribe({
      next: (data) => {
        this.allMovies.set(data);
        this.isLoading.set(false);
      },
      error: (err) =>{
        this.isLoading.set(false);
        this.hasError.set(true);
        console.error(Messages.ERROR_LOADING_MOVIES, err);
      }
    });
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
  }

  onDownload(movie: MovieDto): void {
    this.movieService.downloadMovie(movie.moviePath);
  }
}
