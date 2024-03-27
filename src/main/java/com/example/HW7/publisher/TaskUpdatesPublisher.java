package com.example.HW7.publisher;

import com.example.HW7.data.dto.TaskDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class TaskUpdatesPublisher {

    private final Sinks.Many<TaskDto> itemModelUpdateSink;

    public TaskUpdatesPublisher() {
        this.itemModelUpdateSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public void publish(TaskDto taskDto) {
        itemModelUpdateSink.tryEmitNext(taskDto);
    }

    public Sinks.Many<TaskDto> getUpdateSinks() {
        return itemModelUpdateSink;
    }
}
