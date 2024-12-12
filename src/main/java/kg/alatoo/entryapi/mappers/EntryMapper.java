package kg.alatoo.entryapi.mappers;


import kg.alatoo.entryapi.entities.Entry;
import kg.alatoo.entryapi.dto.EntryDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EntryMapper {

    @Mapping(source = "createdById", target = "createdBy.id")
    Entry entryDtoToEntry(EntryDTO dto);

    @Mapping(source = "createdBy.id", target = "createdById")
    EntryDTO entryToEntryDto(Entry entry);
}
