package com.example.wenda;

import com.example.wenda.service.LikeService;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=WendaApplication.class)
public class LikeServiceTests {
    @Autowired
    LikeService likeService;
    @Before
    public void SetUp()
    {
        System.out.println("SetUp");
    }
    @After
    public void tearDown()
    {
        System.out.println("tearDown");
    }
    @BeforeClass
    public static void beforeClass()
    {
        System.out.println("beforeclass");
    }
    @AfterClass
    public static void afterClass()
    {
        System.out.println("afterclass");
    }

    @Test
    public void testLike()
    {
        System.out.println("testLike");
        likeService.like(12,1,36);
        Assert.assertEquals(1,likeService.getLikeStatus(12,1,36));
        likeService.disLike(12,1,36);
        Assert.assertEquals(-1,likeService.getLikeStatus(12,1,36));
    }
    @Test
    public void tearXXX()
    {
        System.out.println("xxx");
    }
    @Test(expected=IllegalArgumentException.class)
    public void textException()
    {
        System.out.println("testException");
          throw new IllegalArgumentException("异常发生了");
    }
}
