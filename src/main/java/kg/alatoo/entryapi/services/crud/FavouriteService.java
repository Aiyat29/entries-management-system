package kg.alatoo.entryapi.services.crud;

import kg.alatoo.entryapi.dto.EntryDTO;

import java.util.List;

public interface FavouriteService {
    List<EntryDTO> getUsersFavouriteEntries();

    EntryDTO setFavouriteEntry(Long id);
    EntryDTO deleteFavouriteEntry(Long id);
}
