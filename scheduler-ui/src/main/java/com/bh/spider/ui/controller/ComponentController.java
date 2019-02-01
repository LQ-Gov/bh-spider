package com.bh.spider.ui.controller;

import com.bh.spider.client.Client;
import com.bh.spider.transfer.entity.Component;
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
@RequestMapping("/component")
public class ComponentController {

    private final Client client;

    @Autowired
    public ComponentController(Client client) {
        this.client = client;
    }


    @PostMapping
    public void create(String name,Component.Type type,String description, MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        client.component().submit(name, in, type, description);
    }

    @GetMapping(value = "/list")
    public List<Component> list() {

        List<Component> result = client.component().select(Component.Type.GROOVY);
        return result;

    }


    @DeleteMapping(value = "/{name}")
    public String delete(@PathVariable("name") String name, String hash) {

        Component component = client.component().get(name, Component.Type.GROOVY);
        if (component == null) {
            return null;
            //module不存在
        }

        if (!component.getHash().equals(hash)) {
            return null;
            //版本不一致
        }
        client.component().delete(name, Component.Type.GROOVY);
        return "ok";
    }


}
