package ylix9g.catalog;

import ylix9g.catalog.entity.Category;
import ylix9g.catalog.entity.Characteristics;
import ylix9g.catalog.entity.CharacteristicsMeaning;
import ylix9g.catalog.entity.Product;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Application {

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("main");

    private static final BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws IOException {
        // Create a new category - [1]
        // - Create a new product - [2]
        // - Edit category - [3]
        // - Delete category - [4]
        // Choose action : _________
        while (true) {
            System.out.println("Choose an action you want to perform:");
            System.out.println("[1] --> Create a category");
            System.out.println("[2] --> Create a product");
            System.out.println("[3] --> Update a product");
            System.out.println("[4] --> Delete a product");
            System.out.println("[5] --> Cancel operation");
            System.out.println("Choose an action you want to perform: ");

            int choice = Integer.parseInt(IN.readLine());
            boolean Break = false;
            switch (choice) {
                case 1:
                    create();
                    break;
                case 2:
                    createProduct();
                    break;
                case 3:
                    update();
                    break;
                case 4:
                    delete();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
    public static void create() throws IOException {
        EntityManager manager = FACTORY.createEntityManager();
        System.out.print("Enter the name of the new category : ");
        String newCategory = IN.readLine();
        System.out.print("List the characteristics for the following category : ");
        String newChar = IN.readLine();
        String[] words = newChar.split(",");
        try {
            manager.getTransaction().begin();
            Category category = new Category();
            category.setName(newCategory);
            manager.persist(category);
            for (String word : words) {
                Characteristics characteristic = new Characteristics();
                characteristic.setCategory(category);
                characteristic.setName(word);
                manager.persist(characteristic);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            manager.close();
        }
    }
    public static void createProduct() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            List<Category> categories = manager.createQuery("select c from Category c", Category.class)
                    .getResultList();
            for (int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                String name = category.getName();
                System.out.println(name + " [" + (i+1) + "]");
            }
            System.out.println("Choose the category of the product : ");
            String categoryList = IN.readLine();
            // Category selectedCategory = manager.find(Category.class, Long.parseLong(categoryList));
            Category selectedCategory = categories.get(Integer.parseInt(categoryList) - 1);

            System.out.println("Create the name of the product: ");
            String productName = IN.readLine();
            Product product = new Product();
            product.setName(productName);
            product.setCategory(selectedCategory);

            System.out.println("Enter the price of the product: ");
            String productPrice = IN.readLine();
            product.setPrice(Integer.parseInt(productPrice));
            manager.persist(product);

            List<Characteristics> characteristicsList = manager.createQuery("select c from Characteristics c",
                    Characteristics.class).getResultList();
            for (Characteristics characteristics : characteristicsList) {
                String characteristicsName = characteristics.getName();
                System.out.print(characteristicsName + " : ");
                String NameCharacteristicsMeaning = IN.readLine();

                CharacteristicsMeaning characteristicsMeaning = new CharacteristicsMeaning();
                characteristicsMeaning.setCharacteristics(characteristics);
                characteristicsMeaning.setName(NameCharacteristicsMeaning);
                characteristicsMeaning.setProduct(product);
                product.setCategory(selectedCategory);
                manager.persist(characteristicsMeaning);
                // product.getCategory
                // enter new everything
                // setters
            }
            IN.close();
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            manager.close();
        }
    }
    public static void update() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
        manager.getTransaction().begin();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Choose the product by its ID: ");
        long updateProduct = Long.parseLong(in.readLine());
        Product product = manager.find(Product.class, updateProduct);
        System.out.print("Enter the new name for the product: ");
        String newName = in.readLine();
        product.setName(newName);
        if (!newName.isEmpty()) {
            product.setName(newName);
        }
        boolean check = false;
        while (!check) {
            System.out.println("Enter the price of the product: ");
            String priceInput = in.readLine();
            if (priceInput != null && !priceInput.trim().isEmpty()) {
                if (priceInput.matches("\\d+")) {
                    int productPrice = Integer.parseInt(priceInput);
                    product.setPrice(productPrice);
                    check = true;
                }
                else {
                    System.out.println("Enter the price of the product: ");
                }
            } else {
                check = true;
            }
        }
        TypedQuery<Characteristics> characteristicsTypedQuery = manager.createQuery(
                "select c from Characteristics c where c.category = ?1", Characteristics.class
        );characteristicsTypedQuery.setParameter(1, product.getCategory());
        List<Characteristics> characteristics = characteristicsTypedQuery.getResultList();
        for(Characteristics characteristic: characteristics ){
            System.out.println("Enter the new --> " + characteristic.getName() + ": ");
            String newCharMean = in.readLine();
            TypedQuery<CharacteristicsMeaning> characteristicsMeaningTypedQuery = manager.createQuery(
                    "select c from CharacteristicsMeaning c where c.product.id = ?1 and c.characteristics.id = ?2",
                    CharacteristicsMeaning.class
            );characteristicsMeaningTypedQuery.setParameter(1, product.getId());
            characteristicsMeaningTypedQuery.setParameter(2, characteristic.getId());
            characteristicsMeaningTypedQuery.getSingleResult().setName(newCharMean);
        }
        manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    private static void delete() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            System.out.println("Enter the ID of the Product to Delete from the Database : ");
            String productIdString = IN.readLine();
            long productId = Long.parseLong(productIdString);
            Query query = manager.createQuery("delete from Product p where p.id = ?1");
            query.setParameter(1, productId);
            Query query2 = manager.createQuery("delete from CharacteristicsMeaning c where c.product.id = ?1");
            query2.setParameter(1, productId);
            System.out.println("Product was deleted successfully !");
            query2.executeUpdate();
            query.executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}