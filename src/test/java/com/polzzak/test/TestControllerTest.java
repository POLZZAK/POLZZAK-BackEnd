package com.polzzak.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(TestController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class TestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void example() throws Exception {
        mockMvc.perform(
                        get("/{path}/rest", "path_value")
                                .param("param", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(
                        document("example-get",
                                pathParameters(
                                        parameterWithName("path").description("path 값")
                                ),
                                queryParameters(parameterWithName("param").description("param")),
                                responseFields( // response 필드 정보 입력
                                        fieldWithPath("path").description("path"),
                                        fieldWithPath("param").description("param")
                                )
                        )
                )
        ;
    }
}