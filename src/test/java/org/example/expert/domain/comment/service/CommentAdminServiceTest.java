package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;


    // Mocking된 commentRepository를 commentAdminService에 주입한다.
    @InjectMocks
    private CommentAdminService commentAdminService;


    //테스트 메서드를 실행할때마다 Mock을 초기화한다.
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void deleteComment_정상적으로_삭제된다() {
        // given
        long commentId = 1L; // 삭제할 댓글의 ID

        // when
        commentAdminService.deleteComment(commentId);

        // then
        // deleteById가 commentId와 함께 한번 호출되었는지 확인한다.
        verify(commentRepository, times(1)).deleteById(commentId);
    }

}