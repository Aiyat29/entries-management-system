package kg.alatoo.entryapi.controllers;

import kg.alatoo.entryapi.dto.EntryDTO;
import kg.alatoo.entryapi.services.crud.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entries")
@RequiredArgsConstructor
public class EntryController {
    private final EntryService entryService;

    private final String ID_PATH = "/{id}";

    @GetMapping()
    public List<EntryDTO> getUsersEntries() {
        return entryService.findUsersEntries();
    }

    @GetMapping(ID_PATH)
    public EntryDTO getEntryById(@PathVariable Long id) {
        return entryService.findUsersEntryById(id);
    }

    @PostMapping()
    public EntryDTO createEntry(@RequestBody EntryDTO dto) {
        return entryService.saveEntry(dto);
    }

    @PutMapping(ID_PATH)
    public EntryDTO updateEntry(@PathVariable Long id, @RequestBody EntryDTO entryDTO) {
        entryService.findEntryById(id);
        entryDTO.setEntryId(id);
        return entryService.saveEntry(entryDTO);
    }

    @PatchMapping(ID_PATH)
    public EntryDTO updateEntryForPatch(@PathVariable Long id, @RequestBody EntryDTO entryDTO) {
        return entryService.updateEntry(id, entryDTO);
    }

    @DeleteMapping(ID_PATH)
    public void deleteEntry(@PathVariable Long id) {
        entryService.deleteEntry(id);
    }
}
