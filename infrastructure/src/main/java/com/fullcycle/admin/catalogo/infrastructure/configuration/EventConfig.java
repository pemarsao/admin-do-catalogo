package com.fullcycle.admin.catalogo.infrastructure.configuration;

import com.fullcycle.admin.catalogo.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.amqp.QueueProperties;
import com.fullcycle.admin.catalogo.infrastructure.services.EventService;
import com.fullcycle.admin.catalogo.infrastructure.services.impl.RabbitEventService;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    @VideoCreatedQueue
    public EventService videoCreatedEventService(
        @VideoCreatedQueue final QueueProperties pros,
        final RabbitOperations ops
    ) {
        return new RabbitEventService(
            pros.getExchange(),
            pros.getRoutingKey(),
            ops
        );
    }
}
