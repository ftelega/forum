package ft.projects.forum.controller;

import ft.projects.forum.exception.ForumExceptionResponse;
import ft.projects.forum.model.ForumUserRequest;
import ft.projects.forum.model.ForumUserResponse;
import ft.projects.forum.model.TokenResponse;
import ft.projects.forum.service.ForumUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
@Tag(name = "user", description = "User API")
public class ForumUserController {

    private final ForumUserService userService;

    @Operation(summary = "Register", description = "Register User", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully registered", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Invalid Request Body", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
        }
    )
    @PostMapping(path = "/register")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void register(@RequestBody ForumUserRequest userRequest) {
        userService.register(userRequest);
    }

    @Operation(summary = "Login", description = "Login User", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid Basic Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "BasicAuth")
    @PostMapping(path = "/login")
    @ResponseStatus(value = HttpStatus.OK)
    public TokenResponse login() {
        return userService.login();
    }

    @Operation(summary = "Users", description = "Get Users", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched users", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ForumUserResponse.class))) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() }),
            @ApiResponse(responseCode = "403", description = "Invalid User Authorization (Admin Role Required)", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<ForumUserResponse> getUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "Update", description = "Update Username", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated username", content = {@Content()}),
            @ApiResponse(responseCode = "400", description = "Invalid Username Param", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {@Content()})
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @PutMapping(path = "/username")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateUsername(@RequestParam String username) {
        userService.updateUsername(username);
    }

    @Operation(summary = "Update", description = "Update Password", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated password", content = {@Content()}),
            @ApiResponse(responseCode = "400", description = "Invalid Password Param", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {@Content()})
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @PutMapping(path = "/password")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePassword(@RequestParam String password) {
        userService.updatePassword(password);
    }

    @Operation(summary = "Delete", description = "Delete User", tags = { "user" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user", content = {@Content()}),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {@Content()})
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @DeleteMapping(path = "/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete() {
        userService.delete();
    }
}
