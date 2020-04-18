package jpa;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import jpa.entity.Customer;
import jpa.repository.CustomerRepository;

public class Test
{
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"/application.xml");
		// context.
		CustomerRepository repository = context
				.getBean(CustomerRepository.class);
				
		// repository.save(new Customer("欧阳", "国栋"));
		List<Customer> ls = repository.findByFirstName("欧阳");
		for (Customer customer : ls)
		{
			System.out.println(customer);
		}
		System.exit(0);
	}
}
