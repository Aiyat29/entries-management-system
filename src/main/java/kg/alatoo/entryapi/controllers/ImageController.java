package kg.alatoo.entryapi.controllers;


import kg.alatoo.entryapi.dto.ImageDTO;
import kg.alatoo.entryapi.services.crud.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
    private final String ID_PATH = "/{id}";
    private final String USER_PATH = "/user";
    private final String ENTRY_PATH = "/entry";

    private final ImageService imageService;

    @PostMapping(ENTRY_PATH + ID_PATH)
    public ImageDTO saveImageToEntry(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return imageService.saveImageToEntry(id, file);
    }

    @GetMapping(ENTRY_PATH + ID_PATH)
    public List<ImageDTO> getAllImagesOfEntry(@PathVariable Long id) {
        return imageService.getAllImagesOfEntry(id);
    }

    @PostMapping(USER_PATH)
    public ImageDTO saveImageToUser(@RequestParam("file") MultipartFile file) throws IOException {
        return imageService.saveImageToUser(file);
    }

    @DeleteMapping(USER_PATH)
    public void deleteUsersImage() {
        imageService.deleteProfileImageOfUser();
    }

    @GetMapping(USER_PATH)
    public ResponseEntity<?> getProfileImage() throws IOException {
        try {
            MultipartFile multipartFile = imageService.getProfileImage();
            if (multipartFile == null) {
                return null;
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + multipartFile.getOriginalFilename() + "\"")
                    .contentType(MediaType.valueOf(multipartFile.getContentType()))
                    .body(multipartFile.getBytes());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching the image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<?> getImage(@PathVariable Long id) {
        try {
            MultipartFile multipartFile = imageService.getImage(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + multipartFile.getOriginalFilename() + "\"")
                    .contentType(MediaType.valueOf(multipartFile.getContentType()))
                    .body(multipartFile.getBytes());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching the image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
