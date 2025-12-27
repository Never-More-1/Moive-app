package movieApp.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import movieApp.model.dto.tmdbDto.TmdbResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    @Value("${tmdb.api.language}")
    private String language;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TmdbService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // Получить информацию о фильме по ID
    public TmdbResponse getMovieById(Integer movieId) {
        try {
            String url = buildMovieUrl(movieId);
            System.out.println("Запрос к TMDB: " + url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TmdbResponse movie = objectMapper.readValue(
                        response.getBody(),
                        TmdbResponse.class
                );
                System.out.println("Получен фильм: " + movie.getTitle());
                return movie;
            } else {
                System.err.println("Ошибка TMDB: " + response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Ошибка при запросе к TMDB: " + e.getMessage());
            return null;
        }
    }

    // Проверить существует ли фильм
    public boolean movieExists(Integer movieId) {
        TmdbResponse movie = getMovieById(movieId);
        return movie != null;
    }

    // Получить URL для постера
    public String getPosterUrl(String posterPath, String size) {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }
        return String.format("https://image.tmdb.org/t/p/%s%s", size, posterPath);
    }

    // Построить URL для запроса фильма
    private String buildMovieUrl(Integer movieId) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path("/movie/{movieId}")
                .queryParam("api_key", apiKey)
                .queryParam("language", language)
                .buildAndExpand(movieId)
                .toUriString();
    }

    // Поиск фильмов
    public String searchMovies(String query, Integer page) {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/search/movie")
                    .queryParam("api_key", apiKey)
                    .queryParam("query", query)
                    .queryParam("page", page != null ? page : 1)
                    .queryParam("language", language)
                    .queryParam("include_adult", false)
                    .build()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (Exception e) {
            System.err.println("Ошибка поиска: " + e.getMessage());
            return null;
        }
    }

    // Получить популярные фильмы
    public String getPopularMovies(Integer page) {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/movie/popular")
                    .queryParam("api_key", apiKey)
                    .queryParam("page", page != null ? page : 1)
                    .queryParam("language", language)
                    .build()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (Exception e) {
            System.err.println("Ошибка получения популярных: " + e.getMessage());
            return null;
        }
    }
}
