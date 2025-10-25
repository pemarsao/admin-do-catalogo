package com.fullcycle.admin.catalogo.domain;

import com.fullcycle.admin.catalogo.domain.event.DomainEvent;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidateHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityTest extends UnitTest {

    @Test
    public void givenNullAsEvent_whenInstantiate_shouldBOk() {
        //given
        final List<DomainEvent> events = null;
        //when
        final var entity = new DummyEntity(new DummyID(), events);
        //then
        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertTrue(entity.getDomainEvents().isEmpty());
    }

    @Test
    public void givenDomainEvents_whenPassInConstructor_shouldADefensiveClone() {
        //given
        final var expectedEvents = 1;
        final List<DomainEvent> events = new ArrayList<>();
        events.add(new DummyEvent());
        //when
        final var entity = new DummyEntity(new DummyID(), events);
        //then
        Assertions.assertNotNull(entity.getDomainEvents());
        Assertions.assertEquals(expectedEvents, entity.getDomainEvents().size());
        Assertions.assertThrows(RuntimeException.class, () -> {
            final var domainEvents = entity.getDomainEvents();
            domainEvents.add(new DummyEvent());
        });
    }

    @Test
    public void givenDomainEvents_whenRegisterEvent_shouldAddEventToList() {
        //given
        final var expectedEvents = 1;
        final var anEntity = new DummyEntity(new DummyID(), new ArrayList<>());

        //when
        anEntity.registerEvent(new DummyEvent());
        //then
        Assertions.assertNotNull(anEntity.getDomainEvents());
        Assertions.assertEquals(expectedEvents, anEntity.getDomainEvents().size());
    }

    @Test
    public void givenAFewDomainEvents_whenPublishEvents_shouldCallPublisherAndClearTheList() {
        //given
        final var expectedEvents = 0;
        final var expectedSentEvents = 2;
        final var counter = new AtomicInteger(0);
        final var anEntity = new DummyEntity(new DummyID(), new ArrayList<>());
        anEntity.registerEvent(new DummyEvent());
        anEntity.registerEvent(new DummyEvent());
        Assertions.assertEquals(2, anEntity.getDomainEvents().size());
        //when
        anEntity.publishDomainEvent(event -> {
            counter.incrementAndGet();
        });
        //then
        Assertions.assertNotNull(anEntity.getDomainEvents());
        Assertions.assertEquals(expectedEvents, anEntity.getDomainEvents().size());
        Assertions.assertEquals(expectedSentEvents, counter.get());
    }

    public static class DummyID extends Identifier {
        private final String value;
        public DummyID() {
            this.value = IdUtils.uuid();
        }
        @Override
        public String getValue() {
            return this.value;
        }
    }

    public static class DummyEvent implements DomainEvent {
        @Override
        public Instant occurredOn() {
            return Instant.now();
        }
    }

    public static class DummyEntity extends Entity<DummyID> {

        protected DummyEntity(DummyID dummyID, List<DomainEvent> domainEvents) {
            super(dummyID, domainEvents);
        }

        @Override
        public void validate(ValidateHandler handler) {

        }
    }

}
