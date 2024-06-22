package br.com.tech.challenge.mspagamento.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${queue.filas.pedidos_criados}")
    private String pedidosCriados;

    @Value("${queue.filas.pagamentos_confirmados}")
    private String pagamentosConfirmados;

    @Value("${queue.filas.pagamentos_nao_gerados}")
    private String pagamentosNaoGerados;

    @Value("${queue.exchange.fanoutPedido}")
    private String queuePedidoExchange;

    @Value("${queue.exchange.fanoutPagamento}")
    private String queuePagamentoExchange;

    @Value("${queue.exchange.deadLetterPedido}")
    private String queueDeadLetterExchange;

    @Bean
    public RabbitAdmin createRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> initiateAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Queue queuePedidosCriados() {
        return QueueBuilder
                .nonDurable(pedidosCriados)
                .deadLetterExchange(queueDeadLetterExchange)
                .build();
    }

    @Bean
    public FanoutExchange fanoutPedidosExchange() {
        return ExchangeBuilder
                .fanoutExchange(queuePedidoExchange)
                .build();
    }

    @Bean
    public Binding bindPedidoExchange() {
        return BindingBuilder
                .bind(queuePedidosCriados())
                .to(fanoutPedidosExchange());
    }







    @Bean
    public Queue queuePagamentosConfirmados() {
        return QueueBuilder
                .nonDurable(pagamentosConfirmados)
                .build();
    }

    @Bean
    public FanoutExchange fanoutPagamentosExchange() {
        return ExchangeBuilder
                .fanoutExchange(queuePagamentoExchange)
                .build();
    }

    @Bean
    public Binding bindPagamentosExchange() {
        return BindingBuilder
                .bind(queuePagamentosConfirmados())
                .to(fanoutPagamentosExchange());
    }






    //DLQ
    @Bean
    public Queue queuePagamentosNaoGerados() {
        return QueueBuilder
                .nonDurable(pagamentosNaoGerados)
                .build();
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return ExchangeBuilder
                .fanoutExchange(queueDeadLetterExchange)
                .build();
    }

    @Bean
    public Binding bindPagamentoDlqExchange() {
        return BindingBuilder
                .bind(queuePagamentosNaoGerados())
                .to(deadLetterExchange());
    }
}
