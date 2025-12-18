package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();

        String sql = "SELECT sc.user_id, sc.product_id, sc.quantity, " +
                "       p.name, p.price, p.category_id, p.description, " +
                "       p.subcategory, p.stock, p.featured, p.image_url " +
                "FROM shopping_cart sc " +
                "JOIN products p ON sc.product_id = p.product_id " +
                "WHERE sc.user_id = ?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    Product product = new Product();
                    product.setProductId(row.getInt("product_id"));
                    product.setName(row.getString("name"));
                    product.setPrice(row.getBigDecimal("price"));
                    product.setCategoryId(row.getInt("category_id"));
                    product.setDescription(row.getString("description"));
                    product.setSubCategory(row.getString("subcategory"));
                    product.setStock(row.getInt("stock"));
                    product.setFeatured(row.getBoolean("featured"));
                    product.setImageUrl(row.getString("image_url"));

                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(row.getInt("quantity"));

                    cart.add(item);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return cart;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart addItem(int userId, int productId) {

        String checkSql = "INSERT INTO shopping_cart(user_id, product_id, quantity) values (?,?,?) ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(checkSql)) {
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, 1);
            statement.setInt(4, 1);

            statement.executeUpdate();

            return getByUserId(userId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateItem(int userId, int productId, int quantity) {

        {
            String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, quantity);
                statement.setInt(2, userId);
                statement.setInt(3, productId);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void clearCart(int userId) {

        {
            String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void deleteItem(int userId, int productId) {


        String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

