package com.blackshoe.moongklheremobileapi.controller;


import com.blackshoe.moongklheremobileapi.dto.EnquiryDto;
import com.blackshoe.moongklheremobileapi.dto.NotificationDto;
import com.blackshoe.moongklheremobileapi.dto.PostDto;
import com.blackshoe.moongklheremobileapi.dto.ResponseDto;
import com.blackshoe.moongklheremobileapi.service.StoryService;
import com.blackshoe.moongklheremobileapi.service.UserService;
import com.blackshoe.moongklheremobileapi.sqs.SqsSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moongkl-works")
@RequiredArgsConstructor
public class MoongklWorksController {
    private final UserService userService;
    private final SqsSender sqsSender;

    //get notifications
    @GetMapping("/notification")
    public ResponseEntity<ResponseDto<Page<NotificationDto.NotificationReadResponse>>> getNotification(@RequestParam(defaultValue = "10") Integer size,
                                                                                                       @RequestParam(defaultValue = "0") Integer page) {
        final Page<NotificationDto.NotificationReadResponse> notificationReadResponsePage
                = userService.getNotification(size, page);

        final ResponseDto<Page<NotificationDto.NotificationReadResponse>> responseDto = ResponseDto.<Page<NotificationDto.NotificationReadResponse>>success()
                .payload(notificationReadResponsePage)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //send enquiry
    @PostMapping("/enquiry")
    public ResponseEntity<ResponseDto> sendEnquiry(@RequestBody EnquiryDto.SendEnquiryRequest request) {
        userService.sendEnquiry(request);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.success().build());
    }

}
