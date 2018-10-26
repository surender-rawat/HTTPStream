package com.github.ssr.stream.controller;

import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@RestController
@RequestMapping("/stream")
public class ServiceStreamController {
    
    @PostConstruct
    public void init() {
    
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{file:.+}")
    public ResponseEntity<ResponseBodyEmitter> stream(@PathVariable String file) throws FileNotFoundException {
        System.out.println("called file stream....");
        final ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/event-stream")).body(getServiceData(emitter));
        
    }
    
    @RequestMapping("/test")
    public ResponseEntity<ResponseBodyEmitter> handleRequest() {
        
        final ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    
                    System.out.println("My Index " + i);
                    emitter.send("This is test for ResonseBodyEmitter" + i + " - ", MediaType.TEXT_PLAIN);
                    
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.completeWithError(e);
                    return;
                }
            }
            System.out.println("Thread Executor finished");
            emitter.complete();
        });
        System.out.println("Emitter response returned....");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/event-stream")).body(emitter);
        
    }
    
    private ResponseBodyEmitter getServiceData(ResponseBodyEmitter emitter) {
        
        System.out.println("getServiceData called...");
        
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try {
                System.out.println("getServiceData called...future1");
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println("writing and flushing...future1");
                // emitter.send("Result of Future 1".getBytes());
                emitter.send("Result of Future 1 after 1 sec \n", MediaType.TEXT_PLAIN);
                
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            System.out.println("called file stream....F1 ");
            
            // return "Result of Future 1";
        });
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("getServiceData called...future2");
                TimeUnit.MILLISECONDS.sleep(5000);
                System.out.println("writing and flushing...future2");
                emitter.send("Result of Future 2 after 5 sec \n", MediaType.TEXT_PLAIN);
                
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            System.out.println("called file stream....F2 ");
            return "Result of Future 2";
        }); //
        
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("getServiceData called...future3");
                TimeUnit.MILLISECONDS.sleep(20000);
                System.out.println("writing and flushing...future3");
                emitter.send("Result of Future 3 after 20 sec \n ", MediaType.TEXT_PLAIN);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            System.out.println("called file stream....F3 ");
            return "Result of Future 3";
        });
        
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.execute(() -> {
            try {
                CompletableFuture.allOf(future3, future1, future2).get();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            emitter.complete();
        });
        
        // list.add(future3);
        try {
            // CompletableFuture<Void> result = CompletableFuture.allOf(future1);// CompletableFuture.allOf(future3, future1, future2);
            // result.
            // result.get();
            // emitter.complete();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return emitter;
        
    }
    
}
