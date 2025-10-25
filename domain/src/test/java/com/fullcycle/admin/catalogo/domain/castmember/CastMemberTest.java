package com.fullcycle.admin.catalogo.domain.castmember;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CastMemberTest extends UnitTest {

    @Test
    public void givenAValidParams_whenCallNewMember_thenInstantiateACastMember() {

        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var actualCastMember = CastMember.newMember(expectedName, expectedType);

        Assertions.assertNotNull(actualCastMember);
        Assertions.assertNotNull(actualCastMember.getId());
        Assertions.assertEquals(expectedName, actualCastMember.getName());
        Assertions.assertEquals(expectedType, actualCastMember.getType());
        Assertions.assertNotNull(actualCastMember.getCreatedAt());
        Assertions.assertNotNull(actualCastMember.getUpdatedAt());
        Assertions.assertEquals(actualCastMember.getCreatedAt(), actualCastMember.getUpdatedAt());

    }

    @Test
    public void givenAnInvalidNullName_whenCallNewMember_thenShouldNotificationError() {
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewMember_thenShouldNotificationError() {
        final var expectedName = "   ";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameWithMoreThen255Characters_whenCallNewMember_thenShouldNotificationError() {
        final var expectedName = """
            As experiências acumuladas demonstram que a necessidade de renovação processual maximiza as possibilidades por conta das direções preferenciais no sentido do progresso.
            Não obstante, o entendimento das metas propostas exige a precisão e a definição de alternativas específicas. Assim mesmo, o consenso sobre a 
            necessidade de qualificação facilita a criação de todos os recursos funcionais envolvidos.
            """;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 1 and 255 characters";

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNullType_whenCallNewMember_thenShouldNotificationError() {
        final var expectedName = "Vin Diesel";
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> CastMember.newMember(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }

    @Test
    public void givenAValidParams_whenCallUpdateAndValidate_thenShouldSuccess() {

        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var actualCastMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);
        final var expectedCastMemberID = actualCastMember.getId();
        final var updatedAt = actualCastMember.getUpdatedAt();

        actualCastMember.update(expectedName, expectedType);

        Assertions.assertNotNull(actualCastMember);
        Assertions.assertEquals(expectedCastMemberID, actualCastMember.getId());
        Assertions.assertEquals(expectedName, actualCastMember.getName());
        Assertions.assertEquals(expectedType, actualCastMember.getType());
        Assertions.assertNotNull(actualCastMember.getCreatedAt());
        Assertions.assertTrue(updatedAt.isBefore(actualCastMember.getUpdatedAt()));

    }

    @Test
    public void givenAnValidParams_whenCallUpdateWithNullName_thenShouldBeNotificationError() {
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualCastMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> actualCastMember.update(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }

    @Test
    public void givenAnValidParams_whenCallUpdateWithEmptyName_thenShouldBeNotificationError() {
        final var expectedName = "  ";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualCastMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> actualCastMember.update(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }

    @Test
    public void givenAnValidParams_whenCallUpdateWithNameWithMoreThen255Characters_thenShouldBeNotificationError() {
        final var expectedName = """
            As experiências acumuladas demonstram que a necessidade de renovação processual maximiza as possibilidades por conta das direções preferenciais no sentido do progresso.
            Não obstante, o entendimento das metas propostas exige a precisão e a definição de alternativas específicas. Assim mesmo, o consenso sobre a 
            necessidade de qualificação facilita a criação de todos os recursos funcionais envolvidos.
            """;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 1 and 255 characters";

        final var actualCastMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> actualCastMember.update(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }

    @Test
    public void givenAnValidParams_whenCallUpdateWithNullType_thenShouldBeNotificationError() {
        final var expectedName = "Vin Diesel";
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var actualCastMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);

        final var castMemberException = Assertions.assertThrows(
            NotificationException.class,
            () -> actualCastMember.update(expectedName, expectedType)
        );

        Assertions.assertNotNull(castMemberException);
        Assertions.assertEquals(expectedErrorCount, castMemberException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, castMemberException.getErrors().get(0).message());
    }


}
