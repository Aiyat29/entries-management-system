package kg.alatoo.entryapi.services.crud.iml;

import kg.alatoo.entryapi.dto.ImageDTO;
import kg.alatoo.entryapi.entities.Entry;
import kg.alatoo.entryapi.entities.Image;
import kg.alatoo.entryapi.entities.User;
import kg.alatoo.entryapi.exceptions.ApiException;
import kg.alatoo.entryapi.mappers.ImageMapper;
import kg.alatoo.entryapi.repositories.EntryRepository;
import kg.alatoo.entryapi.repositories.ImageRepository;
import kg.alatoo.entryapi.repositories.UserRepository;
import kg.alatoo.entryapi.services.crud.ImageService;
import kg.alatoo.entryapi.services.crud.UserService;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageServiceJPA implements ImageService {

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;
    private final UserService userService;
    private final ImageMapper imageMapper;
    private final EntryRepository entryRepository;

    public ImageServiceJPA(ImageRepository imageRepository, UserService userService, ImageMapper imageMapper, EntryRepository entryRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.imageMapper = imageMapper;
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
    }

    private Image saveImage(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename; // Ensure unique naming
        FileOutputStream out = new FileOutputStream("images/" + uniqueFilename);

        out.write(file.getBytes());
        Image image = new Image();
        image.setImageUrl("images/" + uniqueFilename);

        return image;
    }

    public ImageDTO saveImageToEntry(Long id, MultipartFile file) throws IOException {
        Image image = saveImage(file);
        User user = userService.getCurrentUser();
        Optional<Entry> optionalEntry = entryRepository.findByEntryIdAndCreatedById(id, user.getId());
        Entry entry = optionalEntry.orElseThrow(() -> new ApiException("Entry not found with id: " + id, HttpStatusCode.valueOf(409)));
        image.setBelongsTo(entry);
        imageRepository.save(image);
        return imageMapper.imageToImageDto(image);
    }

    public ImageDTO saveImageToUser(MultipartFile file) throws IOException {
        Image image = saveImage(file);
        User user = userService.getCurrentUser();
        Image savedImage = imageRepository.save(image);
        user.setProfileImage(image);
        userRepository.save(user);
        return imageMapper.imageToImageDto(savedImage);
    }

    public void deleteProfileImageOfUser() {
        User user = userService.getCurrentUser();
        user.setProfileImage(null);
        userRepository.save(user);
    }

    public MultipartFile getImage(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image with ID " + imageId + " not found"));
        return convertImageToMultipartFile(image);
    }

    public MultipartFile getProfileImage() throws IOException {
        User user = userService.getCurrentUser();
        Image image = user.getProfileImage();
        if (image != null) {
            return convertImageToMultipartFile(image);
        }
        return null;
    }

    public List<ImageDTO> getAllImagesOfEntry(Long id) {
        List<Image> images = imageRepository.findByBelongsToEntryId(id);
        return images.stream()
                .map(imageMapper::imageToImageDto)
                .collect(Collectors.toList());
    }

    private MultipartFile convertImageToMultipartFile(Image image) throws IOException {
        String imagePath = image.getImageUrl();
        File file = new File(imagePath);

        if (!file.exists()) {
            throw new IOException("File not found at path: " + imagePath);
        }

        return convertFileToMultipartFile(file);
    }


    private MultipartFile convertFileToMultipartFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + (file != null ? file.getAbsolutePath() : "null"));
        }

        Path path = file.toPath();
        String name = file.getName();
        String originalFileName = file.getName();
        String contentType = Files.probeContentType(path);
        byte[] content = Files.readAllBytes(path);

        return new MockMultipartFile(name, originalFileName, contentType, content);
    }
}

