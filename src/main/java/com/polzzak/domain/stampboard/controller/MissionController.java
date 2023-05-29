package com.polzzak.domain.stampboard.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polzzak.domain.stampboard.service.StampBoardService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/missions")
public class MissionController {

	private final StampBoardService stampBoardService;

}
