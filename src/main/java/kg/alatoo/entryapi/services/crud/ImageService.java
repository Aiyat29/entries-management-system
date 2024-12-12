package kg.alatoo.entryapi.services.crud;

import kg.alatoo.entryapi.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    ImageDTO saveImageToEntry(Long id, MultipartFile file) throws IOException;

    ImageDTO saveImageToUser(MultipartFile file) throws IOException;

    MultipartFile getImage(Long imageId) throws IOException;

    MultipartFile getProfileImage() throws IOException;

    List<ImageDTO> getAllImagesOfEntry(Long id);

    void deleteProfileImageOfUser();
}
