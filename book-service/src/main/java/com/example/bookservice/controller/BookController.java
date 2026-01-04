package com.example.bookservice.controller;

import com.example.bookservice.entity.Book;
import com.example.bookservice.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        // Exemple : on tente de récupérer un prix externe pour l'initialiser
        // Ce n'est pas obligatoire pour le CRUD de base mais ça montre le lien.
        if (book.getPrice() == null) {
            // Fake ID 1 pour l'appel pricing
            Double externalPrice = bookService.getPriceExternal(1L);
            if (externalPrice != null && externalPrice > 0) {
                book.setPrice(externalPrice);
            } else {
                book.setPrice(10.0); // Default local
            }
        }
        return bookService.save(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        try {
            return bookService.findById(id).map(book -> {
                book.setTitle(bookDetails.getTitle());
                book.setAuthor(bookDetails.getAuthor());
                book.setPrice(bookDetails.getPrice());
                // Optimistic locking : le version est géré automatiquement par Hibernate
                // Mais si le client envoie une version, il faut vérifier ?
                // En général JPA checke la version en base vs celle de l'objet managed.
                // Si on fait un mapping manuel :
                // book.setVersion(bookDetails.getVersion()); // attention si version != base =>
                // exception

                return ResponseEntity.ok(bookService.save(book));
            }).orElse(ResponseEntity.notFound().build());
        } catch (ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(409).body("Conflict: The book was updated by another transaction.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Endpoint spécifique pour tester la résilience sans créer de livre
    @GetMapping("/test-pricing/{id}")
    public Double testPricing(@PathVariable Long id) {
        return bookService.getPriceExternal(id);
    }
}
