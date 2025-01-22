package com.documentor.amazon.Controller;

import com.documentor.amazon.Service.AmazonOrderService;
import com.documentor.amazon.Service.TaskerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v0")
@RestController
public class UserController {
    @Autowired
    private TaskerService taskerService;
    @Autowired
    private AmazonOrderService amazonOrderService;

    @GetMapping("/add")
    protected ResponseEntity<?> addNow(){
        try {
            return ResponseEntity.ok(taskerService.addToSheet());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/show")
    protected ResponseEntity<?> showOrders(){
        try {
            return ResponseEntity.ok(amazonOrderService.fetchOrders());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

}
