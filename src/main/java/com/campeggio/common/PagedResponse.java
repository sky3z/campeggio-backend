package com.campeggio.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper per le risposte paginate — struttura JSON stabile e esplicita.
 * Sostituisce la serializzazione diretta di PageImpl che produce un warning.
 */
public record PagedResponse<T>(
        List<T> content,
        int totalPages,
        long totalElements,
        int number,    // pagina corrente (0-based)
        int size       // elementi per pagina
) {
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }
}
