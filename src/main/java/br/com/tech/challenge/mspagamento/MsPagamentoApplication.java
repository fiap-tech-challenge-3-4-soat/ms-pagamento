package br.com.tech.challenge.mspagamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsPagamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPagamentoApplication.class, args);
	}

}
