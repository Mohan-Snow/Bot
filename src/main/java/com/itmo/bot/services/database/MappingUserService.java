package com.itmo.bot.services.database;

import com.itmo.bot.entities.User;
import com.itmo.bot.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MappingUserService implements Dao<User> {

    @Autowired
    private UserRepo userRepo;

    @Override
    public Optional<User> get(long id) {
        return Optional.empty();
    }

    @Override
    public List<User> getAll() {
        List<User> list = new ArrayList<>();

        for (User u : userRepo.findAll()) {
            list.add(u);
        }
        return list;
    }

    @Override
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public void update(User user, String[] params) {

    }

    @Override
    public void delete(User user) {
        userRepo.delete(user);
    }
}
