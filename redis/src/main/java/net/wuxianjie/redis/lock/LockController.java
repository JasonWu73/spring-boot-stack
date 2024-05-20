package net.wuxianjie.redis.lock;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.wuxianjie.myspringbootstarter.util.RedisLockUtils;

@RestController
@RequestMapping("/api/v1")
public class LockController {

    private final static Logger LOG = LoggerFactory.getLogger(LockController.class);
    private static final String LOCK_KEY = "lock:demo";

    @GetMapping("/lock")
    public ResponseEntity<Void> doSyncBiz() {
        new Thread(this::executeSync).start();
        new Thread(this::executeSync).start();
        new Thread(this::executeSync).start();
        return ResponseEntity.noContent().build();
    }

    private void executeSync() {
        LOG.info("[{}] 准备开始执行业务逻辑", Thread.currentThread().getName());

        // 生成锁的唯一值
        String identifier = UUID.randomUUID().toString();

        // 直到获取到锁才能执行
        waitUntilGotLock(identifier);

        // 开始执行业务逻辑
        doBiz(identifier);
    }

    private void waitUntilGotLock(String identifier) {
        while (!RedisLockUtils.lock(LOCK_KEY, identifier)) {
            // 休眠以等待下一次获取锁
            try {
                TimeUnit.MILLISECONDS.sleep(800);
            } catch (InterruptedException e) {
                LOG.warn("获取锁休眠异常：{}", e.getMessage());
            }
        }
    }

    private void doBiz(String identifier) {
        String threadName = Thread.currentThread().getName();
        LOG.info("🔐[{}] 获取锁成功", threadName);
        try {
            LOG.info("[{}] 执行业务逻辑", threadName);
            delay();
            LOG.info("[{}] 完成业务逻辑", threadName);
        } finally {
            RedisLockUtils.unlock(LOCK_KEY, identifier);
            LOG.info("🔓[{}] 解锁成功", threadName);
        }
    }

    private void delay() {
        try {
            // 设置大于 `RedisLockUtils#LOCK_TIMEOUT_SECONDS` 的超时时间，可以验证锁续期逻辑是否正确
            TimeUnit.SECONDS.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
