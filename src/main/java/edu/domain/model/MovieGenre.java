package edu.domain.model;

public enum MovieGenre {
    ACTION("боевик"),
    ADVENTURE("приключения"),
    ANIMATED("мультфильм"),
    BIOPIC("биография"),
    CHILDREN("детский"),
    COMEDY("комедия"),
    DETECTIVE("детектив"),
    DOCUMENTARY("документальный"),
    DRAMA("драма"),
    FANTASY("фэнтези"),
    GANGSTER("гангстерский"),
    HISTORICAL("исторический"),
    HORROR("ужасы"),
    MUSICAL("мюзикл"),
    ROMANCE("романтика"),
    SCIENCE_FICTION("научная фантастика"),
    SPORT("спорт"),
    THRILLER("триллер"),
    WAR("военный"),
    WESTERN("вестерн");

    private final String russianName;

    MovieGenre(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}
