package kg.alatoo.entryapi.services.crud.iml;


import kg.alatoo.entryapi.dto.EntryDTO;
import kg.alatoo.entryapi.entities.Entry;
import kg.alatoo.entryapi.entities.User;
import kg.alatoo.entryapi.exceptions.ApiException;
import kg.alatoo.entryapi.mappers.EntryMapper;
import kg.alatoo.entryapi.repositories.EntryRepository;
import kg.alatoo.entryapi.services.crud.EntryService;
import kg.alatoo.entryapi.services.crud.UserService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntryServiceJPA implements EntryService {
    private final EntryRepository entryRepository;
    private final EntryMapper entryMapper;
    private final UserService userService;

    public EntryServiceJPA(EntryRepository entryRepository, EntryMapper entryMapper, UserService userService) {
        this.entryRepository = entryRepository;
        this.entryMapper = entryMapper;
        this.userService = userService;
    }

    @Override
    public List<EntryDTO> findAllEntries() {
        List<Entry> entries = entryRepository.findAll();
        return entries.stream()
                .map(entryMapper::entryToEntryDto)
                .collect(Collectors.toList());
    }

    @Override
    public EntryDTO findEntryById(Long id) {
        Optional<Entry> optionalEntry = entryRepository.findById(id);
        Entry entry = optionalEntry.orElseThrow(() -> new ApiException("Entry not found with id: " + id, HttpStatusCode.valueOf(409)));
        return entryMapper.entryToEntryDto(entry);
    }

    @Override
    public EntryDTO updateEntry(Long id, EntryDTO dto) {
        User user = userService.getCurrentUser();
        if (!entryRepository.existsByEntryIdAndCreatedById(id, user.getId())) {
            throw new ApiException("Entry not found with id: " + id, HttpStatusCode.valueOf(409));
        }
        Optional<Entry> optionalEntry = entryRepository.findById(id);
        Entry entry = optionalEntry.orElseThrow(() -> new ApiException("Entry not found with id: " + id, HttpStatusCode.valueOf(409)));
        if (dto.getContent() != null) {
            entry.setContent(dto.getContent());
        }
        if (dto.getName() != null) {
            entry.setName(dto.getName());
        }
        entry.setUpdatedAt(LocalDateTime.now());

        Entry savedEntry = entryRepository.save(entry);
        return entryMapper.entryToEntryDto(savedEntry);
    }

    @Override
    public EntryDTO findUsersEntryById(Long id) {
        User user = userService.getCurrentUser();
        Optional<Entry> optionalEntry = entryRepository.findByEntryIdAndCreatedById(id, user.getId());
        Entry entry = optionalEntry.orElseThrow(() -> new ApiException("Entry not found with id: " + id, HttpStatusCode.valueOf(409)));
        return entryMapper.entryToEntryDto(entry);
    }

    @Override
    public EntryDTO saveEntry(EntryDTO dto) {
        User user = userService.getCurrentUser();
        Entry entry = entryMapper.entryDtoToEntry(dto);
        entry.setCreatedBy(user);
        entry.setUpdatedAt(LocalDateTime.now());
        Entry savedEntry = entryRepository.save(entry);
        return entryMapper.entryToEntryDto(savedEntry);
    }

    @Override
    public void deleteEntry(Long id) {
        User user = userService.getCurrentUser();
        if (!entryRepository.existsByEntryIdAndCreatedById(id, user.getId())) {
            throw new ApiException("Entry not found with id: " + id, HttpStatusCode.valueOf(409));
        }
        entryRepository.deleteById(id);
    }

    @Override
    public void deleteEntryById(Long id) {
        if (!entryRepository.existsById(id)) {
            throw new ApiException("Entry not found with id: " + id, HttpStatusCode.valueOf(409));
        }
        entryRepository.deleteById(id);
    }

    @Override
    public List<EntryDTO> findUsersEntries() {
        User user = userService.getCurrentUser();
        List<Entry> entries = entryRepository.findByCreatedById(user.getId());
        return entries.stream()
                .map(entryMapper::entryToEntryDto)
                .collect(Collectors.toList());
    }
}
