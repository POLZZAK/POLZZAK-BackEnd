package com.polzzak.domain.mission.controller;

import static com.polzzak.support.MissionFixtures.*;
import static com.polzzak.support.TokenFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.polzzak.domain.stampboard.controller.MissionController;
import com.polzzak.domain.stampboard.service.StampBoardService;
import com.polzzak.domain.user.service.UserService;
import com.polzzak.support.test.ControllerTestHelper;

@WebMvcTest(MissionController.class)
class MissionControllerTest extends ControllerTestHelper {

	@MockBean
	private UserService userService;
	@MockBean
	private StampBoardService stampBoardService;

	private static final String BASE_URL = "/api/v1/missions";

	@BeforeEach
	public void setup() {

	}



}
