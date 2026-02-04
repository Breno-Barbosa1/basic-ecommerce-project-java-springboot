package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.*;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.InsufficientStockQuantityException;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.RequiredObjectIsNullException;
import br.com.breno_barbosa1.basic_ecommerce.model.*;
import br.com.breno_barbosa1.basic_ecommerce.repository.CartRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.OrderRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseListObjects;
import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseObject;

@Service
public class CartService {

    Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public CartDTO getCart(String email) {
        logger.info("Retrieving user cart for: {}", email);

        User retrievedUser = userRepository.findByEmail(email);

        if (retrievedUser == null) throw new RequiredObjectIsNullException("User with email " + email + " not found!");

        Cart existingCart = cartRepository.findByUserEmail(email)
            .orElseGet(() -> {
                logger.info("Creating user cart for: {}", email);
                Cart newCart = new Cart();
                newCart.setUser(retrievedUser);
                newCart.setItems(new ArrayList<>());
                return cartRepository.save(newCart);
            });

        var dto = parseObject(existingCart, CartDTO.class);
        dto.setEmail(retrievedUser.getEmail());

        for (int i = 0; i < existingCart.getItems().size(); i++) {
            ProductDTO productDTO = parseObject(existingCart.getItems().get(i).getProduct(), ProductDTO.class);

            Long correctId = existingCart.getItems().get(i).getProduct().getId();
            dto.getItems().get(i).setProductDTO(productDTO);
            dto.getItems().get(i).getProductDTO().setId(correctId);
        }

        return dto;
    }

    @Transactional
    public CartDTO updateCart(String email, CartItemDTO item) {
        logger.info("Updating user cart!");

        User retrievedUser = userRepository.findByEmail(email);

        if (retrievedUser == null) throw new RequiredObjectIsNullException("User with email " + email + " not found!");

        Cart existingCart = cartRepository.findByUserEmail(email)
            .orElseGet(() -> {
                logger.info("Creating user cart for: {}", email);
                Cart newCart = new Cart();
                newCart.setUser(retrievedUser);
                newCart.setItems(new ArrayList<>());
                return cartRepository.save(newCart);
            });

        CartItem existingItem = existingCart.getItems().stream()
            .filter(cartItem -> cartItem.getProduct().getId().equals(item.getProductDTO().getId()))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            Product product = productRepository.findById(item.getProductDTO().getId())
                .orElseThrow(() -> new RequiredObjectIsNullException("Product not found for this id: " + item.getProductDTO().getId()));

            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(item.getQuantity());
            newCartItem.setCart(existingCart);
            existingCart.getItems().add(newCartItem);
        }

        Cart savedCart = cartRepository.save(existingCart);

        var dto = parseObject(savedCart, CartDTO.class);

        dto.setEmail(retrievedUser.getEmail());

        for (int i = 0; i < savedCart.getItems().size(); i++) {
            ProductDTO productDTO = parseObject(savedCart.getItems().get(i).getProduct(), ProductDTO.class);

            Long correctId = savedCart.getItems().get(i).getProduct().getId();
            dto.getItems().get(i).setProductDTO(productDTO);
            dto.getItems().get(i).getProductDTO().setId(correctId);
        }
        return dto;
    }

    @Transactional
    public CartDTO removeItemFromCart(String email, Long cartItemId) {
        logger.info("Removing item from cart!");

        User retrievedUser = userRepository.findByEmail(email);

        if (retrievedUser == null) throw new RequiredObjectIsNullException("User with email " + email + " not found!");

        Cart existingCart = cartRepository.findByUserEmail(email)
            .orElseThrow(() -> new RequiredObjectIsNullException("No cart found for this user!"));

        existingCart.getItems().removeIf(cartItem ->
            Objects.equals(cartItem.getId(), cartItemId));

        var dto = parseObject(cartRepository.save(existingCart), CartDTO.class);

        dto.setEmail(retrievedUser.getEmail());

        for (int i = 0; i < existingCart.getItems().size(); i++) {
            ProductDTO productDTO = parseObject(existingCart.getItems().get(i).getProduct(), ProductDTO.class);

            Long correctId = existingCart.getItems().get(i).getProduct().getId();
            dto.getItems().get(i).setProductDTO(productDTO);
            dto.getItems().get(i).getProductDTO().setId(correctId);
        }

        return dto;
    }

    @Transactional
    public OrderResponseDTO placeOrder(String email, OrderRequestDTO requestDTO) {
        logger.info("Placing order!");

        User retrievedUser = userRepository.findByEmail(email);

        if (retrievedUser == null) throw new RequiredObjectIsNullException("User with email " + email + " not found!");

        Cart existingCart = cartRepository.findByUserEmail(retrievedUser.getEmail())
            .orElseThrow(() -> new RequiredObjectIsNullException(" Cart not found for email" + email));

        List<CartItem> cartItemsList = existingCart.getItems().stream().toList();
        List<OrderItem> orderItemsList = parseListObjects(cartItemsList, OrderItem.class);

        double totalAmount = 0;

        Order order = new Order();

        for (int i = 0; i < cartItemsList.size() ; i++) {
            var actualPrice = cartItemsList.get(i).getProduct().getPrice();

            orderItemsList.get(i).setPriceAtPurchase(actualPrice);
            orderItemsList.get(i).setOrder(order);
            orderItemsList.get(i).setId(null);

            totalAmount += orderItemsList.get(i).getQuantity() * orderItemsList.get(i).getPriceAtPurchase();
        }

        order.setUser(retrievedUser);
        order.setCreatedDate(LocalDateTime.now());
        order.setItems(orderItemsList);
        order.setTotal(totalAmount);

        var dto = parseObject(orderRepository.save(order), OrderResponseDTO.class);
        dto.setUserId(retrievedUser.getId());
        dto.setEmail(retrievedUser.getEmail());

        double totalAmountForItem = 0;

        for (int i = 0; i < dto.getItems().size() ; i++) {
            dto.getItems().get(i).setPriceAtPurchase(orderItemsList.get(i).getPriceAtPurchase());
            dto.getItems().get(i).setProductName(orderItemsList.get(i).getProduct().getName());
            dto.getItems().get(i).setProductId(orderItemsList.get(i).getProduct().getId());

            totalAmountForItem += dto.getItems().get(i).getPriceAtPurchase() * dto.getItems().get(i).getQuantity();

            dto.getItems().get(i).setTotal(totalAmountForItem);

            totalAmountForItem = 0;

            int finalI = i;
            Optional<Product> product = Optional.ofNullable(productRepository.findById(dto.getItems().get(i).getProductId())
                .orElseThrow(() -> new RequiredObjectIsNullException("Product not found for id: " + dto.getItems().get(finalI).getProductId())));

            if (product.get().getStockQuantity() < dto.getItems().get(i).getQuantity()) throw new InsufficientStockQuantityException("Not enough stock quantity for this transaction!");

            product.get().setStockQuantity(product.get().getStockQuantity() - dto.getItems().get(i).getQuantity());

            productRepository.save(product.get());
        }

        existingCart.getItems().clear();
        cartRepository.save(existingCart);


        return dto;
    }

    @Transactional
    public CartDTO clearCart(String email) {

        Cart existingCart = cartRepository.findByUserEmail(email)
            .orElseThrow(() -> new RequiredObjectIsNullException("No records found for this cart!"));

        existingCart.getItems().clear();

        var dto = parseObject(cartRepository.save(existingCart), CartDTO.class);
        dto.setEmail(existingCart.getUser().getEmail());
        return dto;
    }
}