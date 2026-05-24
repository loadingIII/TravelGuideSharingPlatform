package com.travel.listener;

import com.travel.mapper.DestinationMapper;
import com.travel.mapper.TagMapper;
import com.travel.pojo.model.Destination;
import com.travel.pojo.model.Tag;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class DataInitListener implements ServletContextListener {

    private ServletContext servletContext;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed(ContextRefreshedEvent event) {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        if (servletContext == null) {
            servletContext = ((org.springframework.web.context.WebApplicationContext)
                    event.getApplicationContext()).getServletContext();
        }
        try {
            TagMapper tagMapper = applicationContext.getBean(TagMapper.class);
            DestinationMapper destinationMapper = applicationContext.getBean(DestinationMapper.class);

            List<Tag> hotTags = tagMapper.selectHotTags();
            servletContext.setAttribute("hotTags", hotTags);
            log.info("初始化热门标签数据: {} 条", hotTags.size());

            List<Destination> hotDestinations = destinationMapper.listHotDestinations(10);
            servletContext.setAttribute("hotDestinations", hotDestinations);
            log.info("初始化热门目的地数据: {} 条", hotDestinations.size());
        } catch (Exception e) {
            log.error("初始化数据失败", e);
            servletContext.setAttribute("hotTags", Collections.emptyList());
            servletContext.setAttribute("hotDestinations", Collections.emptyList());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("应用上下文销毁，清除缓存数据");
    }
}
