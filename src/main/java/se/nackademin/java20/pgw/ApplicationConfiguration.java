package se.nackademin.java20.pgw;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.nackademin.java20.pgw.application.PaymentService;
import se.nackademin.java20.pgw.domain.PaymentNotificationService;
import se.nackademin.java20.pgw.domain.PaymentRepository;
import se.nackademin.java20.pgw.persistance.PaymentRepositoryHibernate;
import se.nackademin.java20.pgw.persistance.RabbitNotificationService;

import javax.persistence.EntityManager;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

    @Bean
    public PaymentRepository paymentRepository(EntityManager em) {
        return new PaymentRepositoryHibernate(em);
    }

    @Bean
    public PaymentNotificationService paymentNotificationService(RabbitTemplate template, ObjectMapper objectMapper) {
        return new RabbitNotificationService(template, objectMapper);
    }

    final String topicExchangeName = "payments-exchange";

    static final String queueName = "payments";

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("#");
    }

    @Bean
    public PaymentService personalFinanceService(PaymentRepository paymentRepository, PaymentNotificationService paymentNotificationService) {
        return new PaymentService(paymentRepository, paymentNotificationService);
    }


}
