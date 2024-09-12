package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private ManagerRepository managerRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        ServerException exception = assertThrows(ServerException.class, () -> {
            commentService.saveComment(authUser, todoId, request);
        });

        // then
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void comment를_정상적으로_등록한다() throws AccessDeniedException {
        // given
        long todoId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "title", "contents", user);
        Comment comment = new Comment(request.getContents(), user, todo);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(commentRepository.save(any())).willReturn(comment);

        // any(User.class) 및 any(Todo.class)는 exsitsByUserAndTodo의 인자값으로 어떤 User나 Todo 객체도 받아들이겠다는 의미이다.
        // 만약 그냥 user와 todo를 받게된다면 managerRepository.existsByUserAndTodo(user, todo)를 스텁할 때,
        // Mockito는 user와 todo 객체가 실제 메서드 호출 시와 일치하는지 확인한다.
        // Mockito는 스텁을 설정할 때, 메서드 호출 시의 인자와 스텁 설정 시의 인자가 정확히 같은 객체일 것을 기대한다.
        // 즉, user와 todo가 메서드 호출 시의 인자와 동일한 객체여야 합니다.
        // 만약 스텁 설정 시 사용한 user와 todo 객체와 실제 메서드 호출 시 전달된 user와 todo 객체가 다른 인스턴스일 경우,
        // Mockito는 이를 일치하지 않는다고 보고 PotentialStubbingProblem 예외를 발생시킨다.
        given(managerRepository.existsByUserAndTodo(any(User.class), any(Todo.class))).willReturn(true);

        // when
        CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

        // then
        assertNotNull(result);
    }
}
