package com.charles.spider.scheduler.config;

import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lq on 17-3-28.
 */
public class XmlRuleConfig {
    private String path = null;
    public XmlRuleConfig(String path) throws FileNotFoundException, DocumentException {
        this.path = path;
        InputStream stream = XmlRuleConfig.class.getClassLoader().getResourceAsStream(path);

        SAXReader sax = new SAXReader();
        Document doc = sax.read(stream);
        parse(doc);
    }

    public void parse(Document doc) throws DocumentException {
        Element root = doc.getRootElement();

        if (root == null || !"rules".equals(root.getName()))
            throw new DocumentException("not find root element with rules");

        chainsParser(root.element("chains"));
        List proxies = root.elements("proxies");
        if (proxies != null)
            proxiesParser(proxies);
    }

    private void chainsParser(Element el) {
        if (el == null) return;

        for (Element it : (List<Element>)el.elements("chain")) {
            String pattern = it.attributeValue("pattern");
            String scope = it.attributeValue("scope");
            Chain.Value prepare = null, finished = null;
            Map<Integer, Chain.Value> handlers = new HashMap<>();
            if (it.attributeValue("modules") != null)
                handlers.put(HttpStatus.SC_OK, new Chain.Value(scope, it.attributeValue("modules")));

            List<Element> items = it.elements("item");
            if (items != null) {
                for (Element item : items) {
                    Chain.Value val = new Chain.Value(item.attributeValue("scope", "soft"), item.attributeValue("value", item.getText()));
                    if ("prepare".equals(item.getName()))
                        prepare = val;
                    else if ("finished".equals(item.getName()))
                        finished = val;
                    else
                        handlers.put(Integer.parseInt(item.attributeValue("key")), val);
                }
            }
            Config.defaultChains.put(pattern, new Chain(pattern, scope, handlers, prepare, finished));
        }
    }

    private void proxiesParser(List<Element> list){}
}
