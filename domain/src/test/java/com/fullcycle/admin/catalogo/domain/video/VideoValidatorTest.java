package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

public class VideoValidatorTest extends UnitTest {

    @Test
    public void givenANullTitle_whenCallsValidate_thenShouldReceiveError() {
        // given
        final String expectedTitle = null;
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be null";

        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );
        // when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));
        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenAnEmptyTitle_whenCallsValidate_thenShouldReceiveError() {
        // given
        final var expectedTitle = "";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be empty";

        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );
        // when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));
        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenATitleWithGreaterThan255Characters_whenCallsValidate_thenShouldReceiveError() {
        // given
        final var expectedTitle = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' must be between 1 and 255 characters";

        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );
        // when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));
        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenAnEmptyDescription_whenCallsValidate_thenShouldReceiveError() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = "";
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );
        // when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));
        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenADescriptionWithGreaterThan4000Characters_whenCallsValidate_thenShouldReceiveError() {
        // given
        final var expectedTitle = "Flash";
        final var expectedDescription = """
            É claro que o comprometimento entre as equipes desafia a capacidade de equalização das novas\s
            proposições. A certificação de metodologias que nos auxiliam a lidar com a constante divulgação das\s
            informações maximiza as possibilidades por conta do processo de comunicação como um todo. Nunca é\s
            demais lembrar o peso e o significado destes problemas, uma vez que a consulta aos diversos militantes\s
            oferece uma interessante oportunidade para verificação do levantamento das variáveis envolvidas.\s
            O cuidado em identificar pontos críticos na determinação clara de objetivos obstaculiza a apreciação\s
            da importância do impacto na agilidade decisória. Pensando mais a longo prazo, o início da atividade\s
            geral de formação de atitudes assume importantes posições no estabelecimento dos paradigmas corporativos.
            O empenho em analisar a complexidade dos estudos efetuados deve passar por modificações\s
            independentemente das condições inegavelmente apropriadas. Por outro lado, a adoção de políticas\s
            descentralizadoras aponta para a melhoria das diversas correntes de pensamento. A nível organizacional,\s
            a estrutura atual da organização representa uma abertura para a melhoria de todos os recursos\s
            funcionais envolvidos. É importante questionar o quanto a hegemonia do ambiente político prepara-nos\s
            para enfrentar situações atípicas decorrentes dos procedimentos normalmente adotados. Ainda assim,\s
            existem dúvidas a respeito de como o consenso sobre a necessidade de qualificação exige a precisão e\s
            a definição da gestão inovadora da qual fazemos parte.
            Não obstante, o novo modelo estrutural aqui preconizado auxilia a preparação e a composição dos\s
            conhecimentos estratégicos para atingir a excelência. Percebemos, cada vez mais, que o fenômeno da\s
            Internet promove a alavancagem do retorno esperado a longo prazo. Por conseguinte, o acompanhamento\s
            das preferências de consumo nos obriga à análise das formas de ação.
            No entanto, não podemos esquecer que a expansão dos mercados mundiais facilita a criação do remanejamento\s
            dos quadros funcionais. Desta maneira, a necessidade de renovação processual cumpre um papel essencial na\s
            formulação do fluxo de informações. Todavia, a consolidação das estruturas estimula a padronização do sistema\s
            de formação de quadros que corresponde às necessidades. A prática cotidiana prova que a percepção das dificuldades\s
            ainda não demonstrou convincentemente que vai participar na mudança dos níveis de motivação departamental.
            As experiências acumuladas demonstram que o desafiador cenário globalizado apresenta tendências no sentido de\s
            aprovar a manutenção do sistema de participação geral. Todas estas questões, devidamente ponderadas, levantam\s
            dúvidas sobre se a execução dos pontos do programa estende o alcance e a importância das diretrizes de\s
            desenvolvimento para o futuro. O que temos que ter sempre em mente é que o entendimento das metas propostas\s
            pode nos levar a considerar a reestruturação de alternativas às soluções ortodoxas. No mundo atual, a revolução\s
            dos costumes agrega valor ao estabelecimento dos relacionamentos verticais entre as hierarquias. Acima de tudo,\s
            é fundamental ressaltar que a crescente influência da mídia possibilita uma melhor visão global dos índices pretendidos.
            Do mesmo modo, a contínua expansão de nossa atividade talvez venha a ressaltar a relatividade das regras de\s
            conduta normativas. Caros amigos, o desenvolvimento contínuo de distintas formas de atuação não pode mais se\s
            dissociar do orçamento setorial. O incentivo ao avanço tecnológico, assim como a mobilidade dos capitais\s
            internacionais faz parte de um processo de gerenciamento dos modos de operação convencionais.
            Assim mesmo, o surgimento do comércio virtual afeta positivamente a correta previsão das direções preferenciais no\s
            sentido do progresso. Podemos já vislumbrar o modo pelo qual o aumento do diálogo entre os diferentes setores produtivos\s
            causa impacto indireto na reavaliação das posturas dos órgãos dirigentes com relação às suas atribuições. Evidentemente,\s
            o julgamento imparcial das eventualidades garante a contribuição de um grupo importante na determinação das condições financeiras\s
            e administrativas exigidas.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' must be between 1 and 4000 characters";

        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );
        // when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));
        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenANullRating_whenCallsValidate_thenShouldReceiveError() {
        // given
        final var expectedTitle = "Flash: O Filme";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final var expectedLaunchedAt = Year.of(2022);
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final Rating expectedRating = null;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'rating' should not be null";

        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );
        // when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));
        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }

    @Test
    public void givenANullLaunchedAt_whenCallsValidate_thenShouldReceiveError() {
        // given
        final var expectedTitle = "Flash: O Filme";
        final var expectedDescription = """
            Os mundos colidem quando Flash viaja no tempo para mudar os eventos do passado.
            No entanto, quando sua tentativa de salvar sua família altera o futuro, ele fica 
            preso em uma realidade na qual o General Zod voltou, ameaçando a aniquilação.
            """;
        final Year expectedLaunchedAt = null;
        final var expectedDuration = 180.10;
        final var expectedOpened = false;
        final var expectedPublished = false;
        final var expectedRating = Rating.L;
        final var expectedCategories = Set.of(CategoryID.unique());
        final var expectedGenre = Set.of(GenreID.unique());
        final var expectedMembers = Set.of(CastMemberID.unique());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'launchedAt' should not be null";

        final var actualVideo = Video.newVideo(
            expectedTitle,
            expectedDescription,
            expectedLaunchedAt,
            expectedDuration,
            expectedRating,
            expectedOpened,
            expectedPublished,
            expectedCategories,
            expectedGenre,
            expectedMembers
        );
        // when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> actualVideo.validate(new ThrowsValidationHandler()));
        // then
        Assertions.assertEquals(expectedErrorCount, actualError.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualError.getErrors().get(0).message());
    }
}
