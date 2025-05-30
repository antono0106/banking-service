package com.moroz.bankingservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"pageable", "sort", "first", "last", "empty"})
@RequiredArgsConstructor
public class AccountPageImpl<T> implements Page<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;

    public AccountPageImpl(final List<T> content, final PageRequest pageRequest, final long totalElements) {
        this.content = content;
        this.page = pageRequest.getPageNumber();
        this.size = pageRequest.getPageSize();
        this.totalElements = totalElements;
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    @Override
    public <U> Page<U> map(final Function<? super T, ? extends U> converter) {
        final List<U> mappedContent = this.content.stream()
                .map(converter)
                .collect(Collectors.toList());
        return new AccountPageImpl<>(mappedContent, page, size, totalElements);
    }

    @Override
    public int getNumber() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getNumberOfElements() {
        return content.size();
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }

    @Override
    public Sort getSort() {
        return Sort.unsorted(); // Или поддержи сортировку при необходимости
    }

    @Override
    public boolean isFirst() {
        return page == 0;
    }

    @Override
    public boolean isLast() {
        return page >= getTotalPages() - 1;
    }

    @Override
    public boolean hasNext() {
        return page < getTotalPages() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return page > 0;
    }

    @Override
    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

    @Override
    public Pageable nextPageable() {
        return hasNext() ? PageRequest.of(page + 1, size) : Pageable.unpaged();
    }

    @Override
    public Pageable previousPageable() {
        return hasPrevious() ? PageRequest.of(page - 1, size) : Pageable.unpaged();
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}
