package kg.alatoo.entryapi.bootstrap;

import kg.alatoo.entryapi.entities.Entry;
import kg.alatoo.entryapi.entities.Role;
import kg.alatoo.entryapi.entities.User;
import kg.alatoo.entryapi.repositories.EntryRepository;
import kg.alatoo.entryapi.repositories.RoleRepository;
import kg.alatoo.entryapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class InitData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EntryRepository entryRepository;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole;
        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        } else {
            adminRole = roleRepository.findByName("ROLE_ADMIN");
        }
        Role userRole;
        if (roleRepository.findByName("ROLE_USER") == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        } else {
            userRole = roleRepository.findByName("ROLE_USER");
        }

        User admin = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(adminRole, userRole))
                .enabled(true)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .username("user")
                .email("user@gmail.com")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(userRole))
                .enabled(true)
                .build();
        userRepository.save(user);

        // Adding 5 more users with 5 entries each including latitude and longitude
        for (int i = 1; i <= 5; i++) {
            User newUser = User.builder()
                    .username("user" + i)
                    .email("user" + i + "@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .roles(Set.of(userRole))
                    .enabled(true)
                    .build();
            userRepository.save(newUser);

            // Adding 5 entries for each user with latitude and longitude
            for (int j = 1; j <= 5; j++) {
                double latitude = 40.0 + (Math.random() * 10); // Random latitude between 40 and 50
                double longitude = -80.0 + (Math.random() * 10); // Random longitude between -80 and -70

                Entry entry = Entry.builder()
                        .createdBy(newUser)
                        .name("Entry " + j + " for user" + i)
                        .content("This is the content for entry " + j + " of user" + i)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();
                entryRepository.save(entry);
            }
        }
    }
}
