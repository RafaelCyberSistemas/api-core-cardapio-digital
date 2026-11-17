package br.com.cardapio.adapters.in.web;

import br.com.cardapio.adapters.in.web.dto.AuthLoginRequest;
import br.com.cardapio.application.dto.AuthToken;
import br.com.cardapio.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthToken> login(@RequestBody @Valid AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request.email(), request.password()));
    }
}
