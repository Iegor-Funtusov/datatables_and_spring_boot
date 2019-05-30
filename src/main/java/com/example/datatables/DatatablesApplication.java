package com.example.datatables;

import com.example.datatables.persistence.entities.Department;
import com.example.datatables.persistence.entities.Employee;
import com.example.datatables.persistence.enums.Position;
import com.example.datatables.persistence.repository.DepartmentRepository;
import com.example.datatables.persistence.repository.EmployeeRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = "com.example.datatables.persistence.repository")
public class DatatablesApplication {

	private final DepartmentRepository departmentRepository;
	private final EmployeeRepository employeeRepository;

	private int leftLimit = 97;
	private int rightLimit = 122;
	private int targetStringLength = 10;

	public DatatablesApplication(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
		this.departmentRepository = departmentRepository;
		this.employeeRepository = employeeRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(DatatablesApplication.class, args);
	}

	@PostConstruct
	public void gen() {
		for (int a = 0; a < 21; a++) {

			Department department = new Department();
			department.setName(rand());
			department = departmentRepository.save(department);

			for (int qq = 0; qq < 38; qq++) {
				Employee employee = new Employee();
				employee.setPosition(Position.WORKER);
				List<Employee> employees = employeeRepository.findAllByPosition(Position.OWNER);
				if (employees.size() == 0) {
					employee.setPosition(Position.OWNER);
				}
				employees = employeeRepository.findAllByPositionAndDepartment(Position.DIRECTOR, department);
				if (employees.size() == 0) {
					employee.setPosition(Position.DIRECTOR);
				}
				employees = employeeRepository.findAllByPositionAndDepartment(Position.MANAGER, department);
				if (employees.size() <= 10) {
					employee.setPosition(Position.MANAGER);
				}
				employees = employeeRepository.findAllByPositionAndDepartment(Position.ACCOUNTANT, department);
				if (employees.size() <= 5) {
					employee.setPosition(Position.ACCOUNTANT);
				}
				employee.setDepartment(department);
				employee.setFirstName(rand());
				employee.setLastName(rand());
				employee.setSalary(random());
				employeeRepository.save(employee);
			}
		}
	}

	private String rand() {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int)
					(random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		return buffer.toString();
	}

	public static BigDecimal random() {
		int range = 10000;
		BigDecimal max = new BigDecimal(range);
		BigDecimal randFromDouble = new BigDecimal(Math.random());
		BigDecimal actualRandomDec = randFromDouble.multiply(max);
		actualRandomDec = actualRandomDec
				.setScale(2, BigDecimal.ROUND_DOWN);
		return actualRandomDec;
	}
}
