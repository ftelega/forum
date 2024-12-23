package ft.projects.forum.controller;

import ft.projects.forum.exception.ForumExceptionResponse;
import ft.projects.forum.model.ForumCommentRequest;
import ft.projects.forum.model.ForumCommentResponse;
import ft.projects.forum.service.ForumCommentService;
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
@RequestMapping(path = "/api/comments")
@RequiredArgsConstructor
@Tag(name = "comment", description = "Comment API")
public class ForumCommentController {

    private final ForumCommentService commentService;

    @Operation(summary = "Create", description = "Create Comment", tags = { "comment" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully created", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Invalid Request Body", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @PostMapping(path = "/create")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createComment(@RequestBody ForumCommentRequest commentRequest) {
        commentService.createComment(commentRequest);
    }

    @Operation(summary = "Get", description = "Get Comments For Thread", tags = { "comment" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched comments for thread", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ForumCommentResponse.class))) }),
            @ApiResponse(responseCode = "400", description = "Invalid Request Param", content = { @Content() }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @GetMapping
    public List<ForumCommentResponse> getCommentsForThread(@RequestParam(name = "id") UUID threadId) {
        return commentService.getCommentsForThread(threadId);
    }

    @Operation(summary = "Update", description = "Update Content", tags = { "comment" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated content", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Comment not found / You are not owner", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @PutMapping(path = "/content")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateContent(@RequestParam(name = "id") UUID commentId, @RequestParam String content) {
        commentService.updateContent(commentId, content);
    }

    @Operation(summary = "Delete", description = "Delete Comment", tags = { "comment" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted comment", content = { @Content() }),
            @ApiResponse(responseCode = "400", description = "Comment not found / You are not owner", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ForumExceptionResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid JWT Authentication", content = {  @Content() })
        }
    )
    @SecurityRequirement(name = "JwtAuth")
    @DeleteMapping(path = "/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@RequestParam(name = "id") UUID commentId) {
        commentService.deleteComment(commentId);
    }
}
