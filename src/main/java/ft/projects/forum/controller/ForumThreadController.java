package ft.projects.forum.controller;

import ft.projects.forum.exception.ForumExceptionResponse;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumThreadResponse;
import ft.projects.forum.service.ForumThreadService;
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
import java.util.UUID;

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
            @ApiResponse(responseCode = "200", description = "Successfully fetched threads", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ForumThreadResponse.class))) }),
            @ApiResponse(responseCode = "400", description = "Invalid Request Param",  content = { @Content() }),
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

    @Operation(summary = "Update", description = "Update Content", tags = { "thread" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated content", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Thread not found / You are not owner", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @PutMapping(path = "/content")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateContent(@RequestParam UUID id, @RequestParam String content) {
        threadService.updateContent(id, content);
    }

    @Operation(summary = "Update", description = "Update Closed", tags = { "thread" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated closed status", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Thread not found / You are not owner", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @PutMapping(path = "/closed")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateClosed(@RequestParam UUID id, @RequestParam boolean closed) {
        threadService.updateClosed(id, closed);
    }

    @Operation(summary = "Delete", description = "Delete Thread", tags = { "thread" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted thread", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Thread not found / You are not owner", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @DeleteMapping(path = "/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteThread(@RequestParam UUID id) {
        threadService.deleteThread(id);
    }
}
