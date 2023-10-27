package Homework3_5.controller;
import jakarta.servlet.http.HttpServletResponse;
import Homework3_5.service.AvatarService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService service;

    public AvatarController(AvatarService service) {
        this.service = service;
    }

    @PostMapping("/{studentId}")
    public void upload(@PathVariable long studentId, MultipartFile file) throws IOException {
        service.upload(studentId, file);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<byte[]> find(@PathVariable long studentId) {
        var avatar = service.find(studentId);
        if (avatar != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
            headers.setContentLength(avatar.getData().length);
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/file/{studentId}")
    public void findFile(@PathVariable long studentId, HttpServletResponse response) throws IOException {
        var avatar = service.find(studentId);
        if (avatar == null) {
            response.setStatus(404);
            return;
        }
        File file = new File(avatar.getFilePath());
        if (!file.exists()) {
            response.setStatus(500);
            return;
        }
        response.setContentType(avatar.getMediaType());
        response.setContentLength((int) avatar.getFileSize());
        try (var out = response.getOutputStream();
             var in = new FileInputStream(avatar.getFilePath())) {
            in.transferTo(out);
        }
    }
}

