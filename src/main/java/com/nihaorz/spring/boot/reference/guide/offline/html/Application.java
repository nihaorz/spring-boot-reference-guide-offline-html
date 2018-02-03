package com.nihaorz.spring.boot.reference.guide.offline.html;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Nihaorz
 */
public class Application {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * doc站点地址
     */
    private static final String DOC_URL = "https://docs.spring.io/spring-boot/docs/";

    /**
     * doc起始页
     */
    private static final String HTML_URI = "/reference/html/";

    /**
     * URL分隔符
     */
    private static final String URL_SEPARATOR = "/";

    /**
     * html页面结束标记
     */
    private static final String HTML_END_STR = ".html";

    public static void main(String[] args) throws IOException {
        String storePath = "C:\\work\\IDEA_WS\\OperationCenter_WS\\spring-boot-doc-download\\src\\main\\doc\\1.5.9.RELEASE\\reference\\html";
        String version = "1.5.9.RELEASE";
        generateHtmlDoc(storePath, version);
    }

    /**
     * 生成文档
     *
     * @param storePath
     * @param version
     */
    private static void generateHtmlDoc(String storePath, String version) throws IOException {
        String indexPath = DOC_URL + version + HTML_URI;
        String html = IOUtils.toString(new URL(indexPath), CHARSET);
        saveHtml(storePath, indexPath, html);
        Document document = Jsoup.parse(html);
        Elements elements = new Elements();
        elements.addAll(document.select("span.chapter>a"));
        elements.addAll(document.select("span.part>a"));
        elements.addAll(document.select("span.appendix>a"));
        String urlPath;
        System.out.println("共解析得到" + elements.size() + "个子页面");
        for (Element element : elements) {
            urlPath = element.attr("href");
            if (urlPath != null && !"".equals(urlPath.trim())) {
                if (!urlPath.startsWith("http")) {
                    urlPath = indexPath + urlPath;
                }
            }
            html = IOUtils.toString(new URL(urlPath), CHARSET);
            saveHtml(storePath, urlPath, html);
        }
    }

    private static void saveHtml(String storePath, String urlPath, String html) throws IOException {
        if (!urlPath.contains(".html#")) {
            if (!urlPath.endsWith(HTML_END_STR)) {
                if (urlPath.endsWith(URL_SEPARATOR)) {
                    urlPath = urlPath + "index" + HTML_END_STR;
                } else {
                    urlPath = urlPath + URL_SEPARATOR + "index" + HTML_END_STR;
                }
            }
            urlPath = urlPath.substring(DOC_URL.length(), urlPath.length());
            String localPath = storePath + File.separator + urlPath.replace(URL_SEPARATOR, File.separator);
            File file = new File(localPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileUtils.write(file, html, CHARSET);
            System.out.println("保存" + file.getAbsolutePath() + "成功");
        }
    }

}
