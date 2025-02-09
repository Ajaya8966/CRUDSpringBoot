package mareketplace.services;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mareketplace.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

	List<Product> findByCategory(String category);

}
