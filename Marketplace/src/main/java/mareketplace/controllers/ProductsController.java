package mareketplace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import mareketplace.models.Product;
import mareketplace.models.ProductDto;
import mareketplace.services.ProductsRepository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository repo;

    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", repo.findAll());
        return "products/products";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute @Validated ProductDto productDto, BindingResult result) {
        // Check if image file is present
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        // If there are validation errors, return to the form
        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        // Handle image file storage
        MultipartFile image = productDto.getImageFile();
        String originalFileName = image.getOriginalFilename();  // Get the original file name

        // Check if the file name is null or empty
        if (originalFileName == null || originalFileName.isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "Image file name is invalid"));
            return "products/CreateProduct";
        }

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            // Ensure the directory exists
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the image file with its original name
            Path filePath = uploadPath.resolve(originalFileName);
            image.transferTo(filePath);  // Directly save the image without modifying the name

        } catch (Exception ex) {
            result.addError(new FieldError("productDto", "imageFile", "Failed to save image. Try again."));
            return "products/CreateProduct";
        }

        // Create and save the Product entity
        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(java.time.LocalDateTime.now());
        product.setImageFileName(originalFileName);  // Use original file name

        repo.save(product);

        return "redirect:/products"; // Redirect to the product list after saving
    }

    // Show Edit Product Page
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            // Find product by ID or throw an exception if not found
            Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
            
            // Populate ProductDto with product details
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            
            // Add productDto to the model for the form
            model.addAttribute("productDto", productDto);
            model.addAttribute("product", product);  // If you need the product as well for any reason
            
        } catch (Exception ex) {
            // Log the exception message
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";  // Redirect to the products list if error occurs
        }
        
        return "products/EditProduct";  // Return to the edit product page
    }
    
    // All about the editing the product
    @PostMapping("/edit")
    public String UpdateProduct(Model model, @RequestParam int id, 
    		@Validated @ModelAttribute ProductDto productDto, BindingResult result) {
    	try {
    		Product product = repo.findById(id).get();
    		model.addAttribute("product", product);
    		
    		if(result.hasErrors()) {
    			return "product/EditProduct";
    		}
    		
    		if (!productDto.getImageFile().isEmpty()) {
    		    // delete old image
    		    String uploadDir = "public/images/";
    		    Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
    		    
    		    try {
    		        Files.delete(oldImagePath);  // Deleting the old image
    		    } catch (Exception ex) {
    		        System.out.println("Exception: " + ex.getMessage());
    		    }

    		    // save new image file
    		    MultipartFile image = productDto.getImageFile();
    		    Date createdAt = new Date(id);
    		    String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
    		    
    		    try (InputStream inputStream = image.getInputStream()) {
    		        // Saving the new image file
    		        Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
    		    } catch (Exception ex) {
    		        System.out.println("Exception: " + ex.getMessage());
    		    }

    		    // Update the product entity with the new image file name
    		    product.setImageFileName(storageFileName);
    		}
    		
    		product.setName(productDto.getName());
    		product.setBrand(productDto.getBrand());
    		product.setCategory(productDto.getCategory());
    		product.setPrice(productDto.getPrice());
    		product.setDescription(productDto.getDescription());

    		repo.save(product);
    		
    	}
    	catch(Exception ex) {
    		System.out.println("Exception: " + ex.getMessage());
    	}
    	return "redirect:/products";
    }
    
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            // Retrieve the product by ID
            Product product = repo.findById(id).orElseThrow(() -> 
                new IllegalArgumentException("Invalid product ID: " + id));

            // Delete the product image
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());
            try {
                if (Files.exists(imagePath)) { // Ensure the file exists before attempting to delete
                    Files.delete(imagePath);
                }
            } catch (Exception ex) {
                System.out.println("Failed to delete image file: " + ex.getMessage());
            }

            // Delete the product from the database
            repo.delete(product);

        } catch (Exception ex) {
            System.out.println("Error deleting product: " + ex.getMessage());
        }

        // Redirect to the product list page
        return "redirect:/products";
    }
    
    
    
    
    @GetMapping("/laptops")
    public String showLaptopsPage(Model model) {
        // Fetch only products that belong to the "Laptops" category
        model.addAttribute("laptops", repo.findByCategory("Laptops"));
        return "products/Laptop";
    }


    @GetMapping("/phones")
    public String showPhones(Model model) {
        model.addAttribute("products", repo.findByCategory("Phones")); // Assuming "Phones" is a category in your database
        return "products/Phone";
    }
    
//    @GetMapping("/edit/{id}")
//    public String showEditPage(@PathVariable int id, Model model) {
//        Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
//        model.addAttribute("product", product);
//        return "products/editProduct";
//    }
}
