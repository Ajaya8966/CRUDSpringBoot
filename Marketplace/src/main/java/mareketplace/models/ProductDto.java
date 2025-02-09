package mareketplace.models;


import org.springframework.web.multipart.MultipartFile;

public class ProductDto {

//    @NotEmpty(message = "The name is required")
    private String name;

//    @NotEmpty(message = "The brand name is required")
    private String brand;

//    @NotEmpty(message = "The category name is required")
    private String category;

//    @Min(0)
    private String price;

//    @Size(min=10, max=100, message = "The description should be between 10 and 100 characters")
    private String description;

    private MultipartFile imageFile;

	private int id;

    // Getters and Setters
    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Other getters and setters
    public int getId() {
        return id;
    }
}
