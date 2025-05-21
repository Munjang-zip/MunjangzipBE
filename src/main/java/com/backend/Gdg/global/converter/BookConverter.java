package com.backend.Gdg.global.converter;

import com.backend.Gdg.global.domain.entity.Book;
import com.backend.Gdg.global.domain.entity.Category;
import com.backend.Gdg.global.domain.entity.Member;
import com.backend.Gdg.global.web.dto.Book.BookRequestDTO;
import com.backend.Gdg.global.web.dto.Book.BookResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookConverter {

    //Main화면에 모든 카테고리 조회
    public static BookResponseDTO.MainBookResponseDTO toMainBookResponseDTO(
            Category category,
            String recentBookCover,
            long bookCount,
            long memoCount
    ) {
        return BookResponseDTO.MainBookResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .recentBookCovers(recentBookCover)
                .bookCount(bookCount)
                .memoCount(memoCount)
                .build();
    }

    //BookRegisterResquestDTO를 바탕으로 Book엔티티 변환
    public static Book toBook(BookRequestDTO.BookRegisterResquestDTO request,
                              Category category,
                              Member member) {

        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .ISBN(request.getISBN())
                .member(member)
                .coverImageUrl(request.getCoverImageUrl())
                .category(category)
                .registerAt(LocalDate.now())
                .build();
    }

    // BookResposneDTO를 반환
    public static BookResponseDTO.BookRegisterResponseDTO toBookRegisterResponseDTO(Book book) {
        return BookResponseDTO.BookRegisterResponseDTO.builder()
                .bookId(book.getBookId())
                .memberId(book.getMember().getMemberId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .ISBN(book.getISBN())
                .register_at(book.getRegisterAt())
                .category(book.getCategory().getCategoryName())
                .coverImageUrl(book.getCoverImageUrl())
                .build();
    }

    // BookCategoryListDTO 변환
    public static BookResponseDTO.BookCategoryListDTO toBookCategoryListDTO(Book book) {
        return BookResponseDTO.BookCategoryListDTO.builder()
                .bookId(book.getBookId())
                .bookImage(book.getCoverImageUrl())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }

    // BookByCategoryResponseDTO 변환
    public static BookResponseDTO.BookByCategoryResponseDTO toBookByCategoryResponseDTO(Category category) {
        List<BookResponseDTO.BookCategoryListDTO> books = category.getBooks().stream()
                .map(BookConverter::toBookCategoryListDTO)
                .collect(Collectors.toList());

        return BookResponseDTO.BookByCategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .memberId(category.getMember().getMemberId())
                .books(books)
                .build();
    }


}