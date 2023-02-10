package ylix9g.catalog;

import ylix9g.catalog.entity.Category;
import ylix9g.catalog.entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.text.html.parser.Entity;
import java.util.List;

public class JpaLesson {
    public static void main(String[] args) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        Category category = manager.find(Category.class, (long) 14);
        if (category != null) {
            manager.remove(category);
            manager.persist(category);
            System.out.println(category.getName());
        } else {
            System.out.println("Not Found");
        }
        Product product = manager.find(Product.class, 14L);
        System.out.println(product.getCategory().getName());
        System.out.println(product.getName());
        System.out.println(product.getPrice());


        Category category1 = manager.find(Category.class, 1L);
        List<Product> products = category1.getProducts();
        System.out.println(category.getName() + ":");
        for (Product product1 : products) {
            System.out.printf("- %s (%d)%n", product.getName(), product.getPrice());
        }

        // Principle of Transaction should be implemented in order to transform new categories to the database

//        try {
//            manager.getTransaction().begin();
//
//            Category category2 = new Category();
//            category2.setName("Клавиатуры");
//            manager.persist(category2);
//
//
//            manager.getTransaction().commit();
//        } catch (Exception e) {
//            manager.getTransaction().rollback();
//            e.printStackTrace();
//        }
    }
}
