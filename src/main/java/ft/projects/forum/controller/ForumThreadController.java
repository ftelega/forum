package ft.projects.forum.controller;

import ft.projects.forum.exception.ForumExceptionResponse;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumThreadResponse;
import ft.projects.forum.service.ForumThreadService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(path = "/api/threads")
@RequiredArgsConstructor
@Tag(name = "thread", description = "Thread API")
public class ForumThreadController {

    private final ForumThreadService threadService;

    @Operation(summary = "Create", description = "Create Thread", tags = { "thread" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully created", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Invalid Request Body", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @PostMapping(path = "/create")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createThread(@RequestBody ForumThreadRequest threadRequest) {
        threadService.createThread(threadRequest);
    }

    @Operation(summary = "Get", description = "Get Threads", tags = { "thread" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched threads", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Invalid Request Param", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<ForumThreadResponse> getThreads(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "true") boolean descending) {
        return threadService.getThreads(page, size, descending);
    }
}
