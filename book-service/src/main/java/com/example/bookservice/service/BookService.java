package com.example.bookservice.service;

import com.example.bookservice.entity.Book;
import com.example.bookservice.repository.BookRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;

    // URL du pricing-service.
    // En local = localhost:8081.
    // Avec Docker = http://pricing-service:8081 (on utilisera une variable d'env ou
    // properties pour être flexible)
    // Pour l'instant on hardcode ou on utilise une propriété placeholder.
    @Value("${pricing.service.url:http://localhost:8081}")
    private String pricingServiceUrl;

    public BookService(BookRepository bookRepository, RestTemplate restTemplate) {
        this.bookRepository = bookRepository;
        this.restTemplate = restTemplate;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book save(Book book) {
        // Avant de sauvegarder, on pourrait vouloir récupérer un prix "recommandé" ou
        // autre depuis le pricing service
        // Mais pour la démo de la résilience, faisons un appel explicite lors d'un get
        // ou d'une méthode dédiée.
        // Simulons : lors de la création, on va chercher le prix par défaut via le
        // pricing-service.
        // Si le livre a un ID (update), on peut ne pas le faire.

        // Mais attention, l'appel externe dans le save pourrait être bloquant.
        return bookRepository.save(book);
    }

    // Méthode spécifique pour démontrer la résilience
    @CircuitBreaker(name = "pricingService", fallbackMethod = "fallbackPrice")
    @Retry(name = "pricingService")
    public Double getPriceExternal(Long bookId) {
        String url = pricingServiceUrl + "/api/prices/" + bookId;
        // On attend de récupérer un Map ou un objet
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("price")) {
            Object p = response.get("price");
            if (p instanceof Number) {
                return ((Number) p).doubleValue();
            }
        }
        return null;
    }

    public Double fallbackPrice(Long bookId, Throwable t) {
        // Fallback en cas d'erreur
        // Retourne une valeur par défaut, ex: -1.0 pour signaler une indispo ou un prix
        // standard
        System.err.println("Fallback pricing service called for book " + bookId + " due to: " + t.getMessage());
        return -1.0;
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
