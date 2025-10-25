package com.fullcycle.admin.catalogo.infrastructure.castmember;

import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.fullcycle.admin.catalogo.domain.Fixture.*;
import static com.fullcycle.admin.catalogo.domain.Fixture.CastMembers.type;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MySQLGatewayTest
class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberMySQLGateway castMemberGateway;
    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    public void testDependencyInjection() {
        assertNotNull(castMemberGateway);
        assertNotNull(castMemberRepository);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldBePersistedIt() {

        // given
        final var expectedName = name();
        final var expectedType = type();

        final var aCastMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aCastMember.getId();

        Assertions.assertEquals(0, castMemberRepository.count());

        // when
        final var actualOutput = castMemberGateway.create(CastMember.with(aCastMember));

        // then
        Assertions.assertEquals(1, castMemberRepository.count());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.getId().getValue());
        Assertions.assertEquals(expectedName, actualOutput.getName());
        Assertions.assertEquals(expectedType, actualOutput.getType());
        Assertions.assertNotNull(actualOutput.getCreatedAt());
        Assertions.assertNotNull(actualOutput.getUpdatedAt());

        final var storedCastMember = castMemberRepository.findById(aCastMember.getId().getValue()).get();

        Assertions.assertEquals(expectedId.getValue(), storedCastMember.getId());
        Assertions.assertEquals(expectedName, storedCastMember.getName());
        Assertions.assertEquals(expectedType, storedCastMember.getType());
        Assertions.assertEquals(aCastMember.getCreatedAt(), storedCastMember.getCreatedAt());
        Assertions.assertEquals(aCastMember.getUpdatedAt(), storedCastMember.getUpdatedAt());
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldBePersistedIt() {

        // given
        final var expectedName = name();
        final var expectedType = CastMemberType.ACTOR;

        final var aCastMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);
        final var expectedId = aCastMember.getId();

        final var createdCastMember = castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember)).toAggregate();

        Assertions.assertEquals(1, castMemberRepository.count());
        Assertions.assertEquals("vind", createdCastMember.getName());
        Assertions.assertEquals(CastMemberType.DIRECTOR, createdCastMember.getType());

        // when
        final var actualOutput = castMemberGateway.update(
            CastMember.with(createdCastMember).update(expectedName, expectedType)
        );

        // then
        Assertions.assertEquals(1, castMemberRepository.count());
        Assertions.assertEquals(expectedId.getValue(), actualOutput.getId().getValue());
        Assertions.assertEquals(expectedName, actualOutput.getName());
        Assertions.assertEquals(expectedType, actualOutput.getType());
        Assertions.assertNotNull(actualOutput.getCreatedAt());
        Assertions.assertTrue(actualOutput.getUpdatedAt().isAfter(createdCastMember.getCreatedAt()));

        final var storedCastMember = castMemberRepository.findById(aCastMember.getId().getValue()).get();

        Assertions.assertEquals(expectedId.getValue(), storedCastMember.getId());
        Assertions.assertEquals(expectedName, storedCastMember.getName());
        Assertions.assertEquals(expectedType, storedCastMember.getType());
        Assertions.assertEquals(aCastMember.getCreatedAt(), storedCastMember.getCreatedAt());
        Assertions.assertTrue(storedCastMember.getUpdatedAt().isAfter(createdCastMember.getUpdatedAt()));
    }

    @Test
    public void givenAValidCastMember_whenCallsDeleteById_shouldBeDeletedIt() {

        // given
        final var aCastMember = CastMember.newMember(name(), type());

        final var createdCastMember = castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember)).toAggregate();

        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        castMemberGateway.deleteById(createdCastMember.getId());

        // then
        Assertions.assertEquals(0, castMemberRepository.count());

    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteById_shouldBeIgnored() {
        // given
        final var aCastMember = CastMember.newMember(name(), type());

        final var createdCastMember = castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember)).toAggregate();

        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        castMemberGateway.deleteById(CastMemberID.from("invalid-id"));

        // then
        Assertions.assertEquals(1, castMemberRepository.count());
    }

    @Test
    public void givenAValidId_whenCallsFindById_shouldReturnACastMember() {
        // given
        final var aCastMember = CastMember.newMember(name(), type());

        final var createdCastMember = castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember)).toAggregate();

        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        final var actualCastMember = castMemberGateway.findById(createdCastMember.getId()).get();

        // then
        Assertions.assertEquals(createdCastMember.getId(), actualCastMember.getId());
        Assertions.assertEquals(createdCastMember.getName(), actualCastMember.getName());
        Assertions.assertEquals(createdCastMember.getType(), actualCastMember.getType());
        Assertions.assertEquals(createdCastMember.getCreatedAt(), actualCastMember.getCreatedAt());
        Assertions.assertEquals(createdCastMember.getUpdatedAt(), actualCastMember.getUpdatedAt());
    }

    @Test
    public void givenAnInvalidId_whenCallsFindById_shouldReturnEmpty() {
        // given
        final var aCastMember = CastMember.newMember(name(), type());

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember)).toAggregate();

        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        final var actualCastMember = castMemberGateway.findById(CastMemberID.from("invalid-id"));

        // then
        Assertions.assertTrue(actualCastMember.isEmpty());
    }

    @Test
    public void givenAEmptyList_whenCallsFindAll_shouldReturnEmptyList() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedDirection = "asc";
        final var expectedSort = "name";
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        // when

        final var actualResult = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedTotal, actualResult.items().size());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "vin, 0, 10, 1, 1, Vin Diesel",
        "kit, 0, 10, 1, 1, Kit Harington",
        "quen, 0, 10, 1, 1, Quentin Tarantino",
        "jason, 0, 10, 1, 1, Jason Mamoa",
        "MART, 0, 10, 1, 1, Martin Scorsese"
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final int expectedTotal,
        final String expectedName
    ) {

        // given
        mockCastMembers();

        final var expectedDirection = "asc";
        final var expectedSort = "name";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        // when
        final var actualResult = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "name,asc,0,10,5,5,Jason Mamoa",
        "name,desc,0,10,5,5,Vin Diesel",
        "createdAt,asc,0,10,5,5,Kit Harington",
        "createdAt,desc,0,10,5,5,Martin Scorsese"})
    public void givenAValidSort_whenCallsFindAll_shouldReturnSorted(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final int expectedTotal,
        final String expectedName
    ) {

        // given
        mockCastMembers();

        final var expectedTerms = "";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        // when
        final var actualResult = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "0,2,2,5,Jason Mamoa;Kit Harington",
        "1,2,2,5,Martin Scorsese;Quentin Tarantino",
        "2,2,1,5,Vin Diesel",})
    public void givenAValidPagination_whenCallsFindAll_shouldReturnPaginated(
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final int expectedTotal,
        final String expectedName
    ) {
        // given
        mockCastMembers();

        final var expectedTerms = "";
        final var expectedDirection = "asc";
        final var expectedSort = "name";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        // when
        final var actualResult = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        var index = 0;
        for (final var expectedNameValue : expectedName.split(";")) {
            Assertions.assertEquals(expectedNameValue, actualResult.items().get(index).getName());
            index++;
        }
    }


    private void mockCastMembers() {
        final var aCastMemberList = List.of(
            CastMemberJpaEntity.from(CastMember.newMember("Kit Harington", CastMemberType.ACTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Vin Diesel", CastMemberType.ACTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Quentin Tarantino", CastMemberType.DIRECTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Jason Mamoa", CastMemberType.ACTOR)),
            CastMemberJpaEntity.from(CastMember.newMember("Martin Scorsese", CastMemberType.DIRECTOR))
        );
        castMemberRepository.saveAllAndFlush(aCastMemberList);
    }

    @Test
    public void givenPrePersistedGenres_whenCallsExistsByIds_shouldReturnIds() {

        final var jasonMomoa = CastMember.newMember(CastMembers.JASON_MOMOA.getName(), CastMemberType.ACTOR);
        final var vinDiesel = CastMember.newMember(CastMembers.VIN_DIESEL.getName(), CastMemberType.ACTOR);

        Assertions.assertEquals(0, castMemberRepository.count());

        castMemberRepository.saveAll(List.of(CastMemberJpaEntity.from(jasonMomoa), CastMemberJpaEntity.from(vinDiesel)));

        Assertions.assertEquals(2, castMemberRepository.count());

        final var expectedIds = List.of(jasonMomoa.getId(), vinDiesel.getId());
        final var ids = List.of(jasonMomoa.getId(), vinDiesel.getId(), CastMemberID.from("123"));
        final var actualResult = castMemberGateway.existsByIds(ids);

        Assertions.assertTrue(expectedIds.size() == actualResult.size()
                && expectedIds.containsAll(actualResult));


    }

}