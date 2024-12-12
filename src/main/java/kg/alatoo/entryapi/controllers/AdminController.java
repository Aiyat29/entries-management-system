package kg.alatoo.entryapi.controllers;

import kg.alatoo.entryapi.dto.EntryDTO;
import kg.alatoo.entryapi.dto.UserDTO;
import kg.alatoo.entryapi.services.crud.EntryService;
import kg.alatoo.entryapi.services.crud.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final EntryService entryService;
    private final UserService userService;

    private final String ENTRY_PATH = "/entries";
    private final String USER_PATH = "/users";
    private final String ID_PATH = "/{id}";

    @GetMapping(ENTRY_PATH)
    public List<EntryDTO> getAllEntries() {
        return entryService.findAllEntries();
    }

    @GetMapping(USER_PATH)
    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping(USER_PATH + ID_PATH)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @DeleteMapping(ENTRY_PATH + ID_PATH)
    public void deleteEntry(@PathVariable Long id) {
        entryService.deleteEntryById(id);
    }

}
