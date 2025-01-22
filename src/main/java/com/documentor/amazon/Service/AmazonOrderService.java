package com.documentor.amazon.Service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
@Service
public class AmazonOrderService {

    @Value("${access.key}")
    private String accessToken;
    @Value("${order.ep}")
    private String ordersEndpoint;
    @Value("${order.items.ep}")
    private String orderItemsEndpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(cron = "* * * * *")
    public List<Map<String, Object>> fetchOrders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                ordersEndpoint,
                HttpMethod.GET,
                request,
                JsonNode.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode orders = Objects.requireNonNull(response.getBody()).path("orders");
            List<Map<String, Object>> orderList = new ArrayList<>();

            orders.forEach(order -> {
                String orderId = order.path("AmazonOrderId").asText();
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("amazonOrderId", orderId);
                orderMap.put("merchantOrderId", order.path("SellerOrderId").asText());
                orderMap.put("purchaseDate", order.path("PurchaseDate").asText());
                orderMap.put("lastUpdatedDate", order.path("LastUpdateDate").asText());
                orderMap.put("orderStatus", order.path("OrderStatus").asText());
                orderMap.put("fulfillmentChannel", order.path("FulfillmentChannel").asText());
                orderMap.put("salesChannel", order.path("SalesChannel").asText());
                orderMap.put("orderChannel", order.path("OrderChannel").asText());
                orderMap.put("url", ""); // URL might not be directly available
                orderMap.put("shipServiceLevel", order.path("ShipServiceLevel").asText());
                orderMap.put("shipCity", order.path("ShippingAddress").path("City").asText());
                orderMap.put("shipState", order.path("ShippingAddress").path("StateOrRegion").asText());
                orderMap.put("shipPostalCode", order.path("ShippingAddress").path("PostalCode").asText());
                orderMap.put("shipCountry", order.path("ShippingAddress").path("CountryCode").asText());
                orderMap.put("isBusinessOrder", order.path("IsBusinessOrder").asText());
                orderMap.put("purchaseOrderNumber", order.path("PurchaseOrderNumber").asText());
                orderMap.put("priceDesignation", order.path("PriceDesignation").asText());
                orderMap.put("fulfilledBy", order.path("FulfillmentChannel").asText());
                orderMap.put("isIBA", order.path("IsIBA").asText());

                // Fetch order items for remaining fields
                enrichWithOrderItems(orderId, orderMap);

                orderList.add(orderMap);
            });
            return orderList;
        }
        throw new RuntimeException("Failed to fetch orders from Amazon API");
    }

    private void enrichWithOrderItems(String orderId, Map<String, Object> orderMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                orderItemsEndpoint.replace("{orderId}", orderId),
                HttpMethod.GET,
                request,
                JsonNode.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode orderItems = Objects.requireNonNull(response.getBody()).path("orderItems");
            if (orderItems.isArray() && !orderItems.isEmpty()) {
                // Assuming only one item for simplicity; handle multiple items as needed
                JsonNode item = orderItems.get(0);
                orderMap.put("productName", item.path("Title").asText());
                orderMap.put("sku", item.path("SellerSKU").asText());
                orderMap.put("asin", item.path("ASIN").asText());
                orderMap.put("itemStatus", item.path("OrderItemStatus").asText());
                orderMap.put("quantity", item.path("QuantityOrdered").asText());
                orderMap.put("currency", item.path("ItemPrice").path("CurrencyCode").asText());
                orderMap.put("itemPrice", item.path("ItemPrice").path("Amount").asText());
                orderMap.put("itemTax", item.path("ItemTax").path("Amount").asText());
                orderMap.put("shippingPrice", item.path("ShippingPrice").path("Amount").asText());
                orderMap.put("shippingTax", item.path("ShippingTax").path("Amount").asText());
                orderMap.put("giftWrapPrice", item.path("GiftWrapPrice").path("Amount").asText());
                orderMap.put("giftWrapTax", item.path("GiftWrapTax").path("Amount").asText());
                orderMap.put("itemPromotionDiscount", item.path("PromotionDiscount").path("Amount").asText());
                orderMap.put("shipPromotionDiscount", item.path("ShipPromotionDiscount").path("Amount").asText());
                orderMap.put("promotionIds", item.path("PromotionIds").toString());
            }
        }
    }
}
