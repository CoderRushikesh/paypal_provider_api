package com.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class paymentController {
	
	private Logger logger = LoggerFactory.getLogger(paymentController.class);
	
    @PostMapping("/payment")
    public int add(@RequestParam int num1, @RequestParam int num2) {
    	// just demo test 
        logger.info("num1:{}|num2:{}", num1, num2);
        
        int sumResult = num1 + num2;
        logger.info("sumResult:{}", sumResult);

        return sumResult;
   
        
    }
}
