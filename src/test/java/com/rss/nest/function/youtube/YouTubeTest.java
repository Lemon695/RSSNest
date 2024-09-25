package com.rss.nest.function.youtube;

import com.rss.nest.utils.http.OkHttpClientUtil;
import okhttp3.Headers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author Lemon695
 * @date: 2024/9/25 上午10:04:10
 * @description:
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest
public class YouTubeTest {

    @Autowired
    private OkHttpClientUtil okHttpClientUtil;

    /**
     * 测试请求YouTube博主-视频
     *
     * @return: void
     * @author Lemon695
     * @date 2024/9/25 上午10:11
     */
    @Test
    public void testYouTubeV01() {
        String url = "https://www.youtube.com/@gingerbread0529/videos";

        Headers headers = new Headers.Builder().build();

        String response = okHttpClientUtil.doGet(url, headers);
        assertNotNull(response);
        System.out.println(response);
    }
}
