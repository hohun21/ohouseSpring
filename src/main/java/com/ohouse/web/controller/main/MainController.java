package com.ohouse.web.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
	
	@GetMapping(value = "/main.htm")
	public String main() {
		
		log.info("🤩 MainController.main()...");
				
				 return "/main/main";
		
	}
	
}
