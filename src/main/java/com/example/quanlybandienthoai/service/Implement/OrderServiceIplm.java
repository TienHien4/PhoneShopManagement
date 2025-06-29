package com.example.quanlybandienthoai.service.Implement;

import com.example.quanlybandienthoai.dto.Request.OrderItemRequest;
import com.example.quanlybandienthoai.dto.Request.OrderRequest;
import com.example.quanlybandienthoai.dto.Response.OrderItemResponse;
import com.example.quanlybandienthoai.dto.Response.OrderResponse;
import com.example.quanlybandienthoai.entity.*;
import com.example.quanlybandienthoai.enums.DefinitionCode;
import com.example.quanlybandienthoai.exception.AppException;
import com.example.quanlybandienthoai.repository.*;
import com.example.quanlybandienthoai.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai các chức năng xử lý đơn hàng.
 * Bao gồm đặt hàng mới, lấy danh sách đơn hàng và xóa đơn hàng.
 * 
 * @author Nguyễn Tiến Hiền
 * @since 25/06/2025
 */
@Service
public class OrderServiceIplm implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    /**
     * Xử lý đặt đơn hàng mới từ người dùng.
     * Kiểm tra hợp lệ người dùng, sản phẩm, tính tổng giá và số lượng,
     * tạo mới đơn hàng và xóa giỏ hàng sau khi đặt.
     * 
     * @param request Dữ liệu đơn hàng từ client
     * @return Thông tin đơn hàng đã tạo
     */
    @Override
    @Transactional
    @CacheEvict(value = { "orderList" }, allEntries = true)
    public OrderResponse placeOrder(OrderRequest request) {
        // Lấy thông tin người dùng
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(
                        DefinitionCode.NOT_FOUND,
                        "Không tìm thấy sản phẩm",
                        "User not found with id: " + request.getUserId()));

        // Tạo mới đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setOrder_date(LocalDateTime.now());

        List<OrderItem> orderItemList = new ArrayList<>();
        double totalPrice = 0;
        int totalQuantity = 0;

        // Xử lý từng sản phẩm trong đơn hàng
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProduct_id())
                    .orElseThrow(() -> new AppException(
                            DefinitionCode.NOT_FOUND,
                            "Không tìm thấy sản phẩm",
                            "Product not found with id: " + itemReq.getProduct_id()));
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);

            orderItemList.add(orderItem);
            totalQuantity += itemReq.getQuantity();
            totalPrice += itemReq.getQuantity() * product.getPrice();
        }

        order.setTotal_quantity(totalQuantity);
        order.setTotal_price(totalPrice);
        order.setOrderItems(orderItemList);

        // Lưu đơn hàng
        orderRepository.save(order);

        // Xóa giỏ hàng của người dùng sau khi đặt hàng
        ShoppingCart shoppingCart = user.getShopping_cart();
        if (shoppingCart != null) {
            List<CartItem> items = shoppingCart.getListCartItems();
            if (items != null) {
                items.clear();
            }
            shoppingCart.setTotal_price(0);
            shoppingCart.setTotal_product(0);
            shoppingCartRepository.save(shoppingCart);
        }

        // Tạo đối tượng phản hồi
        OrderResponse response = new OrderResponse();
        response.setOrder_id(order.getOrder_id());
        response.setUser_id(user.getUserId());
        response.setOrder_date(order.getOrder_date());
        response.setTotal_quantity(totalQuantity);
        response.setTotal_price(totalPrice);

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : orderItemList) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProduct_id(item.getProduct().getProduct_id());
            itemResponse.setProduct_name(item.getProduct().getProduct_name());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPrice(item.getPrice());
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);
        return response;
    }

    /**
     * Lấy tất cả đơn hàng trong hệ thống.
     *
     * @return Danh sách đơn hàng (OrderResponse)
     */
    @Override
    @Cacheable(value = "orderList")
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrder_id(order.getOrder_id());
            orderResponse.setUser_id(order.getUser().getUserId());
            orderResponse.setOrder_date(order.getOrder_date());
            orderResponse.setTotal_quantity(order.getTotal_quantity());
            orderResponse.setTotal_price(order.getTotal_price());

            List<OrderItemResponse> itemResponses = order.getOrderItems().stream().map(item -> {
                OrderItemResponse itemResponse = new OrderItemResponse();
                itemResponse.setProduct_id(item.getProduct().getProduct_id());
                itemResponse.setProduct_name(item.getProduct().getProduct_name());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setPrice(item.getPrice());
                return itemResponse;
            }).toList();

            orderResponse.setItems(itemResponses);
            return orderResponse;
        }).toList();
    }

    /**
     * Xóa đơn hàng theo ID.
     *
     * @param orderId ID của đơn hàng cần xóa
     * @throws AppException nếu không tìm thấy đơn hàng
     */
    @Override
    @CacheEvict(value = { "orderList" }, allEntries = true)
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new AppException(
                    DefinitionCode.NOT_FOUND,
                    "Không tìm thấy đơn hàng để xóa",
                    "Order not found");
        }
        orderRepository.deleteById(orderId);
    }
}
