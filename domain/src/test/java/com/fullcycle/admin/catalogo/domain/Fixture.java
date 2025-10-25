package com.fullcycle.admin.catalogo.domain;

import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import com.fullcycle.admin.catalogo.domain.video.MediaStatus;
import com.fullcycle.admin.catalogo.domain.video.Rating;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.github.javafaker.Faker;

import java.time.Year;
import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.List;
import static io.vavr.API.Match;

public final class Fixture {

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static String title() {

        return FAKER.options().option(
            "The Lord of the Rings",
            "The Hobbit",
            "The Lord of the Rings: The Fellowship of the Ring",
            "The Lord of the Rings: The Two Towers",
            "The Lord of the Rings: The Return of the King",
            "Saint Seiya"
        );
    }

    public static Integer releaseYear() {
        return FAKER.number().numberBetween(2010, 2030);
    }

    public static Double duration() {
        return FAKER.number().randomDouble(2, 60, 180);
    }

    public static Boolean bool() {
        return FAKER.bool().bool();
    }

    public static final class CastMembers {

        public static final CastMember VIN_DIESEL = CastMember.newMember("Vin Diesel", CastMemberType.ACTOR);
        public static final CastMember JASON_MOMOA = CastMember.newMember("Jason Mamoa", CastMemberType.ACTOR);
        public static final CastMember STEVEN_SPIELBERG = CastMember.newMember("Steven Spielberg", CastMemberType.DIRECTOR);

        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.values());
        }

        public static CastMember castMember() {
            return FAKER.options().option(CastMember.with(VIN_DIESEL), CastMember.with(JASON_MOMOA), CastMember.with(STEVEN_SPIELBERG));
        }
    }

    public static final class Categories {

        public static final Category FILMES = Category.newCategory("Filmes", "Filmes", true);
        public static final Category SERIES = Category.newCategory("Series", "Series", true);
        public static final Category ANIME = Category.newCategory("Anime", "Anime", true);
        public static final Category DOCUMENTARIES = Category.newCategory("Documentaries", "Documentaries", true);
        public static final Category SPORTS = Category.newCategory("Sports", "Sports", true);

        public static Category category() {
            return FAKER.options().option(Category.with(FILMES), Category.with(SERIES), Category.with(ANIME), Category.with(DOCUMENTARIES), Category.with(SPORTS));
        }

    }

    public static final class Genres {
        public static final Genre DRAMA = Genre.newGenre("Drama", true);
        public static final Genre COMEDIA = Genre.newGenre("Comedia",  true);
        public static final Genre AVENTURA = Genre.newGenre("Aventura",  true);
        public static final Genre FICCAO = Genre.newGenre("Ficcao", true);

        public static Genre genre() {
            return FAKER.options().option(Genre.with(DRAMA), Genre.with(COMEDIA), Genre.with(AVENTURA), Genre.with(FICCAO));
        }
    }

    public static final class Videos {

        public static Video systemDesigner() {
            return Video.newVideo(
                title(),
                description(),
                Year.of(releaseYear()),
                duration(),
                rating(),
                bool(),
                bool(),
                Set.of(Categories.category().getId()),
                Set.of(Genres.genre().getId()),
                Set.of(CastMembers.castMember().getId())
            );
        }
        public static String description() {

            return FAKER.options().option(
                "Em uma terra fantástica e única, um hobbit recebe de presente de seu tio um anel mágico e maligno que precisa ser destruído antes que caia nas mãos do mal. Para isso, o hobbit Frodo tem um caminho árduo pela frente, onde encontra perigo, medo e seres bizarros. Ao seu lado para o cumprimento desta jornada, ele aos poucos pode contar com outros hobbits, um elfo, um anão, dois humanos e um mago, totalizando nove seres que formam a Sociedade do Anel.",
                "Após a captura de Merry e Pippy pelos orcs, a Sociedade do Anel é dissolvida. Frodo e Sam seguem sua jornada rumo à Montanha da Perdição para destruir o anel e descobrem que estão sendo perseguidos pelo misterioso Gollum. Enquanto isso, Aragorn, o elfo e arqueiro Legolas e o anão Gimli partem para resgatar os hobbits sequestrados e chegam ao reino de Rohan, onde o rei Theoden foi vítima de uma maldição mortal de Saruman.",
                "Sauron planeja um grande ataque a Minas Tirith, capital de Gondor, o que faz com que Gandalf e Pippin partam para o local na intenção de ajudar a resistência. Frodo, Sam e Gollum seguem sua viagem rumo à Montanha da Perdição para destruir o anel."
            );
        }

        public static Rating rating() {
            return FAKER.options().option(Rating.values());
        }

        public static VideoMediaType mediaType() {
            return FAKER.options().option(VideoMediaType.values());
        }

        public static Resource resource(VideoMediaType type) {
            final String contentType = Match(type).of(
                Case($(List(VideoMediaType.VIDEO, VideoMediaType.TRAILER)::contains), "video/mp4"),
                Case($(), "image/jpeg")
            );
            String checksum = IdUtils.uuid();
            final byte[] content = "Conteudo".getBytes();
            return Resource.with(checksum, content, contentType, type.name().toLowerCase());
        }

        public static AudioVideoMedia audioVideo(final VideoMediaType type) {
            String checksum = IdUtils.uuid();
            return AudioVideoMedia.with(
                checksum,
                checksum,
                type.name().toLowerCase(),
                "/videos/" + checksum,
                "",
                MediaStatus.PENDING
            );
        }

        public static ImageMedia imageMedia(final VideoMediaType type) {
            String checksum = IdUtils.uuid();
            return ImageMedia.with(
                checksum,
                type.name().toLowerCase(),
                "/images/" + checksum
            );
        }
    }

}
