package com.charles.spider.scheduler.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

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

        List chains = root.elements("chains");
        if (chains != null)
            chainsParser(chains);
        List timers = root.elements("timers");
        if (timers != null)
            timersParser(timers);
        List proxies = root.elements("proxies");
        if (proxies != null)
            proxysParser(proxies);
    }

    private void chainsParser(List<Element> list){}

    private void timersParser(List<Element> list){}

    private void proxysParser(List<Element> list){}
}
