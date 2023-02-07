package com.aelous.model.content.title;

import java.util.*;

public class CategorizedTitles {

    private final TitleCategory category;

    private final List<AvailableTitle> titles;

    public CategorizedTitles(TitleCategory category, List<AvailableTitle> titles) {
        this.category = category;
        this.titles = titles;
    }

    public CategorizedTitles(TitleCategory category, Collection<? extends AvailableTitle>... collections) {
        this.category = category;
        this.titles = new ArrayList<>();
        Arrays.stream(collections).forEach(titles::addAll);
    }


    public TitleCategory getCategory() {
        return category;
    }

    public List<AvailableTitle> getTitles() {
        return titles;
    }
}
