package com.example.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.dto.DashboardResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardSseService {

    private final DashboardService dashboardService;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private final TaskScheduler scheduler = new ConcurrentTaskScheduler();
    private ScheduledFuture<?> scheduledTask;
    private long refreshInterval = 5000; // milliseconds
    private boolean isRefreshing = true;

    public SseEmitter registerEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // send initial snapshot
        try {
            DashboardResponse snapshot = dashboardService.getDashboard();
            emitter.send(SseEmitter.event().name("dashboard").data(snapshot));
        } catch (IOException ex) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void manualPublish() {
        // Always publish regardless of isRefreshing
        publishToEmitters(dashboardService.getDashboard());
    }

    private void publishUpdates() {
        if (!isRefreshing || emitters.isEmpty()) return;
        publishToEmitters(dashboardService.getDashboard());
    }

    private void publishToEmitters(DashboardResponse data) {
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("dashboard").data(data));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        if (!dead.isEmpty()) {
            emitters.removeAll(dead);
        }
    }

    @PostConstruct
    public void init() {
        scheduleTask();
    }

    private void scheduleTask() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
        if (isRefreshing) {
            scheduledTask = scheduler.scheduleAtFixedRate(this::publishUpdates, refreshInterval);
        }
    }

    public void setRefreshInterval(long seconds) {
        this.refreshInterval = seconds * 1000L;
        scheduleTask();
    }

    public void setRefreshing(boolean enabled) {
        this.isRefreshing = enabled;
        scheduleTask();
    }

    public int getConnectedClientCount() {
        return emitters.size();
    }
}
