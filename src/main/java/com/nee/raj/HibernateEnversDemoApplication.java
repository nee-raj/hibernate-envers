package com.nee.raj;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;
import org.hsqldb.util.DatabaseManagerSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nee.raj.entity.Customer;
import com.nee.raj.repo.CustomerRepository;

@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class HibernateEnversDemoApplication {
	
	private static final Logger log = LoggerFactory.getLogger(HibernateEnversDemoApplication.class);
	
	@Autowired
	private  EntityManager entityManager;
	
	@Autowired
	private  EntityManagerFactory entityManagerFactory;
	
	 @RequestMapping("/greeting")
	    String home() {
	        return "Hello World!";
	    }

	public static void main(String[] args) {
		SpringApplication.run(HibernateEnversDemoApplication.class, args);
	}
	
	
	@Bean
	public CommandLineRunner demo(CustomerRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Customer("Irina", "Kavsan"));
			repository.save(new Customer("Kristin", "Dahl"));
			repository.save(new Customer("Mhay", "Reyes"));
			repository.save(new Customer("Sash", "Kavsan"));
			repository.save(new Customer("Aaron", "Breen"));
			repository.save(new Customer("Clara", "Kavsan"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				log.info(customer.toString());
			}
            log.info("");

			// fetch an individual customer by ID
			Customer customer = repository.findOne(1L);
			log.info("Customer found with findOne(1L):");
			log.info("--------------------------------");
			log.info(customer.toString());
            log.info("");

			fetchCustomersByLastName(repository, "Kavsan");
            
            // update lastname of Clara and Mhay
            updateLastName(repository, "Clara", "Breen");
            updateLastName(repository, "Mhay", "Kavsan");
            
         // fetch customers by last name
            fetchCustomersByLastName(repository, "Kavsan");
            
            
         // Verifying Auditing. 
         printPersonHistory(3l);
         
		};
	}
	
	
	private void printPersonHistory(long personId) {
		StringBuilder sb = new StringBuilder();
		
		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		AuditReader reader = AuditReaderFactory.get(entityManager);
		List personHistory = reader.createQuery()
                .forRevisionsOfEntity(Customer.class, false, true)
                .add(AuditEntity.id().eq(personId))
                .getResultList();
      if (personHistory.size() == 0) {
      sb.append("A person with id ").append(personId).append(" does not exist.\n");
  } else {
      for (Object historyObj : personHistory) {
          Object[] history = (Object[]) historyObj;
          DefaultRevisionEntity revision = (DefaultRevisionEntity) history[1];
          sb.append("revision = ").append(revision.getId()).append(", ");
          sb.append( history[0].toString() );
          sb.append(" (").append(revision.getRevisionDate()).append(")\n");
          sb.append(" (").append(revision.getRevisionDate()).append(")\n");
      }
  }
      System.out.println("Output" + sb.toString());
		
		entityManager.getTransaction().commit();
		entityManager.close();
	
    }
	

	private void printCustomer(Customer customer) {
		customer.toString();
	}

	private void updateLastName(CustomerRepository repository, String firstName, String newLastName) {
		List<Customer> members =  repository.findByFirstName(firstName);
		for( Customer customer : members) {
		customer.setLastName(newLastName);
		repository.save(customer);
		}
	}

	private void fetchCustomersByLastName(CustomerRepository repository, String lastName) {
		log.info("Customer found with findByLastName('"+ lastName +"'):");
		log.info("--------------------------------------------");
		for (Customer member : repository.findByLastName(lastName)) {
			log.info(member.toString());
		}
		log.info("");
	}
		
	//default username : sa, password : ''
	// you have to do headless = false
	//  -Djava.awt.headless=false
	@PostConstruct
	public void getDbManager(){
	   DatabaseManagerSwing.main(
			new String[] { "--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", ""});
	}

	
	
}
