package edu.domain.repository;

import edu.domain.model.Country;
import edu.domain.repository.mapper.CountryMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CountriesRepositoryTest {
    private static final Country USSR = new Country(1, "USSR");

    private final CountriesRepository countriesRepository = new CountriesRepository(new CountryMapper());

    @BeforeEach
    public void setUp() {
        countriesRepository.saveById(USSR);
    }

    @AfterEach
    public void tearDown() {
        countriesRepository.delete(USSR.getId());
    }

    @Test
    public void countryShouldCanBeFoundByIdWhenItCreated() {
        Country country = countriesRepository.findById(USSR.getId()).orElse(null);
        assertThat(country).isNotNull();
        assertThat(country.getId()).isEqualTo(USSR.getId());
        assertThat(country.getName()).isEqualTo(USSR.getName());
    }

    @Test
    public void countryShouldCanBeFoundByIdWithUpdatedDataWhenItUpdated() {
        Country sovietUnion = new Country(1, "Soviet Union");
        countriesRepository.update(USSR.getId(), sovietUnion);

        Country country = countriesRepository.findById(USSR.getId()).orElse(null);

        assertThat(country).isNotNull();
        assertThat(country.getName()).isEqualTo(sovietUnion.getName());
    }

    @Test
    public void countryShouldCanNotBeFoundByIdWhenItDeleted() {
        countriesRepository.delete(USSR.getId());

        Country country = countriesRepository.findById(USSR.getId()).orElse(null);

        assertThat(country).isNull();
    }

    @Test
    public void findAllShouldReturnAllCountries() {
        Country usa = new Country(2, "USA");
        countriesRepository.saveById(usa);

        List<Country> countries = countriesRepository.findAll();

        assertThat(countries).hasSizeGreaterThanOrEqualTo(2);
        assertThat(countries).contains(USSR);

        countriesRepository.delete(usa.getId());
    }
}
