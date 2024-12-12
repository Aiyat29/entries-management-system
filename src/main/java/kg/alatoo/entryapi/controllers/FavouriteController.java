package kg.alatoo.entryapi.controllers;

import kg.alatoo.entryapi.dto.EntryDTO;
import kg.alatoo.entryapi.services.crud.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favourites")
@RequiredArgsConstructor
public class FavouriteController {
    private final FavouriteService favouriteService;

    private final String ID_PATH = "/{id}";

    @GetMapping
    public List<EntryDTO> getFavourites() {
        return favouriteService.getUsersFavouriteEntries();
    }

    @PostMapping(ID_PATH)
    public EntryDTO setFavouriteEntry(@PathVariable Long id) {
        return favouriteService.setFavouriteEntry(id);
    }

    @DeleteMapping(ID_PATH)
    public EntryDTO deleteFavouriteEntry(@PathVariable Long id) {
        return favouriteService.deleteFavouriteEntry(id);
    }

}
