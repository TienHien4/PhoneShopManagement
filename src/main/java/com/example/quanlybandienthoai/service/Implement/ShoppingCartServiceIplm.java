package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Request.ShoppingCartRequest;
import com.example.quanlybandienthoai.dto.Response.CartItemResponse;
import com.example.quanlybandienthoai.entity.CartItem;
import com.example.quanlybandienthoai.entity.Product;
import com.example.quanlybandienthoai.entity.ShoppingCart;
import com.example.quanlybandienthoai.entity.User;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.CartItemRepository;
import com.example.quanlybandienthoai.repository.ProductRepository;
import com.example.quanlybandienthoai.repository.ShoppingCartRepository;
import com.example.quanlybandienthoai.repository.UserRepository;
import com.example.quanlybandienthoai.service.RedisService;
import com.example.quanlybandienthoai.service.ShoppingCartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation xử lý các thao tác với giỏ hàng của người dùng
 *
 * @author Nguyễn Tiến Hiền
 * @since 23/06/2025
 */
@Service
public class ShoppingCartServiceIplm implements ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private RedisService redisService;

    private static final Logger logger = LogManager.getLogger(ShoppingCartServiceIplm.class);

    /**
     * Thêm sản phẩm vào giỏ hàng của người dùng
     */
    @Override
    public void addItem(ShoppingCartRequest request, long userId, long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Không tìm thấy khách hàng với ID: {}", userId);
                    return new AppException(
                            DefinitionCode.NOT_FOUND,
                            "Không tìm thấy khách hàng",
                            "No customer with id: " + userId);
                });

        Product p = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Không tìm thấy sản phẩm với id: {}", productId);
                    return new AppException(
                            DefinitionCode.NOT_FOUND,
                            "Không tìm thấy sản phẩm",
                            "Product not found with id: " + productId);
                });

        ShoppingCart shoppingCart = user.getShopping_cart();
        if (shoppingCart == null || !shoppingCartRepository.existsById(shoppingCart.getShoppingCartId())) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            shoppingCart.setListCartItems(new ArrayList<>());
            shoppingCartRepository.save(shoppingCart);
        }

        List<CartItem> listOrders = shoppingCart.getListCartItems();
        CartItem existingOrder = findOrders(listOrders, p);

        if (existingOrder == null) {
            CartItem newOrder = new CartItem();
            newOrder.setProduct(p);
            newOrder.setShoppingCart(shoppingCart);
            newOrder.setTotal_amount(request.getQuantity());
            newOrder.setTotal_price(p.getPrice() * request.getQuantity());
            cartItemRepository.save(newOrder);
            listOrders.add(newOrder);
        } else {
            existingOrder.setTotal_amount(existingOrder.getTotal_amount() + request.getQuantity());
            existingOrder.setTotal_price(existingOrder.getProduct().getPrice() * existingOrder.getTotal_amount());
            cartItemRepository.save(existingOrder);
        }

        shoppingCart.setTotal_price(totalPrice(listOrders));
        shoppingCart.setTotal_product(totalAmount(listOrders));
        shoppingCartRepository.save(shoppingCart);
        // Xóa cache giỏ hàng user này
        redisService.delete("cart:" + userId);
    }

    /**
     * Xóa sản phẩm ra khỏi giỏ hàng của người dùng
     */
    @Override
    public void deleteItem(long userId, long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Không tìm thấy người dùng với mã: {}", userId);
                    return new AppException(
                            DefinitionCode.NOT_FOUND,
                            "Không tìm thấy người dùng",
                            "User not found with id: " + userId);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(
                        DefinitionCode.NOT_FOUND,
                        "Không tìm thấy sản phẩm",
                        "Product not found with id: " + productId));

        ShoppingCart shoppingCart = user.getShopping_cart();
        var listOrders = shoppingCart.getListCartItems();
        var order = findOrders(listOrders, product);
        listOrders.remove(order);

        cartItemRepository.delete(order);
        shoppingCart.setTotal_product(totalAmount(listOrders));
        shoppingCart.setTotal_price(totalPrice(listOrders));
        shoppingCartRepository.save(shoppingCart);
        // Xóa cache giỏ hàng user này
        redisService.delete("cart:" + userId);
    }

    /**
     * Lấy tất cả sản phẩm có trong giỏ hàng
     */
    @Override
    public List<CartItemResponse> getAllItem(long userId) {
        // Kiểm tra cache trước
        String key = "cart:" + userId;
        Object cached = redisService.getValue(key);
        if (cached != null) {
            logger.info("Get cart from Redis cache for userId: {}", userId);
            return (List<CartItemResponse>) cached;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Không tìm thấy người dùng với mã: {}", userId);
                    return new AppException(
                            DefinitionCode.NOT_FOUND,
                            "Không tìm thấy người dùng",
                            "User not found with id: " + userId);
                });
        ShoppingCart shoppingCart = user.getShopping_cart();
        var listOrders = shoppingCart.getListCartItems();
        var response = listOrders.stream().map(
                cartItem -> new CartItemResponse(
                        cartItem.getOrder_id(),
                        cartItem.getTotal_amount(),
                        cartItem.getTotal_price(),
                        cartItem.getProduct()))
                .toList();
        // Lưu vào cache
        redisService.setValue(key, response);
        return response;
    }

    /**
     * Giảm số lượng sản phẩm trong giỏ hàng
     */
    @Override
    public void reduceItem(long userId, long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(
                        DefinitionCode.NOT_FOUND,
                        "Không tìm thấy người dùng",
                        "User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(
                        DefinitionCode.NOT_FOUND,
                        "Không tìm thấy sản phẩm",
                        "Product not found with id: " + productId));

        ShoppingCart shoppingCart = user.getShopping_cart();
        var listOrders = shoppingCart.getListCartItems();
        var order = findOrders(listOrders, product);
        order.setTotal_amount(order.getTotal_amount() - 1);
        order.setTotal_price(order.getTotal_price() - product.getPrice());
        cartItemRepository.save(order);

        shoppingCart.setTotal_product(totalAmount(listOrders));
        shoppingCart.setTotal_price(totalPrice(listOrders));
        shoppingCartRepository.save(shoppingCart);
        // Xóa cache giỏ hàng user này
        redisService.delete("cart:" + userId);
    }

    /**
     * Tìm đơn hàng theo sản phẩm trong giỏ
     */
    public CartItem findOrders(List<CartItem> listOrders, Product p) {
        if (listOrders == null) {
            return null;
        }
        for (CartItem o : listOrders) {
            if (o.getProduct().getProduct_id() == p.getProduct_id()) {
                return o;
            }
        }
        return null;
    }

    /**
     * Tính tổng số lượng sản phẩm trong giỏ hàng
     */
    public int totalAmount(List<CartItem> listOrders) {
        return listOrders.stream().map(CartItem::getTotal_amount)
                .reduce(0, Integer::sum);
    }

    /**
     * Tính tổng giá trị giỏ hàng
     */
    public double totalPrice(List<CartItem> listOrders) {
        return listOrders.stream().map(CartItem::getTotal_price)
                .reduce(0.0, Double::sum);
    }
}