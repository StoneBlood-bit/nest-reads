package nest.repository.book;

import java.util.List;
import java.util.Set;
import nest.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b.title FROM Book b")
    List<String> findAllBookTitles();

    @Query("SELECT b FROM Book b JOIN b.genres g WHERE "
            + "(b.author IN :authors OR g.name IN :genres) "
            + "AND b.id NOT IN :excludedBookIds "
            + "AND b.receiver IS NULL")
    Set<Book> findBooksByGenreOrAuthor(@Param("genres") Set<String> genres,
                                        @Param("authors") Set<String> authors,
                                        @Param("excludedBookIds") Set<Long> excludedBookIds);

    @Query(value = "SELECT * FROM books b WHERE b.id NOT IN :excludedBookIds "
            + "AND b.receiver_id IS NULL "
            + "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Book> findRandomBooks(
            @Param("limit") int limit,
            @Param("excludedBookIds") Set<Long> excludedBookIds
    );

    @Query(value = "SELECT * FROM books WHERE receiver_id IS NULL "
            + "ORDER BY RAND() LIMIT 9", nativeQuery = true)
    List<Book> findRandomBooksForGuest();
}
