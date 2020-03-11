package com.deferred.example.hammertime.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

@RestController
public class HammerController {

  @GetMapping("/hammer/thor")
  public String hammer() {
    return callSleepingDragon();
  }

  @GetMapping("/hammer/callable/thor")
  public Callable<String> hammerCall() {
    System.out.println("A thread has been created id: " + Thread.currentThread().getName());
    Callable<String> callable =
        () -> {
          System.out.println("The thread in callable is id: " + Thread.currentThread().getName());
          return callSleepingDragon();
        };
    System.out.println(
        "The thread returns to where it came from id: " + Thread.currentThread().getName());
    return callable;
  }

  @GetMapping("/hammer/deferred/thor")
  public DeferredResult<String> hammerDeferred() {
    System.out.println("A thread has been created id: " + Thread.currentThread().getName());
    DeferredResult<String> deferredResult = new DeferredResult<>();
    Callable<String> callable =
        () -> {
          try {
            System.out.println("The thread in deferred is id: " + Thread.currentThread().getName());
            deferredResult.setResult(callSleepingDragon());
            return null;
          } catch (Exception e) {
            deferredResult.setErrorResult("Blaaaaaah!!!");
            return null;
          }
        };
    ForkJoinPool.commonPool().submit(callable);
    System.out.println(
        "The thread returns to where it came from id: " + Thread.currentThread().getName());
    return deferredResult;
  }

  private String callSleepingDragon() {
    String url = "http://localhost:8090/sleep/1000";
    new RestTemplate().getForObject(url, Boolean.TYPE);
    return "May the hammer of Thor fall upon they face!";
  }
}
