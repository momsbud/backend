package com.momsbud.backend.shared.web;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PageResponse<T> {
    private List<T> items;
    private long total;
    private int page;
    private int size;
}
