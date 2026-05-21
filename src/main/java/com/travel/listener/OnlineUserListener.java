package com.travel.listener;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class OnlineUserListener implements HttpSessionListener {

    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        int count = onlineCount.incrementAndGet();
        event.getSession().getServletContext().setAttribute("onlineUserCount", count);
        log.info("用户上线 | 当前在线: {}", count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        int count = onlineCount.decrementAndGet();
        event.getSession().getServletContext().setAttribute("onlineUserCount", count);
        log.info("用户下线 | 当前在线: {}", count);
    }

    public static int getOnlineCount() {
        return onlineCount.get();
    }
}
