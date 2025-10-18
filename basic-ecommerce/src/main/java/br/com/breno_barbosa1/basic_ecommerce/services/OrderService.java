package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderItemRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderItemResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.RequiredObjectIsNullException;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.ResourceNotFoundException;
import br.com.breno_barbosa1.basic_ecommerce.model.Order;
import br.com.breno_barbosa1.basic_ecommerce.model.OrderItem;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
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
import java.util.stream.Collectors;

import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseListObjects;
import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseObject;

@Service
public class OrderService {

    Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    public List<OrderResponseDTO> findAll() {

        logger.info("Finding all user orders!");

        List<Order> orders = orderRepository.findAllOrdersWithDetails();

        // entity to dto
        return orders.stream()
            .map(this::orderToResponseDTO)
            .collect(Collectors.toList());
    }

    public OrderResponseDTO findById(Long id) {

        logger.info("Find user order by id!");

        return parseObject(orderRepository.findById(id), OrderResponseDTO.class);
    }

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO orderRequestDto) throws Exception {

        if (orderRequestDto == null) throw new RequiredObjectIsNullException();

        User user = userRepository.findById(orderRequestDto.getUserId())
            .orElseThrow(() -> new RequiredObjectIsNullException("No records found for this ID!"));

        logger.info("Creating an user order!");


        // empty lists
        List<OrderItem> createdOrderItemList = new ArrayList<>();
        List<Product> updatedProducts = new ArrayList<>();
        List<OrderItemResponseDTO> itemResponseDtoList = new ArrayList<>();

        Order order = new Order();

        double total = orderCalculateTotal(orderRequestDto.getItems(), updatedProducts, createdOrderItemList, order);

        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setItems(createdOrderItemList);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        productRepository.saveAll(updatedProducts);

        for (OrderItem entity : savedOrder.getItems()) {

            var itemResponseDto = new OrderItemResponseDTO();

            itemResponseDto.setId(entity.getId());
            itemResponseDto.setProductId(entity.getProduct().getId());
            itemResponseDto.setProductName(entity.getProduct().getName());
            itemResponseDto.setQuantity(entity.getQuantity());
            itemResponseDto.setPriceAtPurchase(entity.getPriceAtPurchase());
            itemResponseDto.setTotal(entity.getPriceAtPurchase() * entity.getQuantity());

            itemResponseDtoList.add(itemResponseDto);
        }

        var dto = new OrderResponseDTO();

        dto.setId(savedOrder.getId());
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setItems(itemResponseDtoList);
        dto.setCreatedDate(order.getCreatedDate());
        dto.setTotal(savedOrder.getTotal());
        dto.setItems(itemResponseDtoList);

        return dto;
    }

    public void delete(Long id) {

        logger.info("Deleting one user order!");

        Order entity = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        orderRepository.delete(entity);
    }

    // calculates total amount and creates order items
    public Double orderCalculateTotal(List<OrderItemRequestDTO> items, List<Product> updatedProducts,List<OrderItem> createdOrderItems, Order order) throws Exception {

        if (items.isEmpty()) throw new RequiredObjectIsNullException();

        double total = 0;

        for (OrderItemRequestDTO item : items) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

            if (item.getQuantity() > product.getStockQuantity()) {
                throw new Exception("Insufficient stock quantity for this product!");
            } else {
                OrderItem orderItem = new OrderItem();

                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPriceAtPurchase(product.getPrice());

                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                total += product.getPrice() * item.getQuantity();
                updatedProducts.add(product);
                createdOrderItems.add(orderItem);
            }
        }

        return total;
    }

    // Order to orderResponseDto
    private OrderResponseDTO orderToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setId(order.getId());
        dto.setCreatedDate(order.getCreatedDate());
        dto.setTotal(order.getTotal());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setEmail(order.getUser().getEmail());
        }

        dto.setItems(orderItemsToDTO(order.getItems()));

        return dto;
    }

    // OrderItemList to OrderItemResponseDTOList
    private List<OrderItemResponseDTO> orderItemsToDTO(List<OrderItem> items) {
        List<OrderItemResponseDTO> dtoList = new ArrayList<>();

        for (OrderItem entity : items) {
            OrderItemResponseDTO itemDto = new OrderItemResponseDTO();

            itemDto.setId(entity.getId());
            itemDto.setQuantity(entity.getQuantity());
            itemDto.setPriceAtPurchase(entity.getPriceAtPurchase());

            if (entity.getProduct() != null) {
                itemDto.setProductId(entity.getProduct().getId());
                itemDto.setProductName(entity.getProduct().getName());
            }

            itemDto.setTotal(entity.getPriceAtPurchase() * entity.getQuantity());

            dtoList.add(itemDto);
        }
        return dtoList;
    }
}