package hello.advanced.trace.threadlocal;

import hello.advanced.trace.threadlocal.code.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;

@Slf4j
public class FieldServiceTest {
    private FieldService fieldService = new FieldService();

    @Test
    void field() {
        log.info("main start");
        Runnable userA = () -> {
            fieldService.logic("userA");
        };
        Runnable userB = () -> {
            fieldService.logic("userB");
        };

        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start();
        // sleep(2000); // 동시성 문제 발생 X
        sleep(100); // 동시성 문제 발생 O -> A가 처리되기전 nameStore에 B가 저장되어있어 Thread-A도 userB를 반환받게된다.
        /**
         * 동시성 문제!
         * 여러 쓰레드가 동시에 같은 인스턴스의 필드 값을 변경하면서 발생하는 문제
         * 트래픽이 점점 많아질수록 자주 발생한다.
         * 특히, 스프링 빈처럼 싱글톤 객체의 필드를 변경하며 사용할 때 동시성 문제를 조심하자!
         */
        threadB.start();

        sleep(3000); // 메인 쓰레드 종료 대기
        log.info("main exit");
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
