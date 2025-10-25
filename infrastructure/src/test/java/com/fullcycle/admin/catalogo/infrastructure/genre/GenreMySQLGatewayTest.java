package com.fullcycle.admin.catalogo.infrastructure.genre;

import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.CategoryMySQLGateway;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private GenreMySQLGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void testDependenciesInjected() {
        Assertions.assertNotNull(categoryGateway);
        Assertions.assertNotNull(genreGateway);
        Assertions.assertNotNull(genreRepository);
    }

    @Test
    public void givenAValidGenre_whenCallsCreate_shouldPersistGenre() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", "A categoria mais assistida", true));

        final var expectedName = "Acao";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        aGenre.addCategories(expectedCategories);

        Assertions.assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.create(aGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(aGenre.getId().getValue()).get();
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoriesIds());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());


    }

    @Test
    public void givenAValidGenreWithOutCategories_whenCallsCreate_shouldPersistGenre() {
        final var expectedName = "Acao";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.create(aGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(aGenre.getId().getValue()).get();
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoriesIds());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());


    }

    @Test
    public void givenAValidGenreWithOutCategories_whenCallsUpdateWithCategories_shouldPersistGenre() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", "A categoria mais assistida", true));
        final var series = categoryGateway.create(Category.newCategory("Series", "A categoria menos assistida", true));

        final var expectedName = "Acao";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var aGenre = Genre.newGenre("ac", expectedIsActive);

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals("ac", aGenre.getName());
        Assertions.assertEquals(0, aGenre.getCategories().size());

        final var actualGenre = genreGateway.update(Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories));

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(sorted(expectedCategories), sorted(actualGenre.getCategories()));
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var presistedGenre = genreRepository.findById(aGenre.getId().getValue()).get();
        Assertions.assertEquals(expectedName, presistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, presistedGenre.isActive());
        Assertions.assertEquals(sorted(expectedCategories), sorted(presistedGenre.getCategoriesIds()));
        Assertions.assertEquals(aGenre.getCreatedAt(), presistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(presistedGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), presistedGenre.getDeletedAt());
        Assertions.assertNull(presistedGenre.getDeletedAt());


    }

    @Test
    public void givenAValidGenreWithCategories_whenCallsUpdateWithCleaningCategories_shouldPersistGenre() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", "A categoria mais assistida", true));
        final var series = categoryGateway.create(Category.newCategory("Series", "A categoria menos assistida", true));

        final var expectedName = "Acao";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre("ac", expectedIsActive);
        aGenre.addCategories(List.of(filmes.getId(), series.getId()));

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals("ac", aGenre.getName());
        Assertions.assertEquals(2, aGenre.getCategories().size());

        final var actualGenre = genreGateway.update(Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories));

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(aGenre.getId().getValue()).get();
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoriesIds());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());

    }

    @Test
    public void givenAValidGenreInactive_whenCallsUpdateWithActivating_shouldPersistGenre() {

        final var expectedName = "Acao";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre(expectedName, false);

        final var expectedId = aGenre.getId();

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertFalse(aGenre.isActive());
        Assertions.assertNotNull(aGenre.getDeletedAt());

        final var actualGenre = genreGateway.update(Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories));

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoriesIds());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        Assertions.assertNull(persistedGenre.getDeletedAt());

    }

    @Test
    public void givenAValidGenreActive_whenCallsUpdateWithInactivating_shouldPersistGenre() {

        final var expectedName = "Acao";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var aGenre = Genre.newGenre(expectedName, true);

        final var expectedId = aGenre.getId();

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertTrue(aGenre.isActive());
        Assertions.assertNull(aGenre.getDeletedAt());

        final var actualGenre = genreGateway.update(Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories));

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoriesIds());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(persistedGenre.getUpdatedAt()));
        Assertions.assertNotNull(persistedGenre.getDeletedAt());

    }

    @Test
    public void givenAnPrePersistedGenre_whenCallsDeleteById_shouldDeleteGenre() {
        final var expectedName = "Acao";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        Assertions.assertEquals(0, genreRepository.count());

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        aGenre.addCategories(expectedCategories);
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));
        Assertions.assertEquals(1, genreRepository.count());

        genreGateway.deleteById(aGenre.getId());

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAnNonExistentGenre_whenCallsDeleteById_shouldBeReturnOk() {
        Assertions.assertEquals(0, genreRepository.count());

        genreGateway.deleteById(GenreID.from("123"));

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenPrePersistedGenres_whenCallsFindByIds_shouldReturnGenres() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", "A categoria mais assistida", true));
        final var series = categoryGateway.create(Category.newCategory("Series", "A categoria menos assistida", true));

        final var expectedName = "Acao";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        Assertions.assertEquals(0, genreRepository.count());

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        aGenre.addCategories(expectedCategories);
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals(1, genreRepository.count());

        final var actualGenres = genreGateway.findById(aGenre.getId()).get();

        Assertions.assertEquals(aGenre.getId(), actualGenres.getId());
        Assertions.assertEquals(expectedName, actualGenres.getName());
        Assertions.assertEquals(expectedIsActive, actualGenres.isActive());
        Assertions.assertEquals(sorted(expectedCategories), sorted(actualGenres.getCategories()));
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenres.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenres.getUpdatedAt());
        Assertions.assertNull(actualGenres.getDeletedAt());
    }

    @Test
    public void givenNonExistentGenres_whenCallsFindByIds_shouldReturnEmpty() {
        Assertions.assertEquals(0, genreRepository.count());

        final var actualGenres = genreGateway.findById(GenreID.from("123"));

        Assertions.assertTrue(actualGenres.isEmpty());
    }

    @Test
    public void givenEmptyGenres_whenCallsFindAll_shouldReturnEmpty() {
        final var expectedPage = 0;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = genreGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "ac,0,10,1,1,Acao",
        "dr,0,10,1,1,Drama",
        "com,0,10,1,1,Comédia romântica",
        "cien,0,10,1,1,Ficção Científica",
        "terr,0,10,1,1,Terror",
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final long expectedItemsCount,
        final long expectedTotal,
        final String expectedName
    ) {
        mockGenres();
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = genreGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "name,asc,0,10,5,5,Acao",
        "name,desc,0,10,5,5,Terror",
        "createdAt,asc,0,10,5,5,Comédia romântica",
        "createdAt,desc,0,10,5,5,Ficção Científica",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnFiltered(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final long expectedItemsCount,
        final long expectedTotal,
        final String expectedName
    ) {
        mockGenres();
        final var expectedTerms = "";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = genreGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "0,2,2,5,Acao;Comédia romântica",
        "1,2,2,5,Drama;Ficção Científica",
        "2,2,1,5,Terror",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnFiltered(
        final int expectedPage,
        final int expectedPerPage,
        final long expectedItemsCount,
        final long expectedTotal,
        final String expectedGenres
    ) {
        mockGenres();
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = genreGateway.findAll(aQuery);

        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());

        int index = 0;
        for (final var expectedName : expectedGenres.split(";")) {
            final var actualName = actualResult.items().get(index).getName();
            Assertions.assertEquals(expectedName, actualName);
            index++;
        }
    }

    private void mockGenres() {
        genreRepository.saveAllAndFlush(List.of(
            GenreJpaEntity.from(Genre.newGenre("Comédia romântica", true)),
            GenreJpaEntity.from(Genre.newGenre("Acao", true)),
            GenreJpaEntity.from(Genre.newGenre("Drama", true)),
            GenreJpaEntity.from(Genre.newGenre("Terror", true)),
            GenreJpaEntity.from(Genre.newGenre("Ficção Científica", true))
            ));
    }

    private List<CategoryID> sorted(List<CategoryID> expectedCategories) {
        return expectedCategories.stream()
            .sorted(Comparator.comparing(CategoryID::getValue))
            .collect(Collectors.toList());
    }

    @Test
    public void givenPrePersistedGenres_whenCallsExistsByIds_shouldReturnIds() {

        final var aventura = Genre.newGenre(Fixture.Genres.AVENTURA.getName(), true);
        final var ficcao = Genre.newGenre(Fixture.Genres.FICCAO.getName(), true);


        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAll(List.of(GenreJpaEntity.from(aventura), GenreJpaEntity.from(ficcao)));

        Assertions.assertEquals(2, genreRepository.count());

        final var expectedIds = List.of(aventura.getId(), ficcao.getId());
        final var ids = List.of(aventura.getId(), ficcao.getId(), GenreID.from("123"));
        final var actualResult = genreGateway.existsByIds(ids);

        Assertions.assertTrue(expectedIds.size() == actualResult.size()
                && expectedIds.containsAll(actualResult));


    }

}
