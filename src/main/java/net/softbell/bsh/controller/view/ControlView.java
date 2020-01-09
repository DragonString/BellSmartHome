package net.softbell.bsh.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author : Bell(bell@softbell.net)
 * @Description : 컨트롤 페이지 뷰 컨트롤러
 */
@Controller
@RequestMapping("/control")
public class ControlView {
	// Global Field
	private final String G_BASE_PATH = "services/control";
	
	@GetMapping()
    public String dispIndex() {
        return G_BASE_PATH + "/index";
    }
}