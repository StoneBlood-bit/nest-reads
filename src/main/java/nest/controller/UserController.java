package nest.controller;

import lombok.RequiredArgsConstructor;
import nest.dto.user.UserInfoResponseDto;
import nest.model.User;
import nest.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoResponseDto getMyProfile(@AuthenticationPrincipal User user) {
        return userService.getById(user.getId());
    }
}
