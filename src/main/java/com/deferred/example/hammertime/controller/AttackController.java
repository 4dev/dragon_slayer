package com.deferred.example.hammertime.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class AttackController extends AbstractController {

  private static final int NAP_TIME_IN_MILLISECONDS = 1000;
  private AtomicInteger counter = new AtomicInteger(0);

  private final RestTemplate restTemplate;

  public AttackController(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @GetMapping("/blocking/attack")
  public ResponseEntity<String> attack() {
    return ResponseEntity.ok(callSleepingDragon());
  }

  @GetMapping("/callable/attack")
  public Callable<String> callableAttack() {
    return this::callSleepingDragon;
  }

  @GetMapping("/deferred/attack")
  public DeferredResult<String> deferredAttack() {
    DeferredResult<String> result = new DeferredResult<>();
    new Thread(
            () -> result.setResult(callSleepingDragon()), "MyThread-" + counter.incrementAndGet())
        .start();
    return result;
  }

  @GetMapping("/callable/deferred/attack")
  public DeferredResult<ResponseEntity<String>> callableDeferredAttack() {
    return executeDeferred(() -> ResponseEntity.ok(callSleepingDragon()));
  }

  private String callSleepingDragon() {
    restTemplate.getForObject("http://localhost:8090/sleep/" + NAP_TIME_IN_MILLISECONDS, Boolean.TYPE);
    return "The dragon takes a critical hit and succumbs to its wounds!";
  }
}
