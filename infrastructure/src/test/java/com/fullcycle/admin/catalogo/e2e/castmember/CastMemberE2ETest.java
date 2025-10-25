package com.fullcycle.admin.catalogo.e2e.castmember;

import com.fullcycle.admin.catalogo.E2ETest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.e2e.MockDsl;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@E2ETest
@Testcontainers
public class CastMemberE2ETest implements MockDsl {

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:8.2.0")
        .withPassword("123456")
        .withUsername("root")
        .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(3306));
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Test
    public void asACastMemberAdminIShouldBeAbleToCreateACastMemberWithValidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);

        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aMemberId = givenCastMember(expectedName, expectedType);

        final var actualMember = this.castMemberRepository.findById(aMemberId.getValue()).get();

        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertNotNull(actualMember.getCreatedAt());
        Assertions.assertNotNull(actualMember.getUpdatedAt());
        Assertions.assertEquals(actualMember.getCreatedAt(), actualMember.getUpdatedAt());

    }

    @Test
    public void asACastMemberAdminIShouldSeeATreatedErrorCreatingACastMemberWithInvalidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);

        final String expectedName = null;
        final var expectedType = Fixture.CastMembers.type();
        final var expectedErrorMessage = "'name' should not be null";

        givenCastMemberResult(expectedName, expectedType)
            .andExpect(status().isUnprocessableEntity())
            .andExpect(header().string("Location", nullValue()))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateThruAllCastMembers() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);

        givenCastMember("Vin Diesel", CastMemberType.ACTOR);
        givenCastMember("Quentin Tarantino", CastMemberType.DIRECTOR);
        givenCastMember("Jason Mamoa", CastMemberType.ACTOR);

        listCastMembers(0, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Jason Mamoa")));

        listCastMembers(1, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(1)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Quentin Tarantino")));

        listCastMembers(2, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(2)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Vin Diesel")));

        listCastMembers(3, 1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(3)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(0)));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchThruAllCastMembers() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);

        givenCastMember("Vin Diesel", CastMemberType.ACTOR);
        givenCastMember("Quentin Tarantino", CastMemberType.DIRECTOR);
        givenCastMember("Jason Mamoa", CastMemberType.ACTOR);

        listCastMembers(0, 1, "vin")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(1)))
            .andExpect(jsonPath("$.total", equalTo(1)))
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Vin Diesel")));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllMembersByNameDesc() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);

        givenCastMember("Vin Diesel", CastMemberType.ACTOR);
        givenCastMember("Quentin Tarantino", CastMemberType.DIRECTOR);
        givenCastMember("Jason Mamoa", CastMemberType.ACTOR);

        listCastMembers(0, 3, "", "name", "desc")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.current_page", equalTo(0)))
            .andExpect(jsonPath("$.per_page", equalTo(3)))
            .andExpect(jsonPath("$.total", equalTo(3)))
            .andExpect(jsonPath("$.items", hasSize(3)))
            .andExpect(jsonPath("$.items[0].name", equalTo("Vin Diesel")))
            .andExpect(jsonPath("$.items[1].name", equalTo("Quentin Tarantino")))
            .andExpect(jsonPath("$.items[2].name", equalTo("Jason Mamoa")));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToGetAMembersById() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        final var castMemberId = givenCastMember(expectedName, expectedType);

        final var actualMember = retrieveCastMember(castMemberId);

        Assertions.assertEquals(expectedName, actualMember.name());
        Assertions.assertEquals(expectedType.name(), actualMember.type());
        Assertions.assertNotNull(actualMember.createdAt());
        Assertions.assertNotNull(actualMember.updatedAt());
        Assertions.assertEquals(actualMember.createdAt(), actualMember.updatedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByGettingANotFoundCastMember() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        givenCastMember(expectedName, expectedType);

        getCastMember(CastMemberID.from("123"))
            .andExpect(status().isNotFound());

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToUpdateAMembersById() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var castMemberId = givenCastMember("vin di", CastMemberType.DIRECTOR);
        updateCastMember(castMemberId, expectedName, expectedType)
            .andExpect(status().isOk());

        final var actualMember = retrieveCastMember(castMemberId);

        Assertions.assertEquals(expectedName, actualMember.name());
        Assertions.assertEquals(expectedType.name(), actualMember.type());
        Assertions.assertNotNull(actualMember.createdAt());
        Assertions.assertNotNull(actualMember.updatedAt());
        Assertions.assertNotEquals(actualMember.createdAt(), actualMember.updatedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByUpdatingAMembersById() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);
        final String expectedName = "";
        final var expectedType = Fixture.CastMembers.type();
        final var expectedErrorMessage = "'name' should not be empty";

        final var castMemberId = givenCastMember("vin di", CastMemberType.DIRECTOR);
        updateCastMember(castMemberId, expectedName, expectedType)
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteAMembersById() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);
        givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        final var castMemberId = givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        Assertions.assertEquals(castMemberRepository.count(), 2);
        deleteCastMember(castMemberId)
            .andExpect(status().isNoContent());
        Assertions.assertEquals(castMemberRepository.count(), 1);
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteAMembersByInvalidId() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(castMemberRepository.count(), 0);
        givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        givenCastMember(Fixture.name(), Fixture.CastMembers.type());
        Assertions.assertEquals(castMemberRepository.count(), 2);
        deleteCastMember(CastMemberID.from("123"))
            .andExpect(status().isNoContent());
        Assertions.assertEquals(castMemberRepository.count(), 2);
    }
}
