package com.polzzak.test;

import com.polzzak.support.test.ControllerTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
class TestControllerTest extends ControllerTestHelper {

    @Test
    public void example() throws Exception {
        mockMvc.perform(
                get("/{path}/rest", "path_value")
                    .param("param", "123")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andDo(
                document("example-get",
                    pathParameters(
                        parameterWithName("path").description("path 값")
                    ),
                    queryParameters(parameterWithName("param").description("param")),
                    responseFields(
                        fieldWithPath("path").description("path"),
                        fieldWithPath("dir").description("dir"),
                        fieldWithPath("param").description("param")
                    )
                )
            );
    }
}
