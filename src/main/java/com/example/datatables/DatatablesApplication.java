package com.example.datatables;

import com.example.datatables.db.InitDbDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(value = { "com.example.datatables", "org.springframework.data.jpa.datatables.easy" })
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = "com.example.datatables.persistence.repository")
public class DatatablesApplication {

	@Autowired
	private InitDbDemo initDbDemo;

	public static void main(String[] args) {
		SpringApplication.run(DatatablesApplication.class, args);
	}

	@PostConstruct
	public void gen() {
		initDbDemo.init();
	}
}
