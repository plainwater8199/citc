package com.citc.nce.im.broadcast;

import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.im.broadcast.node.AbstractNode;
import com.citc.nce.im.broadcast.utils.BroadcastPlanUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author jcrenc
 * @since 2024/4/10 14:23
 */
@SpringBootTest
public class BroadcastServiceTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedissonClient redissonClient;

    @BeforeEach
    public void init() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    @Test
    void convertToTreeTest() throws JsonProcessingException {
        List<AbstractNode> tree = BroadcastPlanUtils.getNodeTreeByPlanId(1792805917330305026L);

        Map<MsgTypeEnum, Map<MsgSubTypeEnum, Integer>> resourceRequirements = new HashMap<>();
        BroadcastPlanUtils.applyExecutableNode(tree, node -> {
            Map<MsgSubTypeEnum, Integer> map = resourceRequirements.computeIfAbsent(node.getType(), k -> new HashMap<>());
            map.put(node.getSubType(), map.getOrDefault(node.getSubType(), 0) + node.getMaxSendNumber());
            return true;
        });
        System.out.println(resourceRequirements);
        System.out.println("tree size : " + tree.size());
    }

    @Test
    void getAvailableNodesTest() throws JsonProcessingException {
        List<Long> list = Arrays.asList(
                2235L,//部分成功
                2231L,//全部成功
                2234L//全部失败
        );
    }

    @Test
    void lockTest() throws InterruptedException {
        RLock lock = redissonClient.getLock("test-001");
        Thread a = new Thread(new Task(lock), "a");
        Thread b = new Thread(new Task(lock), "b");
        a.start();
        b.start();
        Thread.sleep(20000);
    }

    @Test
    void tryLockTest() throws InterruptedException {
        RLock lock = redissonClient.getLock("test-001");
        Thread a = new Thread(new TryLockTask(lock), "a");
        Thread b = new Thread(new TryLockTask(lock), "b");
        a.start();
        b.start();
        Thread.sleep(30000);
    }


    static class Task implements Runnable {
        private final RLock lock;

        public Task(RLock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                lock.lock(); //使用lock 将会一直尝试直到获取到锁
                System.out.printf("线程%s获取锁成功%n", Thread.currentThread().getName());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.printf("线程%s执行完毕，释放锁%n", Thread.currentThread().getName());
//                if (lock.isLocked())
                lock.unlock();
            }
        }
    }

    static class TryLockTask implements Runnable {
        private final RLock lock;

        public TryLockTask(RLock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                if (lock.tryLock(5, TimeUnit.SECONDS)) {
                    System.out.printf("线程%s获取锁成功%n", Thread.currentThread().getName());
                    Thread.sleep(6000); //睡眠时间大于获取锁的超时时间，其它尝试获取锁的线程一定会超时失败
                } else {
                    System.out.printf("线程%s执行完毕，获取锁失败，放弃执行%n", Thread.currentThread().getName());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                //使用tryLock获取锁一定要加此判断，因为可能到了超时时间获取锁失败，当前线程没有获取锁就不能够去释放
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    System.out.printf("线程%s执行完毕，释放锁%n", Thread.currentThread().getName());
                }
            }
        }
    }
}