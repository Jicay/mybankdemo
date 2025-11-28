package com.example.mybank.infrastructure.driving.web;

import com.example.mybank.domain.usecase.client.ListClients;
import com.example.mybank.infrastructure.driving.rest.dto.ClientDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
    private final ListClients listClients;

    public PageController(ListClients listClients) {
        this.listClients = listClients;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("clients", listClients.all().stream().map(ClientDTO::fromDomain).toList());
        return "index"; // src/main/resources/templates/index.html
    }

    @GetMapping("/clients/{clientId}/accounts")
    public String clientAccounts(@PathVariable String clientId) {
        return "accounts"; // src/main/resources/templates/accounts.html
    }

    @GetMapping("/accounts")
    public String clientAccounts() {
        return "accounts"; // src/main/resources/templates/accounts.html
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // Explicit routes to render custom error pages when JS redirects on HTTP failures
    @GetMapping("/error/403")
    public String error403() {
        return "error/403";
    }

    @GetMapping("/error/404")
    public String error404() {
        return "error/404";
    }

    @GetMapping("/error/500")
    public String error500() {
        return "error/500";
    }

    @GetMapping("/error/generic")
    public String errorGeneric() {
        return "error/error";
    }
}
