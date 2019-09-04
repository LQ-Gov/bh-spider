package com.bh.spider.ui.controller;

import com.bh.spider.client.Client;
import com.bh.spider.common.component.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lq on 7/5/17.
 */

@RestController
@RequestMapping("/api/component")
public class ComponentController {

    private final Client client;

    @Autowired
    public ComponentController(Client client) {
        this.client = client;
    }


    @PostMapping
    public void create(String name, Component.Type type, String description, MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        client.component().submit(name, in, type, description);
    }

    @GetMapping(value = "/list")
    public List<Component> list(Component.Type type) {

        return client.component().select(type);
    }


    @DeleteMapping(value = "/{name}")
    public boolean delete(@PathVariable("name") String name, String hash) {

        Component component = client.component().get(name);
        if (component == null || !hash.equals(component.getHash())) {
            return false;
            //module不存在||module hash不一样
        }
        client.component().delete(name);
        return true;
    }

    @GetMapping(value = "/code/{name}")
    public String code(@PathVariable String name) {
        if (name == null) return null;

        Component component = client.component().get(name);
        if (component != null && component.getType() == Component.Type.GROOVY) {
            component = client.component().get(name, true);

            return new String(component.getData());
        }

        return null;

    }
}
