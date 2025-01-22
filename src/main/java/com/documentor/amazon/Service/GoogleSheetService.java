package com.documentor.amazon.Service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class GoogleSheetService {

    @Value("${sheets.id}")
    private String spreadsheetId;

    private Sheets sheetsService;

    public void writeOrdersToSheet(List<Map<String, Object>> orders) throws IOException {
        List<List<Object>> data = new ArrayList<>();

        // Add header row
        data.add(Arrays.asList(
                "Amazon Order ID", "Merchant Order ID", "Purchase Date", "Last Updated Date",
                "Order Status", "Fulfillment Channel", "Sales Channel", "Order Channel", "URL",
                "Ship Service Level", "Product Name", "SKU", "ASIN", "Item Status", "Quantity",
                "Currency", "Item Price", "Item Tax", "Shipping Price", "Shipping Tax",
                "Gift Wrap Price", "Gift Wrap Tax", "Item Promotion Discount", "Ship Promotion Discount",
                "Ship City", "Ship State", "Ship Postal Code", "Ship Country", "Promotion IDs",
                "Is Business Order", "Purchase Order Number", "Price Designation", "Fulfilled By", "Is IBA"
        ));

        // Add order data
        for (Map<String, Object> order : orders) {
            data.add(Arrays.asList(
                    order.get("amazonOrderId"), order.get("merchantOrderId"), order.get("purchaseDate"),
                    order.get("lastUpdatedDate"), order.get("orderStatus"), order.get("fulfillmentChannel"),
                    order.get("salesChannel"), order.get("orderChannel"), order.get("url"),
                    order.get("shipServiceLevel"), order.get("productName"), order.get("sku"),
                    order.get("asin"), order.get("itemStatus"), order.get("quantity"),
                    order.get("currency"), order.get("itemPrice"), order.get("itemTax"),
                    order.get("shippingPrice"), order.get("shippingTax"), order.get("giftWrapPrice"),
                    order.get("giftWrapTax"), order.get("itemPromotionDiscount"),
                    order.get("shipPromotionDiscount"), order.get("shipCity"),
                    order.get("shipState"), order.get("shipPostalCode"), order.get("shipCountry"),
                    order.get("promotionIds"), order.get("isBusinessOrder"),
                    order.get("purchaseOrderNumber"), order.get("priceDesignation"),
                    order.get("fulfilledBy"), order.get("isIBA")
            ));
        }

        // Create ValueRange object
        ValueRange body = new ValueRange().setValues(data);

        // Update the sheet with the data
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, "Sheet1!A1", body)
                .setValueInputOption("RAW")
                .execute();
    }
}