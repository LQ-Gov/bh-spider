package com.bh.spider.ui.controller;


import com.bh.spider.client.Client;
import com.bh.spider.ui.entity.Profile;
import com.bh.spider.ui.vo.NodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/node")
public class NodeController {
    private final Client client;

    @Autowired
    public NodeController(Client client) {
        this.client = client;
    }


    @GetMapping("/profile")
    public Profile profile() {
        return new Profile(client.profile(),
                client.nodes().stream().map(NodeVo::new).collect(Collectors.toList()));
    }


}
