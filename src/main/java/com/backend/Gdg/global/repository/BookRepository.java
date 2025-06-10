package com.backend.Gdg.global.repository;

import com.backend.Gdg.global.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    // 가장 최근 등록된 책의 coverImageUrl 하나만 조회
    @Query("""
       SELECT b.coverImageUrl
         FROM Book b
        WHERE b.category.categoryId = :categoryId
        ORDER BY b.registerAt DESC, b.bookId DESC
    """)
    String findLatestCoverByCategory(@Param("categoryId") Long categoryId);

    // 카테고리 ID로 책 개수 세기 (메서드 네이밍 컨벤션 사용)
    long countByCategoryCategoryId(Long categoryId);

}