package com.documentor.amazon.Service;

import com.documentor.amazon.Entity.History;
import com.documentor.amazon.Repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Map;
import java.util.List;

public class TaskerService {
    @Autowired
    private AmazonOrderService amazonOrderService;
    @Autowired
    private GoogleSheetService googleSheetService;
    @Autowired
    private HistoryRepository historyRepository;

    @Scheduled(cron="0 * * * *")
    public String addToSheet() {
        try {
            List<Map<String,Object>> history=amazonOrderService.fetchOrders();
            History history1=new History();
            history1.setData(history);
            historyRepository.save(history1);
            googleSheetService.writeOrdersToSheet(history);
            return "DONE";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
