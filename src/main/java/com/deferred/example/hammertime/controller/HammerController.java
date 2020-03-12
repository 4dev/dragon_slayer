package com.deferred.example.hammertime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RestController
public class HammerController extends AbstractController {

  private final RestTemplate restTemplate;

  public HammerController(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @GetMapping("/hammer/thor")
  public String hammer() {
    return callSleepingDragon();
  }

  @GetMapping("/hammer/callable/thor")
  public Callable<String> hammerCall() {
    return this::callSleepingDragon;
  }

  @GetMapping("/hammer/deferred/thor")
  public DeferredResult<String> hammerDeferred() {
    DeferredResult<String> deferredResult = new DeferredResult<>();
    CompletableFuture.supplyAsync(this::callSleepingDragon)
        .whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
    return deferredResult;
  }

  @GetMapping("/hammer/callable/deferred/thor")
  public DeferredResult<ResponseEntity<String>> hammerCallableDeferred() {
    return executeDeferred(() -> ResponseEntity.ok(callSleepingDragon()));
  }

  private String callSleepingDragon() {
    restTemplate.getForObject("http://localhost:8090/sleep/1000", Boolean.TYPE);
    return "May the hammer of Thor fall upon they face!";
  }
}
