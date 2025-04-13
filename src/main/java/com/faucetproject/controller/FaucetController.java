package com.faucetproject.controller;

import com.faucetproject.model.FaucetRequest;
import com.faucetproject.service.FaucetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FaucetController {

    @Autowired
    private FaucetService faucetService;

    @PostMapping("/faucet")
    public ResponseEntity<?> requestTokens(@RequestBody FaucetRequest request) {
        System.out.println("Requête reçue pour l'adresse : " + request.getAddress()); // Log pour débogage
        try {
            String txHash = faucetService.sendTokens(request.getAddress());
            return ResponseEntity.ok("Tokens envoyés avec succès. Transaction hash : " + txHash);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors de l'envoi des tokens : " + ex.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Application fonctionne !";
    }

    
}
