#Spring Boot 1.5.9.RELEASE版本用户手册下载程序
本示例演示Spring Boot 1.5.9.RELEASE版本的用户手册下载
####pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.nihaorz</groupId>
    <artifactId>spring-boot-reference-guide-offline-html</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.11.2</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>
    </dependencies>

</project>
```



#### Application.java

```java
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
        String storePath = "C:\\work\\IDEA_WS\\spring-boot-reference-guide-offline-html\\src\\main\\doc";
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

    /**
     * 保存html文件
     *
     * @param storePath
     * @param urlPath
     * @param html
     * @throws IOException
     */
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

```



> 运行main函数会在C:\work\IDEA_WS\spring-boot-reference-guide-offline-html\src\main\doc目录下生成html文件

将以下脚本批量为空字符串

```javascript
<script>if(window.parent==window){(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)})(window,document,'script','//www.google-analytics.com/analytics.js','ga');ga('create','UA-2728886-23','auto',{'siteSpeedSampleRate':100});ga('send','pageview');}</script>
```

然后将html文件放到tomcat ROOT应用下访问index.html，使用Fiddler抓包会有如下几个文件404：

> highlight.css
>
> manual.css
>
> manual-multipage.css
>
> background.png
>
> important.png
>
> note.png
>
> tip.png
>
> warning.png
>
> email-decode.min.js

他们的真是下载地址如下：

> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/css/highlight.css>
>
> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/css/manual.css>
>
> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/css/manual-multipage.css>
>
> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/images/background.png>
>
> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/images/important.png>
>
> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/images/note.png>
>
> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/images/tip.png>
>
> <https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/html/images/warning.png>
>
> <https://docs.spring.io/cdn-cgi/scripts/d07b1474/cloudflare-static/email-decode.min.js>

将上面的文件下载下来依次放到html下的css、images、js目录下

在上面的html文件中全局搜索email-decode.min.js并替换为相对路径，然后一个新鲜离线版用户手册就诞生了，从tomcat中拿出来邮件浏览器打开即可使用，还不带google统计，跟官方在线版本简直一毛一样啊。