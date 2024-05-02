package net.wuxianjie.rabbitmqproducer.sweepdirty;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class RabbitmqScheduler {

    private final RabbitmqClient rabbitmqClient;

//    @Scheduled(fixedRate = 1000 * 60)
    @GetMapping("/sweep-dirty-queues")
    public List<RabbitmqQueue> sweepDirtyQueues() {
        var queues = rabbitmqClient.getAllQueues();
        queues.stream()
                .filter(RabbitmqQueue::isDirty)
                .forEach(queue -> {
                    log.info("Queue {} is dirty, sweeping...", queue.getName());
                });
        return queues;
    }

}