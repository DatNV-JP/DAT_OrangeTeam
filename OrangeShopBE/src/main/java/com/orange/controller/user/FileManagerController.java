package com.orange.controller.user;

import com.orange.common.payload.Result;
import com.orange.services.impl.FileManagerServiceIplm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("files")
@RequiredArgsConstructor
public class FileManagerController {
    private final FileManagerServiceIplm fileManagerService;
    @GetMapping("{folder}/{file}")
    public byte[] download (@PathVariable("folder") String folder, @PathVariable("file") String file) {
        return fileManagerService.read(folder, file);
    }

    @PostMapping("upload-avatar")
    public Result<?> uploadAvatar(@RequestBody(required = true) MultipartFile avatar){
        String nameFile = fileManagerService.save("avatar", avatar);
        return Result.result(HttpStatus.OK.value(),"Đã xác thưc thành công",nameFile);
    }

    @PostMapping("upload/{folder}")
    public Result<?> uploadFile(@PathVariable("folder") String folder, @RequestBody(required = true) MultipartFile file){
        String nameFile = fileManagerService.save(folder, file);
        return Result.result(HttpStatus.OK.value(),"Đã xác thưc thành công", nameFile);
    }

    @PostMapping(value = "upload-files", consumes = "multipart/form-data")
    public Result<?> uploadFiles(HttpServletRequest request) {
        List<MultipartFile> files = new ArrayList<>();
        int i = 0;
        MultipartFile file = null;
        do {
            file = ((MultipartHttpServletRequest) request).getFile("file["+i+"]");
            if (file != null) {
                files.add(file);
            }
            i++;
        }
        while (file != null);
        if (files.isEmpty()) {
            Result.result(101, "Tải file thất bại", null);
        }
        List<String> nameFile = fileManagerService.save("productImage", files);
        return Result.result(HttpStatus.OK.value(), "Đã tải file thành công", nameFile);
    }

    @DeleteMapping("{folder}/{file}")
    public Result<?> removeFile (@PathVariable("folder") String folder, @PathVariable("file") String file) {
        fileManagerService.delete(folder, file);
        return Result.result(201, "Xoá file thành công", null);
    }

}
